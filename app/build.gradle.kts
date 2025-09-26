plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // Room 때문에 필요
}

android {
    namespace = "com.example.icebox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fridgeapp2"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        compose = true // ✅ Compose 사용 선언
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // ✅ Kotlin 1.9.0용 Compose Compiler
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // ✅ Jetpack Compose BOM (버전 통합)
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))

    // ✅ Jetpack Compose 기본
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.0")

    // ✅ (선택) 미리보기 도구
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ✅ 기본 라이브러리
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ✅ Room DB
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // ✅ 코루틴 기능 쓸 때 필요!
    kapt("androidx.room:room-compiler:2.6.1")

    // ✅ Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // ✅ WebView
    implementation("androidx.webkit:webkit:1.8.0")

    // LiveData KTX 추가
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")

    // ✅ 테스트
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.compose.material3:material3:1.1.2")

}
