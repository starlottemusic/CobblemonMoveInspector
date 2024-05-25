plugins {
    id("java")
    kotlin("jvm") version ("1.9.22")
    id("fabric-loom") version("1.6-SNAPSHOT")
}

group = "com.starlotte.cobblemon_move_inspector"
version = "1.0.1"


repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://cursemaven.com")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.89.3+1.20.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.17+kotlin.1.9.22")
    modImplementation("com.cobblemon:fabric:1.5.0+1.20.1")

    modImplementation("curse.maven:gravelmon-928129:5337260")
    modImplementation("curse.maven:gravels-extended-battles-930317:5337280")
    modImplementation("curse.maven:architectury-api-419699:5137936")
    modImplementation("curse.maven:midnightlib-488090:4576371")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}