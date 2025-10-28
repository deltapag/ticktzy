import com.android.build.api.dsl.SigningConfig
import java.util.Properties
import java.io.FileInputStream

// Load values from local.properties (machine-specific)
val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        FileInputStream(localFile).use { load(it) }
    }
}

fun localProp(key: String, defaultValue: String? = null): String? =
    localProperties.getProperty(key, defaultValue)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "br.com.sttsoft.ticktzy"
    compileSdk = 35

    defaultConfig {
        applicationId = "br.com.sttsoft.ticktzy"
        minSdk = 24
        targetSdk = 34
        versionCode = 13 // Sempre somar +1
        versionName = "1.0.11"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = localProp("keystore.file")
            require(!keystorePath.isNullOrBlank()) { "Define keystore.file in local.properties" }
            
            // Convert Windows path separators to forward slashes for compatibility
            val normalizedPath = keystorePath?.replace("\\", "/")
            storeFile = file(normalizedPath ?: keystorePath)

            storePassword = localProp("keystore.storePassword")
                ?: error("Define keystore.storePassword in local.properties")
            keyAlias = localProp("keystore.keyAlias")
                ?: error("Define keystore.keyAlias in local.properties")
            keyPassword = localProp("keystore.keyPassword")
                ?: error("Define keystore.keyPassword in local.properties")
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
            val useAPI = (localProp("useAPI", "true") ?: "true").lowercase()
            val urlDelta = localProp("urlDelta", "https://pos.deltapag.com.br/")
            val urlAPI = localProp("urlAPI", "https://parseapi.back4app.com/")
            val bearerToken = localProp("api.bearerToken", "9712f0e92fb7a94b55bc3c23875e8951218635fee146b3932393e782991e5b46")
            val parseAppId = localProp("api.parseAppId", "44Ipo5EE0DDLvducULikdSG6tVbnlyNhTWcQlabp")
            val parseApiKey = localProp("api.parseApiKey", "9U3oQCXujFZPGZQzIV8d36WiAL4VcRGxlfdx8wPp")

            buildConfigField("Boolean", "useAPI", useAPI)
            buildConfigField("String", "urlDelta", "\"$urlDelta\"")
            buildConfigField("String", "urlAPI", "\"$urlAPI\"")
            buildConfigField("String", "bearerToken", "\"$bearerToken\"")
            buildConfigField("String", "parseAppId", "\"$parseAppId\"")
            buildConfigField("String", "parseApiKey", "\"$parseApiKey\"")

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            val useAPI = (localProp("useAPI", "true") ?: "true").lowercase()
            val urlDelta = localProp("urlDelta", "https://pos.deltapag.com.br/")
            val urlAPI = localProp("urlAPI", "https://parseapi.back4app.com/")
            val bearerToken = localProp("api.bearerToken", "9712f0e92fb7a94b55bc3c23875e8951218635fee146b3932393e782991e5b46")
            val parseAppId = localProp("api.parseAppId", "44Ipo5EE0DDLvducULikdSG6tVbnlyNhTWcQlabp")
            val parseApiKey = localProp("api.parseApiKey", "9U3oQCXujFZPGZQzIV8d36WiAL4VcRGxlfdx8wPp")

            buildConfigField("Boolean", "useAPI", useAPI)
            buildConfigField("String", "urlDelta", "\"$urlDelta\"")
            buildConfigField("String", "urlAPI", "\"$urlAPI\"")
            buildConfigField("String", "bearerToken", "\"$bearerToken\"")
            buildConfigField("String", "parseAppId", "\"$parseAppId\"")
            buildConfigField("String", "parseApiKey", "\"$parseApiKey\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf("-Xjsr305=strict")
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
