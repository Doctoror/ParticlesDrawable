import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.signing)
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())
}

android {

    namespace = "com.doctoror.particlesdrawable"

    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {

        minSdk = libs.versions.androidMinSdk.get().toInt()
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

    implementation(libs.annotations)
}

publishing {

    publications {
        register<MavenPublication>("release") {
            groupId = project.property("GROUP").toString()
            artifactId = project.property("POM_ARTIFACT_ID").toString()
            version = project.property("VERSION_NAME").toString()

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
                name = project.property("POM_NAME").toString()
                packaging = project.property("POM_PACKAGING").toString()
                description = project.property("POM_DESCRIPTION").toString()
                url = project.property("POM_URL").toString()

                scm {
                    url = project.property("POM_SCM_URL").toString()
                    connection = project.property("POM_SCM_CONNECTION").toString()
                    developerConnection = project.property("POM_SCM_DEV_CONNECTION").toString()
                }

                licenses {
                    license {
                        name = project.property("POM_LICENCE_NAME").toString()
                        url = project.property("POM_LICENCE_URL").toString()
                        distribution = project.property("POM_LICENCE_DIST").toString()
                    }
                }

                developers {
                    developer {
                        id = project.property("POM_DEVELOPER_ID").toString()
                        name = project.property("POM_DEVELOPER_NAME").toString()
                    }
                }
            }

            artifacts {
                artifact(
                    "${
                        project.layout.buildDirectory.asFile.get().canonicalPath
                    }/outputs/aar/library-production-release.aar"
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