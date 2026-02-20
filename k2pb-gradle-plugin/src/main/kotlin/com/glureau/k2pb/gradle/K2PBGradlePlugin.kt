package com.glureau.k2pb.gradle


import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class K2PBGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.google.devtools.ksp")

        val k2pbExt = K2PBExtension()
        target.extensions.add("k2pb", k2pbExt)

        target.dependencies.apply {
            val pluginVersion = this@K2PBGradlePlugin.javaClass.`package`?.implementationVersion
                ?: throw IllegalStateException(
                    "Cannot determine K2PB plugin version. " +
                            "Ensure 'implementationVersion' is set in the plugin's build configuration."
                )

            // TODO: This approach doesn't work nicely yet... may need the ksp() extension dependency
            // target.logger.warn("K2PB plugin version: $pluginVersion")
            when {
                target.configurations.any { it.name.contains("kspCommonMainMetadata") } -> {
                    add("kspCommonMainMetadata", "com.glureau.k2pb:k2pb-compiler:$pluginVersion")
                }

                target.configurations.any { it.name.contains("kspJvm") } -> {
                    add("kspJvm", "com.glureau.k2pb:k2pb-compiler:$pluginVersion")
                }

                target.configurations.any { it.name == "ksp" } -> {
                    add("ksp", "com.glureau.k2pb:k2pb-compiler:$pluginVersion")
                }

                else -> {
                    error(
                        "Can't detect kotlin multiplatform or jvm plugins, impossible to add the ksp dependency. " +
                                "Please report your issue on https://github.com/glureau/k2pb/issues with " +
                                "your project configuration."
                    )
                }
            }
        }
        target.afterEvaluate {
            val kspExt = target.extensions.getByType(KspExtension::class.java)
            k2pbExt.protoPackageName?.let { protoPackageName ->
                kspExt.arg("com.glureau.k2pb.protoPackageName", protoPackageName)
            }
            k2pbExt.javaPackage?.let { javaPackage ->
                kspExt.arg("com.glureau.k2pb.javaPackage", javaPackage)
            }
            k2pbExt.javaOuterClassnameSuffix?.let { javaOuterClassnameSuffix ->
                kspExt.arg("com.glureau.k2pb.javaOuterClassnameSuffix", javaOuterClassnameSuffix)
            }
            kspExt.arg("com.glureau.k2pb.emitNullability", k2pbExt.emitNullability.toString())
        }
    }
}

