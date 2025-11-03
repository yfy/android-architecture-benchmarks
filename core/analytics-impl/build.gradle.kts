plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.hilt)
}

android {
    namespace = "com.yfy.basearchitecture.core.analytics.impl"
}

dependencies {
    implementation(projects.core.analyticsApi)
    prodImplementation(platform(libs.firebase.bom))
    prodImplementation(libs.firebase.analytics)
} 