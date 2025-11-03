plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.android.room)
    alias(libs.plugins.yfy.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.core.database.impl"
}

dependencies {
    implementation(projects.core.databaseApi)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}