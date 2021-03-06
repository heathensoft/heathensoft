import org.gradle.internal.os.OperatingSystem

plugins {
    id("io.github.heathensoft.project-library")
}

val lwjglVersion = libs.versions.lwjgl

/*
val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else
                "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }                ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) }                           ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}
 */


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":storage"))
    implementation(libs.lwjgl.lwjgl)
    implementation(libs.lwjgl.glfw)
    implementation(libs.lwjgl.opengl)
    //runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    //runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    //runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    //runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
}