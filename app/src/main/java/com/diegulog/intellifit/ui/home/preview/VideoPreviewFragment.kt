package com.diegulog.intellifit.ui.home.preview

import android.annotation.SuppressLint
import android.media.PlaybackParams
import android.os.Bundle
import android.view.*
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.R
import com.diegulog.intellifit.data.isLoading
import com.diegulog.intellifit.data.onFailure
import com.diegulog.intellifit.data.onSuccess
import com.diegulog.intellifit.databinding.FragmentVideoPreviewBinding
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.home.HomeFragment
import com.diegulog.intellifit.ui.home.exercise.ExerciseFragmentArgs
import com.diegulog.intellifit.ui.home.exercise.ExerciseFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class VideoPreviewFragment : BaseFragment<FragmentVideoPreviewBinding>(), MenuProvider {

    private val args: ExerciseFragmentArgs by navArgs()
    private val viewModel: VideoPreviewViewModel by viewModel{
        parametersOf(args.exercise)
    }
    private var totalVideos: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this)
        getHostFragment<HomeFragment>()?.setTitle(getString(R.string.preview) + " - " +args.exercise.name)
        viewModel.getCaptures().observe(viewLifecycleOwner) {
            binding.progress.isVisible = it.isLoading
            it.onSuccess { captures ->
                val (correct, incorrect) = captures.partition { it.moveType == MoveType.CORRECT }
                totalVideos = correct.size
                showListCaptures(correct.toMutableList(), incorrect.toMutableList())
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showListCaptures(corrects: MutableList<Capture>, incorrects: MutableList<Capture>) {
        if (corrects.isEmpty()) {
            showMessage("Ninguna captura disponible")
            findNavController().popBackStack()
            return
        }
        configureButtons(corrects, incorrects)

        binding.videoCounter.text = String.format("%s / %s Videos", corrects.size, totalVideos)
        val videoFile = File(corrects[0].videoPath)
        val videoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            videoFile
        )
        binding.video.setVideoURI(videoUri)
        //binding.video.isVisible = true
        binding.video.setOnTouchListener { _, _ ->

            false
        }

        binding.surfaceView.setOnTouchListener { _, _ ->
            playCapture(corrects[0])
            binding.video.start()
            false
        }
        //playCapture(corrects[0])
        playVideo()
    }

    private fun playCapture(capture: Capture){
        viewModel.playCapture(binding.surfaceView, capture).observe(viewLifecycleOwner){
            binding.surfaceView.isClickable = !it.isLoading
        }
    }


    private fun playVideo() {
        binding.video.setOnPreparedListener { mp ->
            val myPlayBackParams = PlaybackParams()
            myPlayBackParams.speed = 1f
            mp.playbackParams = myPlayBackParams
            binding.video.start()
        }
    }

    private fun configureButtons(corrects: MutableList<Capture>, incorrects: MutableList<Capture>) {

        binding.correct.isEnabled = true
        binding.incorrect.isEnabled = true
        binding.delete.isEnabled = true

        binding.correct.setOnClickListener {
            processCaptures(corrects, incorrects, ::onSelectCorrect)
        }
        binding.incorrect.setOnClickListener {
            //Para que los datos no se sobre entrenen en incorrectos
            if (incorrects.isNotEmpty())
                viewModel.deleteCapture(incorrects.removeAt(0))
            processCaptures(corrects, incorrects, ::onSelectIncorrect)
        }
        binding.delete.setOnClickListener {
            processCaptures(corrects, incorrects, ::onDelete)
        }

    }

    private fun processCaptures(
        corrects: MutableList<Capture>,
        incorrects: MutableList<Capture>,
        action: (Capture, Capture?) -> Unit
    ) {
        action(corrects[0], incorrects.getOrNull(0))
        if (corrects.isNotEmpty()) corrects.removeAt(0)
        if (incorrects.isNotEmpty()) incorrects.removeAt(0)
        showListCaptures(corrects, incorrects)
    }

    private fun onSelectCorrect(correct: Capture, incorrect: Capture?) {
        sendCapture(correct)
        incorrect?.let { sendCapture(it) }
    }

    private fun onSelectIncorrect(correct: Capture, incorrect: Capture?) {
        correct.moveType = MoveType.INCORRECT
        sendCapture(correct)
        incorrect?.let { viewModel.deleteCapture(it) }
    }

    private fun onDelete(correct: Capture, incorrect: Capture?) {
        viewModel.deleteCapture(correct)
        incorrect?.let { viewModel.deleteCapture(it) }
    }

    private fun sendCapture(capture: Capture) {
        viewModel.sendCapture(capture).observe(viewLifecycleOwner){
            it.onFailure {error ->
                error?.message?.let { showMessage(it) }
            }
        }
        viewModel.deleteCapture(capture)
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoPreviewBinding {
        return FragmentVideoPreviewBinding.inflate(inflater, container, false)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.video_preview_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.menu_preview -> {
                binding.video.isVisible = !binding.video.isVisible
                binding.surfaceView.isVisible = !binding.surfaceView.isVisible
            }

        }
        return true
    }
    override fun onDestroyView() {
        requireActivity().removeMenuProvider(this)
        super.onDestroyView()
    }

}