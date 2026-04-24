plugins {
    alias(libs.plugins.christian.calendar.android.library)
    alias(libs.plugins.christian.calendar.hilt)
}

android {
    namespace = "ru.akhilko.core.datastore"
}

dependencies {
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.hilt.android)
}
