plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.github.kwertyXS.birthdayCheckerMobile"
    compileSdk = 37

    buildFeatures.buildConfig = true
    defaultConfig {
        applicationId = "com.github.kwertyXS.birthdayCheckerMobile"
        minSdk = 24
        targetSdk = 37
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
            buildConfigField("boolean", "DEBUG_MODE", "false")
        }
        debug {
            buildConfigField("boolean", "DEBUG_MODE", "true")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // 1. Базовые библиотеки
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // 2. СОВРЕМЕННЫЙ LIFECYCLE (Обновили до 2.8.0+, чтобы он подходил под Kotlin 2.0)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation(libs.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation) // Обновили эту строку!

    // 3. JETPACK COMPOSE (Явно указываем стабильные современные версии без BOM, чтобы избежать конфликтов)
    val composeVersion = "1.7.0" // Стабильная версия под Kotlin 2.0+
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-graphics:$composeVersion")
    implementation("androidx.compose.ui:ui-text:$composeVersion")
    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.material3:material3:1.3.0") // Современный Material 3
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    // Навигация
    implementation("androidx.navigation:navigation-compose:2.8.0-beta02")

    // 4. Авторизация и Сервисы
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.room.ktx)

    // 5. Сеть
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 6. HILT ДЛЯ DI
    implementation("com.google.dagger:hilt-android:2.60.1")
    ksp("com.google.dagger:hilt-compiler:2.60.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Тесты
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

    implementation(libs.androidx.datastore.preferences) // datastore
}