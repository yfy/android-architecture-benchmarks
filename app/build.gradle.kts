import com.yfy.basearchitecture.YfyBuildType

plugins {
    alias(libs.plugins.yfy.android.application)
    alias(libs.plugins.yfy.android.application.compose)
    alias(libs.plugins.yfy.android.application.flavors)
    alias(libs.plugins.yfy.android.application.jacoco)
    alias(libs.plugins.yfy.hilt)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        applicationId = "com.yfy.basearchitecture"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.yfy.basearchitecture.benchmark.YfyTestRunner"
    }

    buildTypes {
        buildTypes {
            debug {
                applicationIdSuffix = YfyBuildType.DEBUG.applicationIdSuffix
            }
            release {
                isMinifyEnabled = false
                applicationIdSuffix = YfyBuildType.RELEASE.applicationIdSuffix
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
                signingConfig = signingConfigs.named("debug").get()
            }
        }
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.yfy.basearchitecture"
}

androidComponents {
    onVariants { variant ->
        if (variant.flavorName == "prod" && variant.buildType == "release") {
            variant.packaging.resources.excludes.add("**.txt")
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logging.timber)

    // Social Authentication
    implementation(libs.google.play.services.auth)
    implementation(libs.facebook.login)
    implementation(libs.facebook.android.sdk)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.kotlin.test)

    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)

    //core
    implementation(projects.core.networkApi)
    implementation(projects.core.networkImpl)
    implementation(projects.core.analyticsApi)
    implementation(projects.core.analyticsImpl)
    implementation(projects.core.databaseApi)
    implementation(projects.core.databaseImpl)
    implementation(projects.core.datastoreApi)
    implementation(projects.core.datastoreImpl)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.navigation)
    implementation(projects.core.notificationApi)
    implementation(projects.core.notificationImpl)
    implementation(projects.core.uiApi)
    implementation(projects.core.uiImpl)

    //feature
    implementation(projects.feature.productApi)
    implementation(projects.feature.cartApi)
    implementation(projects.feature.chatApi)

    //product
    //implementation(projects.feature.productImpl)
    implementation(projects.feature.productImplMvp)
    //implementation(projects.feature.productImplMvc)
    //implementation(projects.feature.productImplClassicmvvm)
    //implementation(projects.feature.productImplMvi)

    //cart
    //implementation(projects.feature.cartImpl)
    implementation(projects.feature.cartImplMvp)
    //implementation(projects.feature.cartImplMvc)
    //implementation(projects.feature.cartImplClassicmvvm)
    //implementation(projects.feature.cartImplMvi)

    //chat
    //implementation(projects.feature.chatImpl)
    implementation(projects.feature.chatImplMvp)
    //implementation(projects.feature.chatImplMvc)
    //implementation(projects.feature.chatImplClassicmvvm)
    //implementation(projects.feature.chatImplMvi)
}