plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

tasks {
    plugins.withId("java") {
        register("javadocJar", Jar::class) {
            dependsOn(named("dokkaHtml"))
            archiveClassifier.set("javadoc")
            from("${layout.buildDirectory}/dokka/html")
        }
        register("sourceJar", Jar::class) {
            archiveClassifier.set("sources")
            from(((project as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer).getByName("main").allSource)
        }
    }
}

// --- Aster internal Nexus (apx-nexus.toolsapple.net, HTTPS only) -----------
// Credentials read from rootProject/local.properties (nexus.username, nexus.password)
// or env vars (MAVEN_ACCOUNT / MAVEN_PASSWORD), matching Android-APX's
// scripts/nexus.gradle convention. local.properties is gitignored — never
// commit credentials.
val apolloLocalProps = java.util.Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val apolloNexusUser: String? =
    apolloLocalProps.getProperty("nexus.username") ?: System.getenv("MAVEN_ACCOUNT")
val apolloNexusPassword: String? =
    apolloLocalProps.getProperty("nexus.password") ?: System.getenv("MAVEN_PASSWORD")

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "Apollo"
                val isSnapshot = (extra.get(KEY_PUBLISH_VERSION)?.toString() ?: "").endsWith("SNAPSHOT")
                url = uri(
                    if (isSnapshot) "https://apx-nexus.toolsapple.net/repository/maven-snapshots"
                    else "https://apx-nexus.toolsapple.net/repository/android"
                )
                credentials {
                    username = apolloNexusUser
                    password = apolloNexusPassword
                }
            }
        }
        publications {
            register<MavenPublication>("mavenJvm") {
                plugins.withId("java") {
                    from(components["java"])
                    artifact(tasks.getByName("sourceJar"))
                    artifact(tasks.getByName("javadocJar"))
                }

                plugins.withId("java-platform") {
                    from(components["javaPlatform"])
                }

                groupId = extra.properties[KEY_PUBLISH_GROUP]?.toString() ?: DEFAULT_PUBLISH_GROUP
                artifactId = requireNotNull(extra.get(KEY_PUBLISH_ARTIFACT_ID)).toString()
                version = requireNotNull(extra.get(KEY_PUBLISH_VERSION)).toString()

                pom {
                    name.set("Reown ${requireNotNull(extra.get(KEY_SDK_NAME))}")
                    description.set("${requireNotNull(extra.get(KEY_SDK_NAME))} SDK for Reown")
                    url.set("https://github.com/reown-com/reown-kotlin")

                    licenses {
                        license {
                            name.set("WALLETCONNECT COMMUNITY LICENSE")
                            url.set("https://github.com/reown-com/reown-kotlin/blob/master/LICENSE")
                        }
                        license {
                            name.set("SQLCipher Community Edition")
                            url.set("https://www.zetetic.net/sqlcipher/license/")
                        }
                    }

                    developers {
                        developer {
                            id.set("KotlinSDKTeam")
                            name.set("Reown Kotlin")
                            email.set("mobile@reown.com ")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/reown-com/reown-kotlin.git")
                        developerConnection.set("scm:git:ssh://github.com/reown-com/reown-kotlin.git")
                        url.set("https://github.com/reown-com/reown-kotlin")
                    }
                }
            }
        }
    }
}

// Skip GPG signing when no key is configured. Apollo internal Nexus does not
// require signed artifacts; upstream Sonatype Central does. CI still signs by
// providing SIGNING_KEY env var.
val hasSigningKey = !System.getenv("SIGNING_KEY").isNullOrBlank()
signing {
    isRequired = hasSigningKey
    if (hasSigningKey) {
        useInMemoryPgpKeys(
            System.getenv("SIGNING_KEY_ID"),
            System.getenv("SIGNING_KEY"),
            System.getenv("SIGNING_PASSWORD")
        )
        sign(publishing.publications)
    }
}