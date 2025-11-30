plugins {
    alias(libs.plugins.christian.calendar.android.library)
    alias(libs.plugins.christian.calendar.hilt)
    id("com.google.devtools.ksp") // Add KSP plugin
}

android {
    namespace = "ru.akhilko.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    tasks.withType<com.google.devtools.ksp.gradle.KspTask>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

// Add KSP configuration
ksp {
    arg("org.jetbrains.kotlin.jvm.target", "1.8")
}

dependencies {
    implementation(projects.core.model)
    api(projects.core.common)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}