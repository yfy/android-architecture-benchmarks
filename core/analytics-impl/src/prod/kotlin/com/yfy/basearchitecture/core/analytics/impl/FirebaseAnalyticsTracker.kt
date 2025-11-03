package com.yfy.basearchitecture.core.analytics.impl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.yfy.basearchitecture.core.analytics.api.AnalyticsProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsProvider {
    override fun logScreen(screenName: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (mainKey, value) ->
                val key = mainKey.take(40)
                when (value) {
                    is String -> putString(key, value.take(100))
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString().take(100))
                }
            }
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle.apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (mainKey, value) ->
                val key = mainKey.take(40)
                when (value) {
                    is String -> putString(key, value.take(100))
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString().take(100))
                }
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun startSession() {
        // Firebase handles sessions automatically
    }

    override fun endSession() {
        // Firebase handles sessions automatically
    }

    override fun resetAnalyticsData() {
        firebaseAnalytics.resetAnalyticsData()
    }
} 