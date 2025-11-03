plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.compose)
    alias(libs.plugins.yfy.hilt)
}

android {
    namespace = "com.yfy.basearchitecture.feature.chat.impl.mvi"
    
    sourceSets {
        getByName("mock") {
            res.srcDirs("src/mock/res")
        }
        getByName("dev") {
            res.srcDirs("src/dev/res")
        }
    }
}

dependencies {
    implementation(projects.feature.chatApi)
    implementation(projects.core.uiApi)
    implementation(projects.core.navigation)
    implementation(projects.core.designsystem)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}
