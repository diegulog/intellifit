package com.diegulog.intellifit.data.repository.remote

import com.diegulog.intellifit.data.ResultOf
import com.diegulog.intellifit.domain.entity.User
import com.diegulog.intellifit.domain.repository.UserRepository
import com.diegulog.intellifit.domain.repository.remote.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class UserRepositoryImpl(
    private val networkDataSource: NetworkDataSource
) : UserRepository{
    override fun login(username: String, password: String) = callFlow {
        emit(ResultOf.Success(networkDataSource.login(username, password)))
    }

    override fun signUp(user: User) = callFlow {
        emit(ResultOf.Success(networkDataSource.signUp(user)))
    }

    private fun <T> callFlow(block: suspend FlowCollector<ResultOf<T>>.() -> Unit) = flow {
        emit(ResultOf.Loading)
        try {
            block.invoke(this)
        } catch (e: Exception) {
            Timber.e(e)
            emit(ResultOf.Failure(e))
        }
    }
}