plugins {
    `java-library`
    id("com.gradleup.shadow") version("9.3.1")
    id("xyz.jpenilla.run-paper") version("3.0.2")
}

group = "org.lushplugins"
version = "2.0.1"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/") // Paper
    maven("https://repo.lushplugins.org/snapshots/") // LushLib
    maven("https://repo.codemc.io/repository/maven-releases/") // PacketEvents
    maven("https://maven.pvphub.me/tofaa") // EntityLib
    maven("https://maven.enginehub.org/repo/") // WorldGuard
}

dependencies {
    // Dependencies
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // Libraries
    implementation("org.lushplugins:LushLib:0.10.89")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.16")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.16")
    implementation("io.github.tofaa2:spigot:3.1.0-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

    registerFeature("optional") {
        usingSourceSet(sourceSets["main"])
    }

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        minimize()

        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        filesMatching("plugin.yml") {
            expand(project.properties)
        }

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }

    runServer {
        minecraftVersion("1.21.11")

        downloadPlugins {
            modrinth("packetevents", "2.11.2+spigot")
            modrinth("worldedit", "CkT32vix")
            modrinth("worldguard", "7.0.15")
            modrinth("viaversion", "5.7.1")
            modrinth("viabackwards", "5.7.1")
        }
    }
}
