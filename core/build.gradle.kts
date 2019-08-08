import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar

val springBootVersion = "2.1.7.RELEASE"
val jupiterVersion = "5.5.1"
val commonsVersion = "3.6"

plugins {
    kotlin("jvm") version "1.3.41"
    kotlin("plugin.allopen") version "1.3.41"
    kotlin("plugin.noarg") version "1.3.41"
    kotlin("plugin.spring") version "1.3.41"
    id("org.springframework.boot") version "2.1.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

group = "com.osmp4j"
version = "0.0.1"


repositories {
    mavenCentral()
    jcenter()
    maven("https://repo.spring.io/snapshot")
    maven("https://repo.spring.io/milestone")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("commons-net:commons-net:$commonsVersion")
    implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-amqp:$springBootVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

tasks.getByName<BootJar>("bootJar") {
    mainClassName = "com.osmp4j.CoreApplication"
}
