package com.diegulog.intellifit.ui.home.training

import android.view.LayoutInflater
import android.view.ViewGroup
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.ItemTrainingBinding
import com.diegulog.intellifit.domain.entity.Training
import com.diegulog.intellifit.ui.base.BaseAdapter
import com.diegulog.intellifit.utils.load
import kotlin.random.Random

class TrainingAdapter: BaseAdapter<Training, ItemTrainingBinding>()  {

    override fun onBind(binding: ItemTrainingBinding, item: Training?, position: Int) {
        item?.let {
            binding.title.text = it.name
            binding.image.load(it.urlImage)
            binding.secondaryText.text =
                String.format(binding.root.context.getString(R.string.exercises), it.exercises.size)
        }
    }
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemTrainingBinding {
        return ItemTrainingBinding.inflate(inflater, parent, false)
    }

}