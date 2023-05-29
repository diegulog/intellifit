package com.diegulog.intellifit.movenet.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.whenCreated
import com.blautic.cameraxlib.getAspectRatio
import com.blautic.cameraxlib.getAspectRatioString
import com.blautic.cameraxlib.getNameString
import com.diegulog.intellifit.databinding.FragmentCameraBinding
import com.diegulog.intellifit.domain.entity.Device
import com.diegulog.intellifit.domain.entity.Sample
import com.diegulog.intellifit.movenet.ml.ModelType
import com.diegulog.intellifit.movenet.ml.MoveNet
import com.diegulog.intellifit.movenet.ml.PoseDetector
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.utils.VisualizationUtils
import com.diegulog.intellifit.utils.YuvToRgbConverter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.diegulog.intellifit.R
import com.diegulog.intellifit.utils.buildModelIsEmulator

class CameraXFragment : BaseFragment<FragmentCameraBinding>() {

    private var modelType = ModelType.Lightning

    /** Default device is CPU */
    private var device = Device.CPU
    private val lock = Any()
    private var detector: PoseDetector? = null
    private lateinit var imageBitmap: Bitmap
    private lateinit var yuvConverter: YuvToRgbConverter
    private lateinit var cameraExecutor: ExecutorService
    var cameraSourceListener: CameraSourceListener? = null

    private val cameraCapabilities = mutableListOf<CameraCapability>()
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var currentRecording: Recording? = null
    private val cameraIndex: Int = CameraSelector.LENS_FACING_FRONT

    private val qualityIndex: Int by lazy {
        if (buildModelIsEmulator()) {
            0
        } else {
            DEFAULT_QUALITY_IDX
        }
    }
    private var audioEnabled = false

    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(requireContext()) }
    private var enumerationDeferred: Deferred<Unit>? = null
    var recordVideoListener: RecordVideoListener? = null

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initCameraFragment()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.pe_request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        requireActivity().finish()
                    }
                    .create()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        yuvConverter = YuvToRgbConverter(requireContext())
        setDetector(MoveNet.create(requireActivity(), device, modelType))
        if (isCameraPermissionGranted()) {
            initCameraFragment()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    // main cameraX capture functions
    /**
     *   Always bind preview + video capture use case combinations in this sample
     *   (VideoCapture can work on its own). The function should always execute on
     *   the main thread.
     */
    @SuppressLint("RestrictedApi", "UnsafeOptInUsageError")
    private suspend fun bindCaptureUsecase() {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

        val cameraSelector = getCameraSelector(cameraIndex)

        // create the user required QualitySelector (video resolution): we know this is
        // supported, a valid qualitySelector will be created.
        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

        binding.surfaceView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            val orientation = this@CameraXFragment.resources.configuration.orientation
            dimensionRatio = quality.getAspectRatioString(
                quality,
                (orientation == Configuration.ORIENTATION_PORTRAIT)
            )
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(quality.getAspectRatio(quality))
            .setTargetRotation(binding.surfaceView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            // The analyzer can then be assigned to the instance
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
                    val image = imageProxy.image
                    if (image != null) {
                        val imageRotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
                        if (!::imageBitmap.isInitialized) {
                            imageBitmap = Bitmap.createBitmap(
                                imageProxy.width,
                                imageProxy.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }
                        yuvConverter.yuvToRgb(image, imageBitmap)
                        val rotateMatrix = Matrix()
                        rotateMatrix.postRotate(imageRotationDegrees)
                        val rotatedBitmap = Bitmap.createBitmap(
                            imageBitmap, 0, 0,
                            imageProxy.width,
                            imageProxy.height,
                            rotateMatrix, false
                        )
                        // Create rotated version for portrait display
                        try {
                            processImage(rotatedBitmap)
                        } catch (_: java.lang.Exception) {

                        }
                    }
                    imageProxy.close()
                }
            }
        // build a recorder, which can:
        //   - record video/audio to MediaStore(only shown here), File, ParcelFileDescriptor
        //   - be used create recording(s) (the recording performs recording)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                imageAnalyzer,
                videoCapture
            )
        } catch (exc: Exception) {
            // we are on main thread, let's reset the controls on the UI.
            Timber.e("Use case binding failed", exc)
            recordVideoListener?.onError("Use case binding failed", exc)
        }
    }

    private fun processImage(bitmap: Bitmap) {

        val samples = mutableListOf<Sample>()
        synchronized(lock) {
            try{
                detector?.estimatePoses(bitmap)?.let {
                    samples.addAll(it)
                    // if the model only returns one item, allow running the Pose classifier.
                }
            }catch (t:Throwable){
                Timber.e(t)
            }

        }
        frameProcessedInOneSecondInterval++
        if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            cameraSourceListener?.onFPSListener(framesPerSecond)
        }

        // if the model returns only one item, show that item's score.
        if (samples.isNotEmpty()) {
            if (samples[0].score > MIN_CONFIDENCE) {
                cameraSourceListener?.onDetected(samples[0])
            }
            //listener?.onDetectedInfo(persons[0].score, classificationResult)
        }
        visualize(samples, bitmap)

    }

    private fun visualize(samples: List<Sample>, bitmap: Bitmap) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            samples.filter { it.score > MIN_CONFIDENCE },
            bitmap
        )

        val holder = binding.surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
                Rect(left, top, right, bottom), null
            )
            binding.surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }


    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    @SuppressLint("MissingPermission")
    fun startVideoRecording(outFile: File) {

        val mediaStoreOutput = FileOutputOptions.Builder(outFile)
            .build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording = videoCapture.output
            .prepareRecording(requireActivity(), mediaStoreOutput)
            .apply { if (audioEnabled) withAudioEnabled() }
            .start(mainThreadExecutor) { event ->
                if (event is VideoRecordEvent.Finalize) {
                    // display the captured video
                    lifecycleScope.launch {
                        Timber.i("Video File : ${event.outputResults.outputUri.path}")
                        recordVideoListener?.onVideoSaved(event.outputResults.outputUri)
                    }
                }
            }

        Timber.i("Recording started")
    }

    fun stopVideoRecording() {
        if (currentRecording == null) {
            return
        }
        val recording = currentRecording
        if (recording != null) {
            recording.stop()
            currentRecording = null
        }
    }

    fun enableAudio(enable: Boolean) {
        this.audioEnabled = enable
    }

    fun getQualities(): List<String> {
        val selectorStrings = cameraCapabilities[cameraIndex].qualities.map {
            it.getNameString()
        }
        return selectorStrings
    }

    /**
     * Retrieve the asked camera's type(lens facing type). In this sample, only 2 types:
     *   idx is even number:  CameraSelector.LENS_FACING_BACK
     *          odd number:   CameraSelector.LENS_FACING_FRONT
     */
    private fun getCameraSelector(idx: Int): CameraSelector {
        return CameraSelector.Builder().requireLensFacing(idx).build()
    }

    data class CameraCapability(val camSelector: CameraSelector, var qualities: List<Quality>)

    /**
     * Query and cache this platform's camera capabilities, run only once.
     */
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(requireContext()).await()

                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // just get the camera.cameraInfo to query capabilities
                        // we are not binding anything here.
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(requireActivity(), camSelector)
                            QualitySelector
                                .getSupportedQualities(camera.cameraInfo)
                                .filter { quality ->
                                    listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD)
                                        .contains(quality)
                                }.also {
                                    cameraCapabilities.add(CameraCapability(camSelector, it))
                                }
                            cameraCapabilities.forEach { it.qualities = it.qualities.reversed() }
                        }
                    } catch (exc: java.lang.Exception) {
                        Timber.e("Camera Face $camSelector is not supported")
                    }
                }
            }
        }
    }

    /**
     * One time initialize for CameraFragment (as a part of fragment layout's creation process).
     * This function performs the following:
     *   - initialize but disable all UI controls except the Quality selection.
     *   - set up the Quality selection recycler view.
     *   - bind use cases to a lifecycle camera, enable UI controls.
     */
    private fun initCameraFragment() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            bindCaptureUsecase()
        }
    }


    override fun onResume() {
        super.onResume()
        fpsTimer = Timer()
        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    override fun onDestroyView() {
        //TODO revisart error al llamar a close()
      /*  detector?.close()
        detector = null*/
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
        super.onDestroyView()
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                initCameraFragment()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    // check if permission is granted or not.
    private fun isCameraPermissionGranted(): Boolean {
        return requireActivity().checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCameraBinding {
        return FragmentCameraBinding.inflate(inflater, container, false)
    }

    companion object {
        const val MIN_CONFIDENCE = .4f
        var DEFAULT_QUALITY_IDX = 1
    }
}