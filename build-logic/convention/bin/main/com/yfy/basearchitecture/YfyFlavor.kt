package com.yfy.basearchitecture

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.gradle.internal.dsl.ProductFlavor as InternalProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

@Suppress("EnumEntryName")
enum class YfyFlavor(
    val dimension: FlavorDimension,
    val applicationIdSuffix: String? = null,
    val dataSource: String,
    val baseUrl: String,
    val databaseEnv: String? = null,
    val isDefaultFlavor: Boolean = false
) {
    mock(
        dimension = FlavorDimension.contentType,
        applicationIdSuffix = ".mock",
        dataSource = "mock",
        baseUrl = "https://mock.local/",
        isDefaultFlavor = true
    ),
    dev(
        dimension = FlavorDimension.contentType,
        applicationIdSuffix = ".dev",
        dataSource = "api",
        baseUrl = "https://api-test.example.com/",
        databaseEnv = "test"
    ),
    prod(
        dimension = FlavorDimension.contentType,
        dataSource = "api",
        baseUrl = "https://api.example.com/",
        databaseEnv = "production"
    ),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: YfyFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            YfyFlavor.values().forEach { yfyFlavor ->
                register(yfyFlavor.name) {
                    this.dimension = yfyFlavor.dimension.name
                    flavorConfigurationBlock(this, yfyFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (yfyFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = yfyFlavor.applicationIdSuffix
                        }
                    }
                    if (yfyFlavor.isDefaultFlavor) {
                        (this as? InternalProductFlavor)?.isDefault = true
                    }

                    // Network configuration
                    this.buildConfigField("String", "DATA_SOURCE", "\"${yfyFlavor.dataSource}\"")
                    this.buildConfigField("String", "BASE_URL", "\"${yfyFlavor.baseUrl}\"")
                    yfyFlavor.databaseEnv?.let {
                        this.buildConfigField("String", "DATABASE_ENV", "\"$it\"")
                    }
                }
            }
        }
    }
}