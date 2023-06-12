package com.diegulog.intellifit.ui.home.details

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.ItemExerciseBinding
import com.diegulog.intellifit.domain.entity.Exercise
import com.diegulog.intellifit.ui.base.BaseAdapter
import com.diegulog.intellifit.utils.load

class DetailsAdapter: BaseAdapter<Exercise, ItemExerciseBinding>()  {
    @SuppressLint("SetTextI18n")
    override fun onBind(binding: ItemExerciseBinding, item: Exercise?, position: Int) {
        item?.let {
            binding.title.text = item.name
            binding.image.load(item.urlImage)
            binding.description.text = item.description
            binding.secondaryText.text = "${item.repeat} ${binding.root.context.getString(R.string.reps)}"
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemExerciseBinding {
        return ItemExerciseBinding.inflate(inflater, parent, false)
    }


}