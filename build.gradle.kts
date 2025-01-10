buildscript {
    repositories {
        google()  // Google Maven deposunu ekler
        mavenCentral()  // Maven Central deposunu ekler
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
