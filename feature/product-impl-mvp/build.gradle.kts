plugins {
    alias(libs.plugins.yfy.android.library)
    alias(libs.plugins.yfy.android.library.compose)
    alias(libs.plugins.yfy.hilt)
}

android {
    namespace = "com.yfy.basearchitecture.feature.product.impl.mvp"
    
    sourceSets {
        getByName("mock") {
            res.srcDirs("src/mock/res")
        }
    }
}

dependencies {
    implementation(projects.feature.productApi)
    implementation(projects.feature.cartApi)
    implementation(projects.core.uiApi)
    implementation(projects.core.navigation)
    implementation(projects.core.analyticsApi)
    implementation(projects.core.networkApi)
    implementation(projects.core.designsystem)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}
