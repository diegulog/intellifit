package com.diegulog.intellifit.domain.repository

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(username:String, password:String): Flow<ResultOf<String>>
    fun signUp(user:User): Flow<ResultOf<User>>

}