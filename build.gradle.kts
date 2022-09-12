import com.mycila.maven.plugin.license.Callback
import com.mycila.maven.plugin.license.header.HeaderDefinition
import nl.javadude.gradle.plugins.license.maven.AbstractLicenseMojo
import nl.javadude.gradle.plugins.license.maven.CallbackWithFailure
import nl.javadude.gradle.plugins.license.maven.LicenseFormatMojo
import java.net.URI
import java.util.Collections

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.hierynomus.gradle.plugins:license-gradle-plugin:0.15.0")
    }
}

allprojects {
    group = "org.sugarcubes"
    version = "1.0.2"
    description = "Java Reflection Cloner library"

    repositories {
        mavenCentral()
    }
}

class LicenseMojo(
    validHeaders: MutableCollection<File>?,
    rootDir: File?,
    initial: MutableMap<String, String>?,
    dryRun: Boolean,
    skipExistingHeaders: Boolean,
    useDefaultMappings: Boolean,
    strictCheck: Boolean,
    header: URI?,
    source: FileCollection?,
    mapping: MutableMap<String, String>?,
    encoding: String?,
    headerDefinitions: MutableList<HeaderDefinition>?
) : AbstractLicenseMojo(
    validHeaders,
    rootDir,
    initial,
    dryRun,
    skipExistingHeaders,
    useDefaultMappings,
    strictCheck,
    header,
    source,
    mapping,
    encoding,
    headerDefinitions
) {

    public override fun execute(callback: Callback?) {
        super.execute(callback)
    }

}

tasks.create("licenseFormat") {
    doLast {
        val callback: CallbackWithFailure = LicenseFormatMojo(project.rootDir, false, false)
        val license = LicenseMojo(
            Collections.emptySet(),
            project.rootDir,
            Collections.emptyMap(),
            false,
            false,
            true,
            true,
            file("HEADER.txt").toURI(),
            fileTree(project.rootDir) {
                include("**/*.java")
            },
            Collections.singletonMap("java", "SLASHSTAR_STYLE"),
            "utf-8",
            Collections.emptyList()
        )
        license.execute(callback)
    }
}
