package com.yfy.basearchitecture.core.ui.impl.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.yfy.basearchitecture.core.ui.api.managers.PermissionManager
import com.yfy.basearchitecture.core.ui.api.managers.PermissionResult
import com.yfy.basearchitecture.core.ui.api.managers.PermissionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class PermissionManagerImpl @Inject constructor(
    private val context: Context
) : PermissionManager {

    private val permissionStatusMap = mutableMapOf<String, MutableStateFlow<PermissionStatus>>()

    override fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun arePermissionsGranted(permissions: List<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }

    override suspend fun requestPermission(permission: String): PermissionResult {
        return try {
            when {
                isPermissionGranted(permission) -> PermissionResult.Granted
                shouldShowPermissionRationale(permission) -> PermissionResult.Denied
                else -> PermissionResult.PermanentlyDenied
            }
        } catch (e: Exception) {
            PermissionResult.Error(e)
        }
    }

    override suspend fun requestPermissions(permissions: List<String>): List<PermissionResult> {
        return permissions.map { requestPermission(it) }
    }

    override fun shouldShowPermissionRationale(permission: String): Boolean {
        // This would need to be implemented with Activity or Fragment context
        // For now, return false as a default
        return false
    }

    override fun getPermissionStatusFlow(permission: String): Flow<PermissionStatus> {
        return permissionStatusMap.getOrPut(permission) {
            MutableStateFlow(getCurrentPermissionStatus(permission))
        }
    }

    override fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun arePermissionsPermanentlyDenied(permissions: List<String>): Boolean {
        return permissions.all { permission ->
            !isPermissionGranted(permission) && !shouldShowPermissionRationale(permission)
        }
    }

    /**
     * Update permission status for a specific permission
     */
    fun updatePermissionStatus(permission: String, status: PermissionStatus) {
        permissionStatusMap[permission]?.value = status
    }

    /**
     * Get current permission status
     */
    private fun getCurrentPermissionStatus(permission: String): PermissionStatus {
        return when {
            isPermissionGranted(permission) -> PermissionStatus.Granted
            shouldShowPermissionRationale(permission) -> PermissionStatus.Denied
            else -> PermissionStatus.PermanentlyDenied
        }
    }

    /**
     * Set Activity/Fragment context for permission rationale
     * This should be called from Activity or Fragment
     */
    fun setActivityContext(activityContext: android.app.Activity?) {
        // In a real implementation, you would store the activity context
        // to properly check shouldShowPermissionRationale
    }

    /**
     * Set Fragment context for permission rationale
     * This should be called from Fragment
     */
    fun setFragmentContext(fragmentContext: androidx.fragment.app.Fragment?) {
        // In a real implementation, you would store the fragment context
        // to properly check shouldShowPermissionRationale
    }
} 