package com.diegulog.intellifit.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.diegulog.intellifit.R
import com.diegulog.intellifit.data.isLoading
import com.diegulog.intellifit.data.onFailure
import com.diegulog.intellifit.data.onLoading
import com.diegulog.intellifit.data.onSuccess
import com.diegulog.intellifit.databinding.FragmentLoginBinding
import com.diegulog.intellifit.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val viewModel: LoginViewModel by activityViewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)
        }

        binding.signInButton.setOnClickListener(View.OnClickListener {
            cleanErrors()
            val user = binding.user.text.toString()
            if (!viewModel.isUserNameValid(user)) {
                binding.inputLayoutMail.error = "wrong email format"
                return@OnClickListener
            }
            val password = binding.password.text.toString()
            if (!viewModel.isPasswordValid(password)) {
                binding.inputLayoutPassword.error = "wrong password format"
                return@OnClickListener
            }
            viewModel.login(user, password).observe(viewLifecycleOwner) {
                binding.signInButton.isEnabled =  !it.isLoading
                binding.progressBar.isVisible = it.isLoading
                binding.signInButton.text =  if(it.isLoading) "" else getString(R.string.sign_in)
                it.onSuccess { token ->
                    viewModel.saveSessionToken(token)
                    findNavController().navigate(R.id.action_LoginFragment_to_HomeFragment)
                }
                it.onFailure { failure ->
                    failure?.message?.let { message ->
                        binding.apiError.text = message
                    }

                }
            }
        })
    }

    private fun cleanErrors() {
        binding.inputLayoutMail.error = ""
        binding.inputLayoutPassword.error = ""
        binding.apiError.text = ""
    }

}