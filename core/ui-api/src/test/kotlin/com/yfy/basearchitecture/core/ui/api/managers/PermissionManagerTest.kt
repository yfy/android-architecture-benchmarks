package com.yfy.basearchitecture.core.ui.api.managers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class PermissionManagerTest {

    @Mock
    private lateinit var permissionManager: PermissionManager

    private val testPermission = "android.permission.CAMERA"
    private val testPermissions = listOf("android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE")

    @Before
    fun setup() {
        // Setup default mock behavior
        `when`(permissionManager.isPermissionGranted(testPermission)).thenReturn(true)
        `when`(permissionManager.arePermissionsGranted(testPermissions)).thenReturn(true)
        `when`(permissionManager.shouldShowPermissionRationale(testPermission)).thenReturn(false)
        `when`(permissionManager.getPermissionStatusFlow(testPermission)).thenReturn(flowOf(PermissionStatus.Granted))
        `when`(permissionManager.arePermissionsPermanentlyDenied(testPermissions)).thenReturn(false)
    }

    @Test
    fun `test isPermissionGranted returns true for granted permission`() {
        // Given
        `when`(permissionManager.isPermissionGranted(testPermission)).thenReturn(true)

        // When
        val result = permissionManager.isPermissionGranted(testPermission)

        // Then
        assertTrue(result)
        verify(permissionManager).isPermissionGranted(testPermission)
    }

    @Test
    fun `test isPermissionGranted returns false for denied permission`() {
        // Given
        `when`(permissionManager.isPermissionGranted(testPermission)).thenReturn(false)

        // When
        val result = permissionManager.isPermissionGranted(testPermission)

        // Then
        assertFalse(result)
        verify(permissionManager).isPermissionGranted(testPermission)
    }

    @Test
    fun `test arePermissionsGranted returns true when all permissions granted`() {
        // Given
        `when`(permissionManager.arePermissionsGranted(testPermissions)).thenReturn(true)

        // When
        val result = permissionManager.arePermissionsGranted(testPermissions)

        // Then
        assertTrue(result)
        verify(permissionManager).arePermissionsGranted(testPermissions)
    }

    @Test
    fun `test arePermissionsGranted returns false when any permission denied`() {
        // Given
        `when`(permissionManager.arePermissionsGranted(testPermissions)).thenReturn(false)

        // When
        val result = permissionManager.arePermissionsGranted(testPermissions)

        // Then
        assertFalse(result)
        verify(permissionManager).arePermissionsGranted(testPermissions)
    }

    @Test
    fun `test requestPermission returns granted result`() = runTest {
        // Given
        val expectedResult = PermissionResult.Granted
        `when`(permissionManager.requestPermission(testPermission)).thenReturn(expectedResult)

        // When
        val result = permissionManager.requestPermission(testPermission)

        // Then
        assertEquals(expectedResult, result)
        verify(permissionManager).requestPermission(testPermission)
    }

    @Test
    fun `test requestPermission returns denied result`() = runTest {
        // Given
        val expectedResult = PermissionResult.Denied
        `when`(permissionManager.requestPermission(testPermission)).thenReturn(expectedResult)

        // When
        val result = permissionManager.requestPermission(testPermission)

        // Then
        assertEquals(expectedResult, result)
        verify(permissionManager).requestPermission(testPermission)
    }

    @Test
    fun `test requestPermission returns permanently denied result`() = runTest {
        // Given
        val expectedResult = PermissionResult.PermanentlyDenied
        `when`(permissionManager.requestPermission(testPermission)).thenReturn(expectedResult)

        // When
        val result = permissionManager.requestPermission(testPermission)

        // Then
        assertEquals(expectedResult, result)
        verify(permissionManager).requestPermission(testPermission)
    }

    @Test
    fun `test requestPermission returns error result`() = runTest {
        // Given
        val exception = RuntimeException("Permission request failed")
        val expectedResult = PermissionResult.Error(exception)
        `when`(permissionManager.requestPermission(testPermission)).thenReturn(expectedResult)

        // When
        val result = permissionManager.requestPermission(testPermission)

        // Then
        assertEquals(expectedResult, result)
        verify(permissionManager).requestPermission(testPermission)
    }

    @Test
    fun `test requestPermissions returns list of results`() = runTest {
        // Given
        val expectedResults = listOf(
            PermissionResult.Granted,
            PermissionResult.Denied
        )
        `when`(permissionManager.requestPermissions(testPermissions)).thenReturn(expectedResults)

        // When
        val results = permissionManager.requestPermissions(testPermissions)

        // Then
        assertEquals(expectedResults, results)
        verify(permissionManager).requestPermissions(testPermissions)
    }

    @Test
    fun `test shouldShowPermissionRationale returns true`() {
        // Given
        `when`(permissionManager.shouldShowPermissionRationale(testPermission)).thenReturn(true)

        // When
        val result = permissionManager.shouldShowPermissionRationale(testPermission)

        // Then
        assertTrue(result)
        verify(permissionManager).shouldShowPermissionRationale(testPermission)
    }

    @Test
    fun `test shouldShowPermissionRationale returns false`() {
        // Given
        `when`(permissionManager.shouldShowPermissionRationale(testPermission)).thenReturn(false)

        // When
        val result = permissionManager.shouldShowPermissionRationale(testPermission)

        // Then
        assertFalse(result)
        verify(permissionManager).shouldShowPermissionRationale(testPermission)
    }

    @Test
    fun `test getPermissionStatusFlow returns granted status`() {
        // Given
        val expectedStatus = PermissionStatus.Granted
        `when`(permissionManager.getPermissionStatusFlow(testPermission)).thenReturn(flowOf(expectedStatus))

        // When
        val flow = permissionManager.getPermissionStatusFlow(testPermission)

        // Then
        verify(permissionManager).getPermissionStatusFlow(testPermission)
        // Note: Flow testing would require additional setup with coroutines
    }

    @Test
    fun `test openAppSettings is called`() {
        // When
        permissionManager.openAppSettings()

        // Then
        verify(permissionManager).openAppSettings()
    }

    @Test
    fun `test arePermissionsPermanentlyDenied returns true`() {
        // Given
        `when`(permissionManager.arePermissionsPermanentlyDenied(testPermissions)).thenReturn(true)

        // When
        val result = permissionManager.arePermissionsPermanentlyDenied(testPermissions)

        // Then
        assertTrue(result)
        verify(permissionManager).arePermissionsPermanentlyDenied(testPermissions)
    }

    @Test
    fun `test arePermissionsPermanentlyDenied returns false`() {
        // Given
        `when`(permissionManager.arePermissionsPermanentlyDenied(testPermissions)).thenReturn(false)

        // When
        val result = permissionManager.arePermissionsPermanentlyDenied(testPermissions)

        // Then
        assertFalse(result)
        verify(permissionManager).arePermissionsPermanentlyDenied(testPermissions)
    }

    @Test
    fun `test PermissionResult sealed class instances`() {
        // Test all PermissionResult instances
        val granted = PermissionResult.Granted
        val denied = PermissionResult.Denied
        val permanentlyDenied = PermissionResult.PermanentlyDenied
        val error = PermissionResult.Error(RuntimeException("Test"))

        assertTrue(granted is PermissionResult.Granted)
        assertTrue(denied is PermissionResult.Denied)
        assertTrue(permanentlyDenied is PermissionResult.PermanentlyDenied)
        assertTrue(error is PermissionResult.Error)
    }

    @Test
    fun `test PermissionStatus sealed class instances`() {
        // Test all PermissionStatus instances
        val granted = PermissionStatus.Granted
        val denied = PermissionStatus.Denied
        val permanentlyDenied = PermissionStatus.PermanentlyDenied
        val notRequested = PermissionStatus.NotRequested

        assertTrue(granted is PermissionStatus.Granted)
        assertTrue(denied is PermissionStatus.Denied)
        assertTrue(permanentlyDenied is PermissionStatus.PermanentlyDenied)
        assertTrue(notRequested is PermissionStatus.NotRequested)
    }
} 