plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
}

android {
    namespace = "com.yfy.basearchitecture.core.network.api"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
} 