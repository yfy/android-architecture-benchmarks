plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.android.room)
    alias(libs.plugins.yfy.unittest)
}

android {
    namespace = "com.yfy.basearchitecture.core.database.api"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}