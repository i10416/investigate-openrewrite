package com.example

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.gradle.api.plugins.JavaPluginExtension

@Suppress("Unused")
class CustomPlugin: Plugin<Project> {

  override fun apply(target: Project) {
    target.plugins.apply("java")
    target.extensions.create("rewrite", MyRewriteExtension::class.java)
    val conf = target.configurations.maybeCreate("rewrite")
    val sourceSets = target.extensions.findByType(JavaPluginExtension::class.java)!!.sourceSets
    val rewrite = target.tasks.register("myrewrite", MyRewriteTask::class.java)
    rewrite.configure {
      sourceSets.forEach {
        sourceFileCollection = sourceFileCollection.plus(it.allSource)
        dependencyFileCollection = dependencyFileCollection.plus(it.compileClasspath)
        dependencyFileCollection = dependencyFileCollection.plus(it.runtimeClasspath)
      }
      dependsOn(conf)
    }


    sourceSets.all {
      val compile = target.tasks.named(compileJavaTaskName)
      rewrite.configure {
        dependsOn(compile)
      }
    }

  }
}
