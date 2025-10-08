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
        versionCode = 12 // Sempre somar +1
        versionName = "1.0.10"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("D:\\ticktzy\\ticketzy_keystore.jks") // caminho para seu .jks
            storePassword = "ticketzy"
            keyAlias = "ticketzy"
            keyPassword = "ticketzy"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "useAPI", "true")
            buildConfigField("String", "urlDelta", "\"https://pos.deltapag.com.br/\"")
            buildConfigField("String", "urlAPI", "\"https://parseapi.back4app.com/\"")

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            buildConfigField("Boolean", "useAPI", "true")
            buildConfigField("String", "urlDelta", "\"https://pos.deltapag.com.br/\"")
            buildConfigField("String", "urlAPI", "\"https://parseapi.back4app.com/\"")
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
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.material)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.printer)
    implementation(libs.coil)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

    implementation(libs.logInterceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)
    implementation(libs.lottie)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
