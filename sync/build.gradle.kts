plugins {
    alias(libs.plugins.christian.calendar.android.library)
    alias(libs.plugins.christian.calendar.hilt)
}

android {
    namespace = "ru.akhilko.christian.calendar.sync"
}

dependencies {
    implementation(libs.hilt.android.testing)
    implementation(projects.core.data)
}
