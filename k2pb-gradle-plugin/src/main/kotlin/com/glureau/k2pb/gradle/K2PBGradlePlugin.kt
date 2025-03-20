package com.glureau.k2pb.gradle


import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class K2PBGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.google.devtools.ksp")

        val k2pbExt = K2PBExtension()
        target.extensions.add("k2pb", k2pbExt)

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

