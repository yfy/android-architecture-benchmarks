package com.yfy.basearchitecture.core.analytics.api

interface AnalyticsProvider {

    /**
     * Logs an tracked screen with the given name and parameters
     * @param screenName The name of the screen to log
     * @param params Optional parameters to include with the event
     */
    fun logScreen(screenName: String, params: Map<String, Any> = emptyMap())

    /**
     * Logs an event with the given name and parameters
     * @param eventName The name of the event to log
     * @param params Optional parameters to include with the event
     */
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())

    /**
     * Sets a user property that will be included with all future events
     * @param name The name of the property
     * @param value The value of the property
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Sets the user ID for the current user
     * @param userId The user ID to set
     */
    fun setUserId(userId: String)

    /**
     * Logs the user's session is started
     */
    fun startSession()

    /**
     * Logs the user's session is ended
     */
    fun endSession()

    /**
     * Resets analytics data for the current user
     */
    fun resetAnalyticsData()
} 