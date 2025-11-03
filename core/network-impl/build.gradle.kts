plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.hilt)
    id("kotlinx-serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.yfy.basearchitecture.core.network.impl"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.networkApi)

    api(libs.kotlinx.datetime)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.chucker)

    testImplementation(libs.kotlinx.coroutines.test)
}