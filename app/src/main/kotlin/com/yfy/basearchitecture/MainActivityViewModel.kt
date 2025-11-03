package com.yfy.basearchitecture

import com.yfy.basearchitecture.core.ui.api.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : BaseViewModel() {
    
    private fun initializeMainActivity() {
        logEvent("main_activity_initialized")
    }
    
    override fun onActivityCreated() {
        super.onActivityCreated()
        initializeMainActivity()
    }

} 