package com.diegulog.intellifit.ui.home.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.diegulog.intellifit.data.isFailure
import com.diegulog.intellifit.data.isLoading
import com.diegulog.intellifit.data.onFailure
import com.diegulog.intellifit.data.onSuccess
import com.diegulog.intellifit.databinding.FragmentTrainingBinding
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.ui.base.BaseAdapter
import com.diegulog.intellifit.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrainingFragment : BaseFragment<FragmentTrainingBinding>() {

    private val viewModel: TrainingViewModel by viewModel()
    private lateinit var adapter: TrainingAdapter
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTrainingBinding {
        return FragmentTrainingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrainingAdapter()
        binding.recyclerView.layoutManager =  GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        binding.retry.setOnClickListener { loadData() }
        binding.refresh.setOnRefreshListener { loadData()  }
        loadData()
        adapter.setOnClickListener(object : BaseAdapter.OnClickListener<Training>{
            override fun onClick(item: Training) {
                val direction = TrainingFragmentDirections.actionTrainingFragmentToDetailsFragment(item)
                Navigation.findNavController(view).navigate(direction)
            }
        })
    }

    private fun loadData() {
        viewModel.getTrainings().observe(viewLifecycleOwner) {
            binding.refresh.isRefreshing = false
            binding.progress.isVisible = it.isLoading
            binding.errorContainer.isVisible = it.isFailure
            it.onSuccess { trainings ->
                adapter.setItems(trainings)
            }
            it.onSuccess { trainings ->
                adapter.setItems(trainings)
            }
            it.onFailure { failure ->
                failure?.message?.let { error ->
                    binding.error.text = error
                }
            }
        }
    }

}