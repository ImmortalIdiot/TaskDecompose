import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.ii.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 29

        buildFeatures.buildConfig = true

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

        all {
            val authToken = "AUTH_TOKEN"
            val authUrl = "AUTH_URL"
            val baseUrl = "BASE_URL"

            buildConfigField("String", authToken, "\"MDE5OWM4YjQtMmY1Ni03ZTNiLTk3MDAtMDE3Mzg5MmRiNTkzOjdmNGM4YjYwLTlhMDktNDkwZS1hMzlhLTlkZjMwNjQ5ODE4MQ==\"")
            buildConfigField("String", authUrl, "\"https://ngw.devices.sberbank.ru:9443/api/v2/oauth\"")
            buildConfigField("String", baseUrl, "\"https://gigachat.devices.sberbank.ru/api/v1/chat/completions\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.koin.android)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
