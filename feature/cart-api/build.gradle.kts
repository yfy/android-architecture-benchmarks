plugins {
    alias(libs.plugins.yfy.android.library)
    id("kotlinx-serialization")
}

android {
    namespace = "com.yfy.basearchitecture.feature.cart.api"
}

dependencies {
    api(projects.core.navigation)
    api(projects.core.uiApi)
    api(projects.core.model)
    api(projects.feature.productApi)
    implementation(libs.kotlinx.serialization.json)
}
