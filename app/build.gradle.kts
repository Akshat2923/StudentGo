plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.devtools.ksp")
    id("androidx.room")
}

android {
    namespace = "com.example.studentgo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studentgo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        room {
            schemaDirectory("$projectDir/schemas")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
            }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,LICENSE-notice.md}"
        }
    }
}

dependencies {
    // Start of Room Dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.junit.ktx)
    annotationProcessor(libs.room.compiler)
    // Kotlin Symbol Processing libraries
    ksp(libs.room.compiler)
    // This is the KTX dependency needed for coroutines support
    implementation(libs.androidx.room.ktx)
    // End of Room Dependencies

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.activity)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // leaderboard
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation(libs.firebase.database.ktx)

    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // AndroidX Test Core library
    testImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")

    // AndroidX Test Rules library
    testImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // AndroidX Test Runner library
    testImplementation("androidx.test:runner:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")

    // AndroidX Fragment Testing library
    debugImplementation("androidx.fragment:fragment-testing:1.6.1")

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")

    // Mockito

    // Byte Buddy (if explicitly needed)
    testImplementation("net.bytebuddy:byte-buddy:1.14.10")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.14.10")


    // Mockito Android support
    androidTestImplementation("org.mockito:mockito-android:4.11.0")

    // JUnit
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("junit:junit:4.13.2")

    // Arch Core Testing
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.11.1")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.20")

    // Firebase dependencies for testing
    testImplementation("com.google.firebase:firebase-auth:22.1.1")
    testImplementation("com.google.firebase:firebase-firestore:24.7.0")

    // Remove duplicate dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.5.0")
    androidTestImplementation("androidx.test.runner:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Robolectric and other test libraries
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("androidx.test:core:1.5.0")
}

secrets {
    // To add your Maps API key to this project:
    // ¢1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"
    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}