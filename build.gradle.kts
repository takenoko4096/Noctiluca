import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom")
    id("com.github.gmazzo.buildconfig") version "6.0.9"
    `maven-publish`
}

version = "1.0-SNAPSHOT"
group = "io.github.takenoko4096"

val modName = "${project.property("mod_name")}"

base {
    archivesName.set(modName)
}

val javaVersion = 25

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register(modName.lowercase()) {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

fabricApi {

}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")

    implementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    implementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")

    include(api("io.github.takenoko4096:mojangson-bridge:0.1.2")!!)
}

buildConfig {
    sourceSets {
        getByName("main") {
            packageName("io.github.takenoko4096.starlight")
            buildConfigField(
                "String",
                "${modName.uppercase()}_VERSION",
                "\"${project.version}\""
            )
            buildConfigField(
                "String",
                "MINECRAFT_VERSION",
                "\"${project.property("minecraft_version")}\""
            )
            buildConfigField(
                "String",
                "FABRIC_LOADER_VERSION",
                "\"${project.property("loader_version")}\""
            )
            buildConfigField(
                "String",
                "FABRIC_API_VERSION",
                "\"${project.property("fabric_api_version")}\""
            )
            buildConfigField(
                "String",
                "KOTLIN_LOADER_VERSION",
                "\"${project.property("kotlin_loader_version")}\""
            )
            buildConfigField(
                "Int",
                "JAVA_VERSION",
                "$javaVersion"
            )
            buildConfigField(
                "String",
                "FABRIC_LOOM_VERSION",
                 "\"${project.property("loom_version")}\""
            )
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        inputs.property("minecraft_version", project.property("minecraft_version"))
        inputs.property("loader_version", project.property("loader_version"))
        inputs.property("fabric_api_version", project.property("fabric_api_version"))
        inputs.property("java_version", javaVersion)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraft_version" to project.property("minecraft_version")!!,
                "loader_version" to project.property("loader_version")!!,
                "kotlin_loader_version" to project.property("kotlin_loader_version")!!,
                "fabric_api_version" to project.property("fabric_api_version")!!,
                "java_version" to javaVersion.toString()
            )
        }
    }

    withType<JavaCompile>().configureEach {
        // ensure that the encoding is set to UTF-8, no matter what the system default is
        // this fixes some edge cases with special characters not displaying correctly
        // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
        // If Javadoc is generated, this must be specified in that task too.
        options.encoding = Charsets.UTF_8.name()
        options.release.set(javaVersion)
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }

    register<Copy>("copyJar") {
        val jarTask = named<Jar>("jar")

        dependsOn(jarTask)

        from(jarTask.flatMap { it.archiveFile })

        into("${System.getProperty("user.home").replace(File.separatorChar, '/')}/AppData/Roaming/.minecraft/mods")
    }

    named("build") {
        finalizedBy("copyJar", "publishToMavenLocal")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = project.group as String
            artifactId = project.base.archivesName.get()
            version = project.version as String
        }
    }
}
