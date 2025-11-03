plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.core.datastore.api"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.androidx.dataStore)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
} 