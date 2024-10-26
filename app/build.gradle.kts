plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.app_quanly_hocsinh_sinhvien"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app_quanly_hocsinh_sinhvien"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //FireBase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-database")

    //SweetAlertDialog
    implementation ("com.github.f0ris.sweetalert:library:1.5.6")
    implementation ("com.afollestad.material-dialogs:core:3.3.0")

    //CiclerImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Glide: Load image from URL
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:glide:4.16.0")

    //CircularProgressIndicator (Material Design Components)
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("androidx.appcompat:appcompat:1.4.0") // Dùng phiên bản ổn định mới nhất
    implementation ("com.google.android.material:material:1.5.0") // hoặc phiên bản mới nhất

    //Clans Fab github
    implementation ("com.github.clans:fab:1.6.4")



}