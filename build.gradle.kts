plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("io.github.goooler.shadow") version "8.1.8" // Change back once goooler's PR goes through https://github.com/johnrengelman/shadow/pull/876
}

group = "org.insilicon"
version = "1.0.0"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
    maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
    maven("https://libraries.minecraft.net/") { name = "minecraft-repo" }
    //maven("https://repo.skriptlang.org/releases") { name = "skript" }
    maven("https://maven.enginehub.org/repo/") { name = "enginehub" }
    maven("https://repo.cybercake.net/repository/maven-public/") { name = "cybercake" }
    maven("https://mvn-repo.arim.space/lesser-gpl3/") { name = "arim-mvn-lgpl3" }
    maven("https://mvn-repo.arim.space/gpl3/") { name = "arim-mvn-gpl3" }
    maven("https://mvn-repo.arim.space/affero-gpl3/") { name = "arim-mvn-agpl3" }
    maven("https://repo.crazycrew.us/releases") { name = "crazycrew" }
    maven("https://jitpack.io") { name = "jitpack" }
}

dependencies {
    implementation("net.cybercake.cyberapi:spigot:185")
    implementation("net.cybercake.cyberapi:common:185")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.47")) // Ref: https://github.com/IntellectualSites/bom
    implementation("fr.mrmicky:FastInv:3.0.4")
    implementation("net.dv8tion:JDA:5.0.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")

    //https://maven.enginehub.org/repo/com/sk89q/worldedit/worldedit-bukkit/7.3.0-SNAPSHOT/
    //compileOnly 'com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT'

    compileOnly("com.github.SkriptLang:Skript:2.9.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.10")
    compileOnly("com.mojang:authlib:3.17.30")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("space.arim.libertybans:bans-api:1.1.0-RC2")
    compileOnly("us.crazycrew.crazycrates:api:0.7")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    shadowJar {
        relocate("fr.mrmickey.fastinv", "org.insilicon.fastinv")
        relocate("org.mariadb.jdbc", "org.insilicon.mariadb.jdbc")
        relocate("com.github.benmanes.caffeine", "xyz.prorickey.caffeine")
        relocate("net.dv8tion", "xyz.prorickey.dv8tion")

        destinationDirectory.set(file("${rootDir}/output"))
        archiveClassifier.set("")
        archiveBaseName.set("HiantPlugin")
    }

    compileJava {
        options.release = 21
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
