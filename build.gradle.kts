buildscript {
    val kotlinVersion by extra ("1.6.10")
    repositories {
        google()
        mavenCentral()
        maven(url="https://storage.googleapis.com/r8-releases/raw")
    }
    dependencies {
        classpath("com.android.tools:r8:3.3.3-dev")
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://api.xposed.info")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}