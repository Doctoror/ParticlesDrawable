plugins {
    id("com.android.application")
}

android {

    namespace = "com.doctoror.particlesdrawable.demo"

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.doctoror.particlesdrawable.demo"

        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()

        versionCode = project.property("VERSION_CODE").toString().toInt()
        versionName = project.property("VERSION_NAME").toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {

        getByName("debug") {
            isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
        }
    }

    // Added only to be able to compile with library variants
    flavorDimensions += listOf("default")

    productFlavors {
        create("exposed") {
        }

        create("production") {
        }
    }
}

dependencies {
    implementation(project(":library"))
}
