plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.hilt)
    alias(libs.plugins.yfy.unittest)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.yfy.basearchitecture.core.datastore.impl"
}

dependencies {
    implementation(projects.core.datastoreApi)
    implementation(projects.core.model)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.dataStore)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.serialization.json)
}