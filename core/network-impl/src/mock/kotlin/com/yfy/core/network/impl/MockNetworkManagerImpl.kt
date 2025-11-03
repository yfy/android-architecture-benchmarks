package com.yfy.basearchitecture.core.network.impl

import android.content.Context
import com.google.gson.Gson
import com.yfy.basearchitecture.core.network.api.NetworkError
import com.yfy.basearchitecture.core.network.api.NetworkManager
import com.yfy.basearchitecture.core.network.api.NetworkResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockNetworkManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : NetworkManager {

    override fun <T> createService(serviceClass: Class<T>): T {
        // Mock service creation - return a mock instance
        return MockServiceFactory.createMockService(serviceClass)
    }

    override suspend fun <T> executeRequest(
        request: suspend () -> T
    ): Flow<NetworkResponse<T>> = flow {
        emit(NetworkResponse.Loading)
        try {
            // Mock delay
            delay(500L)
            val response = request.invoke()
            emit(NetworkResponse.Success(response))
        } catch (e: Exception) {
            emit(NetworkResponse.Error(NetworkError.UnknownError(e)))
        }
    }

    override fun getBaseUrl(): String = "https://mock.local/"

    fun <T> loadMockData(fileName: String, clazz: Class<T>): T? {
        return try {
            val json = context.assets.open("$fileName.json")
                .bufferedReader()
                .use { it.readText() }
            gson.fromJson(json, clazz)
        } catch (e: Exception) {
            null
        }
    }
    
    fun <T> loadMockDataFromJson(jsonString: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(jsonString, clazz)
        } catch (e: Exception) {
            null
        }
    }
}

// Mock service factory for creating mock service instances
object MockServiceFactory {
    @Suppress("UNCHECKED_CAST")
    fun <T> createMockService(serviceClass: Class<T>): T {
        return when (serviceClass.simpleName) {
            "AuthApiService" -> MockAuthApiService() as T
            else -> throw IllegalArgumentException("Unknown service class: ${serviceClass.simpleName}")
        }
    }
}

// Mock AuthApiService implementation
class MockAuthApiService {
    // Add mock methods as needed
} 