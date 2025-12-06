plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.example.notificationthriller"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notificationthriller"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // For production, use keystore.properties file with actual credentials
            // For demo/testing, we use debug keystore
            storeFile = file(System.getenv("RELEASE_KEYSTORE_PATH") ?: "${System.getProperty("user.home")}/.android/debug.keystore")
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: "android"
        }
    }

    buildTypes {
        debug {
            // Note: applicationIdSuffix not used to avoid google-services.json mismatch
            versionNameSuffix = "-debug"
            isDebuggable = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"

        // Enable warnings as errors for production-ready code
        allWarningsAsErrors = false // Set to true when all warnings are fixed

        freeCompilerArgs =
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
            )
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        ignoreWarnings = false
        // Set to true when all warnings are fixed
        warningsAsErrors = false

        // Disable specific checks if needed (currently allowing some warnings)
        // Keeping resources for future use
        disable += setOf("UnusedResources")
    }

    // Prevent jlink issues with Android Gradle Plugin
    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // SharedPreferences (DataStore alternative for simpler use)
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Firebase Analytics
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Google Play Billing
    implementation("com.android.billingclient:billing-ktx:6.1.0")
}

// Ktlint configuration
ktlint {
    version.set("1.0.1")
    android.set(true)
    // Allow warnings for now, will fix incrementally
    ignoreFailures.set(true)

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

// Detekt configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false

    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))

    source.setFrom(files("src/main/java", "src/main/kotlin"))

    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
    }
}
