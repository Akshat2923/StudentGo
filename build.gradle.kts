// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("androidx.room") version "2.6.1" apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}