plugins {
    id("io.github.heathensoft.project-library")
}

dependencies {
    api(files("external/joml-1.10.4.jar"))
    api(files("external/joml-primitives-1.10.0.jar"))
}

tasks.jar {
    //manifest.attributes["Main-Class"] = "com.example.MyMainClass"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree) // OR .map { zipTree(it) }
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}