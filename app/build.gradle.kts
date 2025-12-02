plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.miapp.warmigomail"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.miapp.warmigomail"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    // Configuración para excluir archivos duplicados
    packaging {
        resources {
            excludes += setOf(
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.md"
            )
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Librerías JavaMail para envío de correos
    //noinspection UseTomlInstead
    implementation("com.sun.mail:android-mail:1.6.7")
    //noinspection UseTomlInstead
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}