plugins {
    alias(libs.plugins.yfy.android.library)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.feature.product.api"
}

dependencies {
    api(projects.core.navigation)
    api(projects.core.uiApi)
    api(projects.core.model)
    implementation(libs.kotlinx.serialization.json)
}
