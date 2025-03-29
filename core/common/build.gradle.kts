plugins {
    alias(libs.plugins.christian.calendar.jvm.library)
    alias(libs.plugins.christian.calendar.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}