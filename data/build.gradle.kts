import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.google.secrets)
}

android {
    namespace = "al.bruno.weather.data"
    compileSdk = 36

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Koin
    implementation(platform(libs.io.koin.bom))
    implementation(libs.io.koin.android)
    // Koin annotations
    implementation(libs.io.koin.annotations)
    ksp(libs.io.koin.ksp.compiler)
    // Room
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.io.ktor.client.core)
    implementation(libs.io.ktor.client.cio)
    implementation(libs.io.ktor.client.auth)
    implementation(libs.io.ktor.client.content.negotiation)
    implementation(libs.io.ktor.client.logging)
    implementation(libs.io.ktor.client.serialization)

    implementation(libs.org.jetbrains.kotlinx.serialization.json)

    implementation(libs.com.google.play.services.location)

    implementation(project(":domain"))

    testImplementation(libs.io.mockk)
    testImplementation(libs.org.jetbrains.kotlinx.serialization.json)
    testImplementation(libs.org.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.io.ktor.client.mock)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}