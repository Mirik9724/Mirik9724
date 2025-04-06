import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    //id "org.jetbrains.kotlin.jvm" version "2.1.20"
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 17
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("titanium") {
            sourceSet("client")
        }
    }
}
val minecraftVersion: String by project



repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx/maven") // <--- Важно
    maven{
        url = uri("${rootDir}/libs")
    }


    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

dependencies {
    if (minecraftVersion == "1.19") {
        minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
        mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
        modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    } else if (minecraftVersion == "1.21.4") {
        minecraft("com.mojang:minecraft:1.21.4")
        mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
        modImplementation("net.fabricmc:fabric-language-kotlin:1.13.2+kotlin.2.1.20")
    }

    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    implementation("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:2.5.1+bf2a60eb2d")
    implementation("net.fabricmc.fabric-api:fabric-networking-api-v1:4.0.7+9342ba644f")

//    implementation("io.ktor:ktor-client-core-2.2.0")
    implementation("io.ktor:ktor-client-cio:2.2.0")
    implementation("io.ktor:ktor-client-json:2.2.0")
    implementation("io.ktor:ktor-client-serialization:2.2.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.0")
    implementation("org.json:json:20210307") // Для работы с JSONObject
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
