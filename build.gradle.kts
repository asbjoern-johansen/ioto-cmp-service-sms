plugins {
    id("application")
    id("idea")
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "ioto.cmp"
version = "0.0.1"


repositories {
    mavenCentral()
    maven{
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/asbjoern-johansen/ioto-cmp-data-model")
        credentials{
            username = project.findProperty("git.user") as String
            password = project.findProperty("git.token") as String
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    //Webservices
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // https://mvnrepository.com/artifact/org.jsmpp/jsmpp
    implementation("org.jsmpp:jsmpp:3.0.1")

    //Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register("setup"){
    var file = File("gradle.properties")
    if(!file.exists())
    {
        file.createNewFile()
        file.printWriter().use {
            it.println("git.user=[USER NAME HERE]")
            it.println("git.token=[PERSONAL ACCESS TOKEN HERE]")
        }
    }
}

tasks.register("printMetadata") {
    group = "docker"
    description = "Prints project name, group and version for this component"

    doLast {
        println("${project.name}:${project.group}:${project.version}")
    }
}

tasks.register("dockerPrepare") {
    group = "docker"
    description = "Utility function for docker compose"

    dependsOn("bootJar")
}

