plugins {
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.yfy.android.test)
}

android {
    namespace = "com.yfy.basearchitecture.benchmark"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
    flavorDimensions += "contentType"
    productFlavors {
        create("mock") {
            dimension = "contentType"
            buildConfigField("String", "TARGET_APP_PACKAGE", "\"com.yfy.basearchitecture.mock\"")
        }
    }

    buildTypes {
        debug {
            // Optional Settings
        }

        create("release") {
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    testOptions.managedDevices.devices {
        create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            systemImageSource = "aosp"
        }
    }
    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants { variant ->
        variant.enable = (variant.flavorName == "mock" && variant.buildType == "release")
    }
}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
}