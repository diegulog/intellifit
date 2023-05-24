package com.diegulog.intellifit.ui.home.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.databinding.FragmentDatailsBinding
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.ui.base.BaseAdapter
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.exercise.ExerciseFragmentDirections
import com.diegulog.intellifit.ui.home.HomeFragment
import com.diegulog.intellifit.ui.home.training.TrainingFragmentDirections
import com.diegulog.intellifit.utils.load

class DetailsFragment : BaseFragment<FragmentDatailsBinding>() {

    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val training = args.training
        binding.image.load(training.urlImage)
        binding.title.text = training.name
        binding.exerciseSize.text = training.exercises.size.toString()
        val adapter = DetailsAdapter()
        binding.recyclerView.adapter = adapter
        adapter.setItems(training.exercises)
        adapter.setOnClickListener(object : BaseAdapter.OnClickListener<Exercise>{
            override fun onClick(item: Exercise) {
                val direction = DetailsFragmentDirections.actionDetailsFragmentToExerciseFragment(item)
                Navigation.findNavController(view).navigate(direction)
            }
        })
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDatailsBinding {
        return FragmentDatailsBinding.inflate(inflater, container, false)
    }


}