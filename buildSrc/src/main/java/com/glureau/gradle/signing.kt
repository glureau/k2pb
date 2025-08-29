import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.io.File
import java.net.URI

private fun Project.signPublicationsIfKeyPresent(publication: MavenPublication) {
    val signingKey: String? = System.getenv("SIGN_KEY")
    val signingKeyPassphrase: String? = System.getenv("SIGN_KEY_PASSPHRASE")

    if (!signingKey.isNullOrBlank()) {
        extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
            sign(publication)
        }
    }
}

fun Project.setupPublishing2() {
    apply(plugin = "com.vanniktech.maven.publish")
    extensions.getByType(MavenPublishBaseExtension::class).apply {
        publishToMavenCentral()
        signAllPublications()
        pom {
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
