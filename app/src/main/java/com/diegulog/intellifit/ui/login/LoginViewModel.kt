package com.diegulog.intellifit.ui.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.asLiveData
import com.auth0.jwt.JWT
import com.diegulog.intellifit.domain.entity.User
import com.diegulog.intellifit.domain.repository.UserRepository
import com.diegulog.intellifit.domain.repository.local.AppPreferences
import com.diegulog.intellifit.ui.base.BaseViewModel


class LoginViewModel(
    private val context: Context,
    private val appPreferences: AppPreferences,
    private val userRepository: UserRepository
) : BaseViewModel()  {


    fun login(username:String, password:String) = userRepository.login(username, password).asLiveData()
    fun signUp(user: User) = userRepository.signUp(user).asLiveData()

    fun saveSessionToken(token:String){
        appPreferences.saveSessionToken(token)
    }

    fun getSessionToken():String? {
        return appPreferences.getSessionToken()
    }

    fun closeSession(){
        appPreferences.saveSessionToken(null)
    }

    fun getUser():String?{
        if(appPreferences.getSessionToken()!=null){
            val decodedJWT = JWT.decode(appPreferences.getSessionToken())
            return decodedJWT.getClaim("username").asString()
        }
        return null
    }
    fun isUserNameValid(username: String?): Boolean {
        if (username == null) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    fun isPasswordValid(password: String?): Boolean {
        if (password == null) {
            return false
        }
        return password.length > 3
    }

    companion object{
        const val SPACE_BETWEEN_INFERENCE = 2
    }
}