package com.diegulog.intellifit.ui.signup

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
import com.diegulog.intellifit.databinding.FragmentSignUpBinding
import com.diegulog.intellifit.domain.entity.User
import com.diegulog.intellifit.ui.base.BaseFragment
import com.diegulog.intellifit.ui.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val viewModel: LoginViewModel by activityViewModel()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.signUpButton.setOnClickListener(View.OnClickListener {
            cleanErrors()
            val email = binding.email.text.toString()
            if (!viewModel.isUserNameValid(email)) {
                binding.inputLayoutMail.error = "wrong email format"
                return@OnClickListener
            }
            val password = binding.password.text.toString()
            if (!viewModel.isPasswordValid(password)) {
                binding.inputLayoutPassword.error = "wrong password format"
                return@OnClickListener
            }
            val passwordConfirm = binding.passwordConfirm.text.toString()
            if (password != passwordConfirm) {
                binding.inputLayoutPasswordConfirm.error = "wrong password format"
                return@OnClickListener
            }
            val height = binding.height.text.toString().toFloat()
            val weight = binding.weight.text.toString().toFloat()

            val user = User(email = email, password = password, height = height, weight = weight)
            viewModel.signUp(user).observe(viewLifecycleOwner) {
                binding.signUpButton.isEnabled =  !it.isLoading
                binding.progressBar.isVisible = it.isLoading
                binding.signUpButton.text =  if(it.isLoading) "" else getString(R.string.sign_in)

                it.onSuccess { token ->
                    showMessage("Account created successfully")
                    findNavController().popBackStack()
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
        binding.inputLayoutPasswordConfirm.error = ""
        binding.apiError.text = ""

    }

}