plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.compose)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.hilt)
    alias(libs.plugins.yfy.unittest)
}

android {
    namespace = "com.yfy.basearchitecture.core.ui.api"
}

dependencies {
    api(libs.androidx.navigation.compose)

    // Core dependencies
    api(projects.core.datastoreApi)
    api(projects.core.analyticsApi)
    api(projects.core.navigation)
    api(projects.core.designsystem)
    api(projects.core.model)
    api(libs.retrofit.converter.gson)
}