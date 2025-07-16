import com.android.build.api.dsl.SigningConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "br.com.sttsoft.ticktzy"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.sttsoft.ticktzy"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "useAPI", "true")
            buildConfigField("String", "urlDelta",  "\"https://pos.deltapag.com.br/\"")
        }
        debug {
            buildConfigField("Boolean", "useAPI", "false")
            buildConfigField("String", "urlDelta",  "\"https://pos.deltapag.com.br/\"")
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation("com.sunmi:printerlibrary:1.0.18")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation(libs.retrofit)
    implementation(libs.retrofitGson)
    implementation(libs.lottie)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}