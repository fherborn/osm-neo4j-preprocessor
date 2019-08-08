import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springBootVersion = "2.1.7.RELEASE"
val jupiterVersion = "5.5.1"
val commonsVersion = "3.6"
val httpClientVersion = "4.5.9"

plugins {
    kotlin("jvm") version "1.3.41"
    kotlin("plugin.allopen") version "1.3.41"
    kotlin("plugin.noarg") version "1.3.41"
    kotlin("plugin.spring") version "1.3.41"
    id("org.springframework.boot") version "2.1.7.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    `maven-publish`
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
    implementation("org.apache.httpcomponents:httpclient:$httpClientVersion")
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

//tasks.getByName<BootJar>("bootJar") {
//    enabled = true
//    mainClassName = "com.osmp4j.CoreApplication"
//}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    kotlinOptions.jvmTarget = "1.8"
}
