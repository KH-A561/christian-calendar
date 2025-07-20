plugins {
    alias(libs.plugins.christian.calendar.android.application)
    alias(libs.plugins.christian.calendar.android.application.compose)
    alias(libs.plugins.christian.calendar.android.application.flavors)
    alias(libs.plugins.christian.calendar.android.application.jacoco)
    alias(libs.plugins.christian.calendar.android.application.firebase)
    alias(libs.plugins.christian.calendar.hilt)

    id("kotlinx-serialization")
}

android {
    namespace = "ru.akhilko.christian_calendar"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.akhilko.christian_calendar"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.feature.day)
    implementation(projects.feature.week)
    implementation(projects.feature.month)
    implementation(projects.feature.search)
    implementation(projects.feature.settings)

    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.database)
    implementation(projects.core.model)

    implementation(libs.androidx.activity.compose)
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

    ksp(libs.hilt.compiler)

    implementation("org.threeten:threeten-extra:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation(libs.androidx.appcompat)
}