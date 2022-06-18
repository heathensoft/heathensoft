
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


tasks.create("fatJar", Jar::class) {
    //group = "my tasks" // OR, for example, "build"
    //description = "Creates a self-contained fat JAR of the application that can be run."
    //manifest.attributes["Main-Class"] = "com.example.MyMainClass"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    with(tasks.jar.get())
}

