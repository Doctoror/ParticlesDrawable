import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.android")
    id("signing")
}

android {

    namespace = "com.doctoror.particlesdrawable.opengl"

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {

        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        checkAllWarnings = true
    }

    flavorDimensions += listOf("default")

    productFlavors {
        create("exposed") {
            proguardFile("proguard-configurable-keep.pro")
        }

        create("production") {
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
            proguardFile("proguard-keep-as-api.pro")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    testImplementation(libs.test.core)
    testImplementation(libs.junit.vintage)
    testImplementation(libs.kotlin.stdlib)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.opengl.android)

    implementation(libs.annotations)
    implementation(project(":library"))
}

publishing {

    publications {
        register<MavenPublication>("release") {
            groupId = project.properties["GROUP"].toString()
            artifactId = project.properties["POM_ARTIFACT_ID"].toString()
            version = project.properties["VERSION_NAME"].toString()

            repositories {
                maven {
                    setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")

                    credentials {
                        val properties = Properties().apply {
                            load(FileInputStream(File(rootProject.rootDir, "local.properties")))
                        }

                        username = properties.getProperty("releaseRepositoryUsername")
                        password = properties.getProperty("releaseRepositoryPassword")
                    }
                }
            }

            pom {
                name = project.properties["POM_NAME"].toString()
                packaging = project.properties["POM_PACKAGING"].toString()
                description = project.properties["POM_DESCRIPTION"].toString()
                url = project.properties["POM_URL"].toString()

                scm {
                    url = project.properties["POM_SCM_URL"].toString()
                    connection = project.properties["POM_SCM_CONNECTION"].toString()
                    developerConnection = project.properties["POM_SCM_DEV_CONNECTION"].toString()
                }

                licenses {
                    license {
                        name = project.properties["POM_LICENCE_NAME"].toString()
                        url = project.properties["POM_LICENCE_URL"].toString()
                        distribution = project.properties["POM_LICENCE_DIST"].toString()
                    }
                }

                developers {
                    developer {
                        id = project.properties["POM_DEVELOPER_ID"].toString()
                        name = project.properties["POM_DEVELOPER_NAME"].toString()
                    }
                }
            }

            artifacts {
                artifact(
                    "${
                        project.layout.buildDirectory.asFile.get().canonicalPath
                    }/outputs/aar/opengl-production-release.aar"
                )
            }

            afterEvaluate {
                from(components.find { it.name == "release" })
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["release"])
}

tasks.named("publishReleasePublicationToMavenRepository") {
    mustRunAfter("bundleProductionReleaseAar")
}