package com.yfy.basearchitecture.core.network.impl

import com.yfy.basearchitecture.core.network.api.NetworkError
import com.yfy.basearchitecture.core.network.api.NetworkManager
import com.yfy.basearchitecture.core.network.api.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManagerImpl @Inject constructor(
    private val retrofit: Retrofit
) : NetworkManager {


    override fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    override suspend fun <T> executeRequest(
        request: suspend () -> T
    ): Flow<NetworkResponse<T>> = flow{
        emit( try {
            val result = request()
            NetworkResponse.Success(result)
        } catch (e: IOException) {
            NetworkResponse.Error(NetworkError.NetworkUnavailable)
        } catch (e: Exception) {
            NetworkResponse.Error(NetworkError.UnknownError(e))
        })
    }.flowOn(Dispatchers.IO)

    override fun getBaseUrl(): String = BuildConfig.BASE_URL

}