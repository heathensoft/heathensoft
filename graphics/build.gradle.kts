
plugins {
    id("io.github.heathensoft.project-library")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":math"))
    implementation(project(":storage"))
    implementation(libs.lwjgl.lwjgl)
    implementation(libs.lwjgl.opengl)
    implementation(libs.lwjgl.stb)
}
