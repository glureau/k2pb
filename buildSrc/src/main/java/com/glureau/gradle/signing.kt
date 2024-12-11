import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.apply
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.net.URI

private fun Project.signPublicationsIfKeyPresent(publication: MavenPublication) {
    val signingKey: String? = System.getenv("SIGN_KEY")
    val signingKeyPassphrase: String? = System.getenv("SIGN_KEY_PASSPHRASE")

    if (!signingKey.isNullOrBlank()) {
        extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
            sign(publication)
            println("Configured signing for ${publication.name}")
        }
    }
}

fun Project.setupPublishing() {
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    extensions.configure(PublishingExtension::class.java) {
        afterEvaluate {
            publications.filterIsInstance<MavenPublication>()
                .forEach { p ->
                    println("SIGNATURE - ${p.name} - ${p.groupId}:${p.artifactId}:${p.version}")
                    signPublicationsIfKeyPresent(p)
                    p.pom {
                        name.set(project.name)
                        description.set("Kotlin to Protocol Buffers")
                        url.set("https://github.com/glureau/k2pb")

                        licenses {
                            license {
                                name.set("The Apache Software License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                distribution.set("repo")
                            }
                        }

                        developers {
                            developer {
                                id.set("glureau")
                                name.set("Grégory Lureau")
                            }
                        }

                        scm {
                            connection.set("scm:git:git://github.com/glureau/k2pb.git")
                            url.set("https://github.com/glureau/k2pb/tree/master")
                        }
                    }
                }
        }
        repositories {
            maven {
                url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("SO1_USER")
                    password = System.getenv("SO1_PASSWORD")
                }
            }
        }
    }
    afterEvaluate {
        val signTasks = tasks.toList().filterIsInstance<Sign>()
        tasks.toList()
            .filter { it is AbstractPublishToMaven }
            .forEach {
                println("Task ${this.name} depends on signing - $signTasks")
                it.dependsOn(signTasks)
            }
    }
}