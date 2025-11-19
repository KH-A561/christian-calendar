plugins {
    alias(libs.plugins.christian.calendar.android.library)
    alias(libs.plugins.christian.calendar.android.library.jacoco)
    alias(libs.plugins.christian.calendar.android.room)
    alias(libs.plugins.christian.calendar.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ru.akhilko.core.database"
}

dependencies {
    api(projects.core.model)
    api(projects.core.data)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}