plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8" // Change back once goooler's PR goes through https://github.com/johnrengelman/shadow/pull/876
}

group = "org.insilicon"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
    maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
    maven("https://libraries.minecraft.net/") { name = "minecraft-repo" }
    maven("https://repo.skriptlang.org/releases") { name = "skript" }
    maven("https://maven.enginehub.org/repo/") { name = "enginehub" }
    maven("https://repo.cybercake.net/repository/maven-public/") { name = "cybercake" }
    maven("https://mvn-repo.arim.space/lesser-gpl3/") { name = "arim-mvn-lgpl3" }
    maven("https://mvn-repo.arim.space/gpl3/") { name = "arim-mvn-gpl3" }
    maven("https://mvn-repo.arim.space/affero-gpl3/") { name = "arim-mvn-agpl3" }
    maven("https://jitpack.io") { name = "jitpack" }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")

    implementation("net.cybercake.cyberapi:spigot:185")
    implementation("net.cybercake.cyberapi:common:185")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.47")) // Ref: https://github.com/IntellectualSites/bom
    implementation("fr.mrmicky:FastInv:3.0.4")
    implementation("net.dv8tion:JDA:5.0.0-beta.19")

    //https://maven.enginehub.org/repo/com/sk89q/worldedit/worldedit-bukkit/7.3.0-SNAPSHOT/
    //compileOnly 'com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT'

    compileOnly("com.github.SkriptLang:Skript:2.9.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.10")
    compileOnly("com.mojang:authlib:3.17.30")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("space.arim.libertybans:bans-api:1.1.0-RC2")

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    shadowJar {
        relocate("fr.mrmickey.fastinv", "org.insilicon.fastinv")

        destinationDirectory.set(file("${rootDir}/output"))
        archiveClassifier.set("")
        archiveBaseName.set("HiantPlugin")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}
