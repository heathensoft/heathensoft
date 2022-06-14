
plugins {
    id("io.github.heathensoft.project-library")
}



/*
val someConfiguration: Configuration by configurations.creating {
    extendsFrom(configurations.api.get())
}

configurations {
    someConfiguration
}

 */

dependencies {
    //someConfiguration(project(":common"))
    api(project(":common"))
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

