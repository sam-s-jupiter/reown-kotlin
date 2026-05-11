plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:${libs.versions.dokka.get()}")
    implementation("com.google.firebase:firebase-appdistribution-gradle:${libs.versions.firebaseAppDistribution.get()}")

    testImplementation("junit:junit:${libs.versions.jUnit.get()}")
}

java {
    toolchain {
        // Local override: dev machine has JDK 17 but not JDK 11. Kotlin still
        // targets JVM 11 bytecode (see kotlinOptions below), so artifacts are
        // identical to a JDK 11 build. Revert to 11 before opening upstream PRs.
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}