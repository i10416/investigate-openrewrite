package com.example

import org.openrewrite.ExecutionContext
import org.openrewrite.NlsRewrite
import org.openrewrite.Recipe
import org.openrewrite.TreeVisitor
import org.openrewrite.java.JavaIsoVisitor
import org.openrewrite.java.JavaTemplate
import org.openrewrite.java.tree.J

class MyRecipe: Recipe() {
    override fun getDisplayName(): @NlsRewrite.DisplayName String {
        return "MyRecipe"
    }

    override fun getDescription(): @NlsRewrite.Description String {
        return "My sample Recipe to add a `hello` method to a class"
    }
    private val hello = JavaTemplate.builder("public String hello() { return \"Hello from #{}!\"; }").build()

    override fun getVisitor(): TreeVisitor<*, ExecutionContext> {
        return object: JavaIsoVisitor<ExecutionContext>() {
            override fun visitClassDeclaration(classDecl: J.ClassDeclaration, p: ExecutionContext): J.ClassDeclaration {
                val done = classDecl.body.statements.stream()
                    .filter {it is J.MethodDeclaration}
                    .map { it as J.MethodDeclaration }
                    .map { it.simpleName }
                    .anyMatch { it == "hello" }
                if (done) {
                    return classDecl
                }else {
                    return hello.apply(
                        updateCursor(classDecl),
                        classDecl.body.coordinates.addMethodDeclaration(Comparator.comparing(J.MethodDeclaration::getSimpleName)),
                        classDecl.name.simpleName
                    )
                }

            }
        }
    }
}

