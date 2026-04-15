package com.example

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ProjectLayout
import org.openrewrite.java.JavaParser
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.openrewrite.InMemoryExecutionContext
import org.openrewrite.internal.InMemoryLargeSourceSet
import java.io.FileOutputStream
import javax.inject.Inject


abstract class MyRewriteTask @Inject constructor (
    private val projectLayout: ProjectLayout,
): DefaultTask() {
    init {
        group = "rewrite"
        description = "rewrite my source code"
    }

    // assumption: java source set paths are added
    @get:InputFiles
    abstract val sourceFileCollection: ConfigurableFileCollection
    @get:InputFiles
    abstract val dependencyFileCollection: ConfigurableFileCollection




    @TaskAction
    fun run() {
        val ctx = InMemoryExecutionContext()
        val recipe = MyRecipe()
        val sources = sourceFileCollection.files.map{it.toPath()}
        val deps = dependencyFileCollection.files.map {it.toPath()}
        val p = JavaParser.fromJavaVersion().classpath(deps)
            .build()
        val result = p.parse(sources,null,ctx)
        val k = recipe.run(InMemoryLargeSourceSet(result.toList()),ctx)
        k.changeset.allResults.forEach { result ->
            result.after?.also { after ->
                FileOutputStream(after.sourcePath.toFile()).use {
                    it.write(after.printAllAsBytes())
                }
            }
        }
    }
}