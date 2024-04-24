package tech.insight.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * add logback and slf4j dependencies to project
 *
 * @author gongxuanzhangmelt@gmail.com
 */
open class LogbackImportPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val dependencies = project.dependencies
        dependencies.add("implementation", "org.slf4j:jul-to-slf4j:2.0.12")
        dependencies.add("implementation", "org.slf4j:slf4j-api:2.0.12")
        dependencies.add("implementation", "ch.qos.logback:logback-classic:1.4.14")
        println("add logback and slf4j to ${project.name}")
    }
}
