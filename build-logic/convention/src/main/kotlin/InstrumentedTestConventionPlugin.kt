import com.yfy.basearchitecture.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class InstrumentedTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.android")
            dependencies {
                "androidTestImplementation"(libs.findLibrary("kotlinx.coroutines.test").get())
                "androidTestImplementation"(libs.findLibrary("junit").get())
                "androidTestImplementation"(libs.findLibrary("turbine").get())
                "androidTestImplementation"(libs.findLibrary("mockk").get())
                "androidTestImplementation"(libs.findLibrary("mockk-android").get())
                "androidTestImplementation"(libs.findLibrary("mockito-core").get())
                "androidTestImplementation"(libs.findLibrary("mockito-junit").get())
                "androidTestImplementation"(libs.findLibrary("mockito-android").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-espresso-core").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-rules").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-runner").get())
                "androidTestImplementation"(libs.findLibrary("androidx-test-ext").get())
            }
        }
    }
}