plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.unittest)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.core.model"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
}