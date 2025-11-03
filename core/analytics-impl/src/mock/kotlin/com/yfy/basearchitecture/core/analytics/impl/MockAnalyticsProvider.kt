package com.yfy.basearchitecture.core.analytics.impl

import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAnalyticsProvider @Inject constructor() : AnalyticsProvider {

    override fun logScreen(screenName: String, params: Map<String, Any>) {
        Timber.d("Screen: $screenName ${if (params.isNotEmpty()) "| Params: $params" else ""}")
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        Timber.d("Event: $eventName ${if (params.isNotEmpty()) "| Params: $params" else ""}")
    }

    override fun setUserProperty(name: String, value: String) {
        Timber.d("User Property: $name = $value")
    }

    override fun setUserId(userId: String) {
        Timber.d("User ID: $userId")
    }

    override fun startSession() {
        Timber.d("Session Started")
    }

    override fun endSession() {
        Timber.d("Session Ended")
    }

    override fun resetAnalyticsData() {
        Timber.d("Analytics Data Reset")
    }

}