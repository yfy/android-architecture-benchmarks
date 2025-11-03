package com.yfy.basearchitecture.core.ui.impl.di

import android.content.Context
import com.yfy.basearchitecture.core.ui.api.handler.ErrorHandler
import com.yfy.basearchitecture.core.ui.api.handler.UiHandler
import com.yfy.basearchitecture.core.ui.api.managers.BackPressManager
import com.yfy.basearchitecture.core.ui.api.managers.DialogManager
import com.yfy.basearchitecture.core.ui.api.managers.LoaderManager
import com.yfy.basearchitecture.core.ui.api.managers.PermissionManager
import com.yfy.basearchitecture.core.ui.api.managers.ToastManager
import com.yfy.basearchitecture.core.ui.api.utils.ResourceProvider
import com.yfy.basearchitecture.core.ui.impl.backpress.BackPressManagerImpl
import com.yfy.basearchitecture.core.ui.impl.dialog.DialogManagerImpl
import com.yfy.basearchitecture.core.ui.impl.error.ErrorHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.handler.UiHandlerImpl
import com.yfy.basearchitecture.core.ui.impl.loader.LoaderManagerImpl
import com.yfy.basearchitecture.core.ui.impl.permission.PermissionManagerImpl
import com.yfy.basearchitecture.core.ui.impl.resources.ResourceProviderImpl
import com.yfy.basearchitecture.core.ui.impl.toast.ToastManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiModule {
    
    @Provides
    @Singleton
    fun provideUiHandler(
        @ApplicationContext context: Context
    ): UiHandler = UiHandlerImpl(context)

    @Provides
    @Singleton
    fun provideResourceProvider(
        @ApplicationContext context: Context
    ): ResourceProvider = ResourceProviderImpl(context)
    
    @Provides
    @Singleton
    fun provideErrorHandler(
        @ApplicationContext context: Context
    ): ErrorHandler = ErrorHandlerImpl(context)
    
    @Provides
    @Singleton
    fun provideToastManager(
        @ApplicationContext context: Context
    ): ToastManager = ToastManagerImpl(context)
    
    @Provides
    @Singleton
    fun provideBackPressManager(): BackPressManager = BackPressManagerImpl()
    
    @Provides
    @Singleton
    fun provideDialogManager(
        @ApplicationContext context: Context
    ): DialogManager = DialogManagerImpl(context)
    
    @Provides
    @Singleton
    fun provideLoaderManager(
        @ApplicationContext context: Context
    ): LoaderManager = LoaderManagerImpl(context)
    
    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): PermissionManager = PermissionManagerImpl(context)
} 