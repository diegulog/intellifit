package com.diegulog.intellifit.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.diegulog.intellifit.ui.home.HomeFragment

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null

    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    inline fun <reified T> getHostFragment(): T? {
        val navHostFragment = parentFragment as NavHostFragment?
        navHostFragment?.let {
            if (navHostFragment.parentFragment is T) {
                return navHostFragment.parentFragment as T
            }
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showMessage(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

}