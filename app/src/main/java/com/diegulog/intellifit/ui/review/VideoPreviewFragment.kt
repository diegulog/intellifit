package com.diegulog.intellifit.ui.review

import android.annotation.SuppressLint
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.diegulog.intellifit.data.isLoading
import com.diegulog.intellifit.data.onSuccess
import com.diegulog.intellifit.databinding.FragmentVideoPreviewBinding
import com.diegulog.intellifit.domain.entity.Capture
import com.diegulog.intellifit.domain.entity.MoveType
import com.diegulog.intellifit.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class VideoPreviewFragment : BaseFragment<FragmentVideoPreviewBinding>() {

    private val videoPreviewViewModel: VideoPreviewViewModel by viewModel()
    private var totalVideos: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPreviewViewModel.getCaptures().observe(viewLifecycleOwner) {
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
        val videoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", videoFile)
        binding.video.setVideoURI(videoUri)
        binding.video.isVisible = true
        binding.video.setOnTouchListener { _, _ ->
            binding.video.start()
            false
        }
        playVideo()
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
        corrects.removeAt(0)
        incorrects.removeAt(0)
        showListCaptures(corrects, incorrects)
    }

    private fun onSelectCorrect(correct: Capture, incorrect: Capture?) {
        sendCapture(correct)
        incorrect?.let { sendCapture(it) }
    }

    private fun onSelectIncorrect(correct: Capture, incorrect: Capture?) {
        correct.moveType = MoveType.INCORRECT
        sendCapture(correct)
        incorrect?.let { videoPreviewViewModel.deleteCapture(it) }
    }

    private fun onDelete(correct: Capture, incorrect: Capture?) {
        videoPreviewViewModel.deleteCapture(correct)
        incorrect?.let { videoPreviewViewModel.deleteCapture(it) }
    }

    private fun sendCapture(capture: Capture) {
        videoPreviewViewModel.parseCsv(capture)
        videoPreviewViewModel.deleteCapture(capture)
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoPreviewBinding {
        return FragmentVideoPreviewBinding.inflate(inflater, container, false)
    }

}