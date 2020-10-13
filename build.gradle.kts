plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.github.aesteve"
version = "4.0.0.Beta3"
val vertxVersion = version

repositories {
    mavenCentral()
}
// Add compatibility
tasks {
    test {
        useJUnitPlatform()
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    fun vertx(module: String) = "io.vertx:vertx-$module:$vertxVersion"
    testImplementation(vertx("redis-client"))
    testImplementation(vertx("web-graphql"))
    testImplementation(vertx("web-client"))
    testImplementation(vertx("rx-java2"))
    testImplementation(vertx("redis-client"))
    testImplementation(vertx("web"))
    testImplementation(vertx("junit5"))

    /*
        "vertx-web-client",
        "vertx-web",
        "vertx-web-graphql",
        "vertx-redis-client",
        "vertx-kafka-client"
     */
}
