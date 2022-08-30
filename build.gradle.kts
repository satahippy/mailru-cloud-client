group = "com.github.satahippy"
version = "0.3.0"

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.7.10"
}

val retrofitVersion = "2.9.0"
val junitVersion = "4.13.2"

repositories {
    mavenCentral()
}

dependencies {
    api("com.squareup.retrofit2:retrofit:$retrofitVersion")
    api("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    api("com.squareup.retrofit2:converter-scalars:$retrofitVersion")

    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    testImplementation("junit:junit:$junitVersion")
}

publishing {
    publications {
        create<MavenPublication>("mailru-cloud-client") {
            groupId = group as String
            artifactId = name
            version = version

            from(components["java"])
        }
    }
}