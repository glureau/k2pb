import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

fun Project.setupPublishing() {
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
