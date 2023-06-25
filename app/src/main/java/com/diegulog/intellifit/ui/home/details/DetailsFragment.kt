package com.diegulog.intellifit.ui.home.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.diegulog.intellifit.databinding.FragmentDatailsBinding
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.ui.base.BaseAdapter
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.utils.load

class DetailsFragment : BaseFragment<FragmentDatailsBinding>() {

    private val training: Training by lazy {
        navArgs<DetailsFragmentArgs>().value.training
    }
    private val detailsViewModel: DetailsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsViewModel.setTraining(training)
        binding.image.load(training.urlImage)
        binding.title.text = training.name
        binding.exerciseSize.text = training.exercises.size.toString()
        val adapter = DetailsAdapter()
        binding.recyclerView.adapter = adapter
        adapter.setItems(training.exercises)
        adapter.setOnClickListener(object : BaseAdapter.OnClickListener<Exercise> {
            override fun onClick(position:Int, item: Exercise) {
                detailsViewModel.currentExerciseIndex = position
                Navigation.findNavController(view).navigate(DetailsFragmentDirections.actionDetailsFragmentToExerciseFragment())
            }
        })
        binding.start.setOnClickListener{
            if(detailsViewModel.start.value == false){
                detailsViewModel.setStart(true)
                detailsViewModel.currentExerciseIndex = 0
                Navigation.findNavController(view).navigate(DetailsFragmentDirections.actionDetailsFragmentToExerciseFragment())
            }else{
                detailsViewModel.setStart(false)
            }
        }

        detailsViewModel.start.observe(viewLifecycleOwner){
            binding.start.isActivated = it
        }
    }


    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDatailsBinding {
        return FragmentDatailsBinding.inflate(inflater, container, false)
    }


}