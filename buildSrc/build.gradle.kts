plugins {
  // This is necessary to use kotlin dsl in this build
  `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
      implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:latest.release"))
      implementation("org.openrewrite:rewrite-java")
      runtimeOnly("org.openrewrite:rewrite-java-21")
      testImplementation("org.openrewrite:rewrite-test")
      testImplementation("org.junit.jupiter:junit-jupiter-api:latest.release")
      testImplementation("org.junit.jupiter:junit-jupiter-params:latest.release")
      testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")
}


gradlePlugin {
    plugins {
        register("CustomPlugin") {
            id = "com.example.custom-plugin"
            implementationClass = "com.example.CustomPlugin"
        }
    }
}
