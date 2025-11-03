plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.compose)
    alias(libs.plugins.yfy.hilt)
    alias(libs.plugins.yfy.unittest)
    alias(libs.plugins.yfy.instrumentedtest)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.core.ui.impl"
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(project(":core:ui-api"))
    implementation(project(":core:datastore-api"))
    implementation(project(":core:analytics-api"))
    implementation(project(":core:designsystem"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
}