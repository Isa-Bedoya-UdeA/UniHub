plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    application
}

application {
    mainClass.set("com.unihub.server.ApplicationKt")
}

tasks.register("runDirect") {
    dependsOn("run")
}

tasks.withType<JavaExec> {
    jvmArgs("-Djava.net.preferIPv4Stack=true")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.unihub.server.ApplicationKt"
    }
    // Incluir todas las dependencias en el JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(libs.ktorServerCore)
    implementation(libs.ktorServerNetty)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorSerializationKotlinxJson)
    implementation(libs.ktorServerCors)
    implementation(libs.ktorServerStatusPages)
    implementation(libs.ktorServerAuth)
    implementation(libs.firebase.admin)
    implementation(libs.ktorClientCore)
    implementation(libs.ktorClientOkhttp)
    implementation(libs.ktorClientContentNegotiation)
    implementation(libs.ktorClientLogging)
    implementation(libs.kotlinxSerializationJson)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.ktorServerTestHost)
}
