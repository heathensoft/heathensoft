plugins {
    `java-library`
}

val jomlPrimitivesVersion = "1.10.0"
val jomlVersion = "1.10.4"


repositories {
    mavenCentral()
}

dependencies {

    api("org.joml", "joml", jomlVersion)
    api("org.joml", "joml-primitives", jomlPrimitivesVersion);
}