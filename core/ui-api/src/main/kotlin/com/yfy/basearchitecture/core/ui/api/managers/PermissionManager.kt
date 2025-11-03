package com.yfy.basearchitecture.core.ui.api.managers

import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing permissions in the application
 */
interface PermissionManager {
    
    /**
     * Check if permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean
    
    /**
     * Check if multiple permissions are granted
     */
    fun arePermissionsGranted(permissions: List<String>): Boolean
    
    /**
     * Request permission
     */
    suspend fun requestPermission(permission: String): PermissionResult
    
    /**
     * Request multiple permissions
     */
    suspend fun requestPermissions(permissions: List<String>): List<PermissionResult>
    
    /**
     * Check if permission should show rationale
     */
    fun shouldShowPermissionRationale(permission: String): Boolean
    
    /**
     * Get permission status flow
     */
    fun getPermissionStatusFlow(permission: String): Flow<PermissionStatus>
    
    /**
     * Open app settings
     */
    fun openAppSettings()
    
    /**
     * Check if all permissions are permanently denied
     */
    fun arePermissionsPermanentlyDenied(permissions: List<String>): Boolean
}

/**
 * Permission result sealed class
 */
sealed class PermissionResult {
    object Granted : PermissionResult()
    object Denied : PermissionResult()
    object PermanentlyDenied : PermissionResult()
    data class Error(val exception: Exception) : PermissionResult()
}

/**
 * Permission status sealed class
 */
sealed class PermissionStatus {
    object Granted : PermissionStatus()
    object Denied : PermissionStatus()
    object PermanentlyDenied : PermissionStatus()
    object NotRequested : PermissionStatus()
} 