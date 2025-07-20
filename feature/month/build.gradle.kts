plugins {
    alias(libs.plugins.christian.calendar.android.feature)
    alias(libs.plugins.christian.calendar.android.library.compose)
}

android {
    namespace = "ru.akhilko.feature.month"
}

dependencies {
    implementation(projects.core.calendar)
    implementation(projects.core.database)
    implementation(projects.core.util)
    implementation(libs.kizitonwose.calendar)

//
//    testImplementation(project(":core:testing"))

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}