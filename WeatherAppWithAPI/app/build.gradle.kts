plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.weatherappwithapi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weatherappwithapi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.coil)
    implementation(libs.play.services.location)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.glance)
    implementation(libs.glide)
    annotationProcessor(libs.glide)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.savedstate.ktx)
    implementation(libs.androidx.annotation.experimental)
    implementation(libs.androidx.versionedparcelable)
    implementation(libs.androidx.vectordrawable)
    implementation(libs.androidx.vectordrawable.animated)
    implementation(libs.androidx.interpolator)
    implementation(libs.androidx.cursoradapter)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.customview)
    implementation(libs.androidx.loader)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.livedata.core.ktx)
    implementation(libs.androidx.appcompat.resources)
    implementation(libs.androidx.ui.geometry)
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.runtime)
    implementation(libs.material)
    implementation(libs.play.services.contextmanager)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}