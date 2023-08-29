#!/sbin/sh

JAVA_HOME=/opt/android-studio/jbr ./gradlew library:publishReleasePublicationToMavenRepository opengl:publishReleasePublicationToMavenRepository
