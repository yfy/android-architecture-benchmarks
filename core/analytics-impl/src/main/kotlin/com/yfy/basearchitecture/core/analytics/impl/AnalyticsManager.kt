package com.yfy.basearchitecture.core.analytics.impl

import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards AnalyticsProvider>
) : AnalyticsProvider {
    override fun logScreen(screenName: String, params: Map<String, Any>) {
        executeProviderTask(::logScreen.name) { provider ->
            provider.logScreen(screenName, params)
        }
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        executeProviderTask(::logEvent.name) { provider ->
            provider.logEvent(eventName, params)
        }
    }

    override fun setUserProperty(name: String, value: String) {
        executeProviderTask(::setUserProperty.name) { provider ->
            provider.setUserProperty(name, value)
        }
    }

    override fun setUserId(userId: String) {
        executeProviderTask(::setUserId.name) { provider ->
            provider.setUserId(userId)
        }
    }

    override fun startSession() {
        executeProviderTask(::startSession.name) { provider ->
            provider.startSession()
        }
    }

    override fun endSession() {
        executeProviderTask(::endSession.name) { provider ->
            provider.endSession()
        }
    }

    override fun resetAnalyticsData() {
        executeProviderTask(::resetAnalyticsData.name) { provider ->
            provider.resetAnalyticsData()
        }
    }

    private fun executeProviderTask(
        functionName: String,
        function: (AnalyticsProvider) -> Unit
    ) {
        providers.forEach { provider ->
            try {
                function.invoke(provider)
            } catch (e: Exception) {
                Timber.e(e, "Failed to execute $functionName on ${provider::class.java.simpleName}")
            }
        }
    }


}