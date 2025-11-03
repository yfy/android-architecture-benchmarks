plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.hilt)
    alias(libs.plugins.yfy.unittest)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.robolectric)
}