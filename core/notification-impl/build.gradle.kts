plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.jacoco)
    alias(libs.plugins.yfy.android.room)
    alias(libs.plugins.yfy.hilt)
    alias(libs.plugins.yfy.unittest)
}

android {
    namespace = "com.yfy.basearchitecture.core.notification.impl"
}

dependencies {
    implementation(projects.core.notificationApi)
    implementation(projects.core.databaseApi)
    implementation(projects.core.analyticsApi)
    implementation(projects.core.datastoreApi)
    implementation(projects.core.model)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.hilt.android)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.work.runtime.ktx)
}