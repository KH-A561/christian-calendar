plugins {
    alias(libs.plugins.christian.calendar.android.library)
    alias(libs.plugins.christian.calendar.hilt)
}

android {
    namespace = "ru.akhilko.christian.calendar.sync"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.common)
    
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.tracing.ktx)

    testImplementation(libs.hilt.android.testing)
}
