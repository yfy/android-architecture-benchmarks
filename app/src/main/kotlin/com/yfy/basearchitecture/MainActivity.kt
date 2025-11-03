package com.yfy.basearchitecture

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yfy.basearchitecture.core.designsystem.theme.theme.YfyTheme
import com.yfy.basearchitecture.core.navigation.NavigationManager
import com.yfy.basearchitecture.core.ui.api.base.BaseActivity
import com.yfy.basearchitecture.di.NavigationRouteRegistry
import com.yfy.basearchitecture.ui.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject 
    lateinit var navManager: NavigationManager
    @Inject lateinit var navigationRegistry: NavigationRouteRegistry
    override fun getViewModelClass() = MainActivityViewModel::class.java

    override fun initializeActivity() {
        setComposeContent {
            enableEdgeToEdge()
            YfyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(navManager, navigationRegistry)
                }
            }
        }
    }
}

@Composable
fun MainScreen(navManager: NavigationManager, navigationRegistry: NavigationRouteRegistry) {
    val navController = rememberNavController()
    navManager.setNavController(navController)

    AppNavHost(navigationRegistry, navManager)
}



