group = "com.github.satahippy"
version = "0.1.0.RELEASE"

buildscript {
    extra["kotlinVersion"] = "1.1.4"
    extra["junitVersion"] = "4.12"
    extra["retrofitVersion"] = "2.3.0"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
    }
}

apply {
    plugin("kotlin")
}

configure<JavaPluginConvention> {
    setSourceCompatibility(1.6)
    setTargetCompatibility(1.6)
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlinVersion"]}")
    compile("com.squareup.retrofit2:retrofit:${extra["retrofitVersion"]}")
    compile("com.squareup.retrofit2:converter-gson:${extra["retrofitVersion"]}")
    compile("com.squareup.retrofit2:converter-scalars:${extra["retrofitVersion"]}")

    testCompile("junit:junit:${extra["junitVersion"]}")
}