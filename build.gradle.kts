import com.soywiz.korge.gradle.*

buildscript {
	val korgePluginVersion: String by project

	repositories {
		mavenLocal()
		mavenCentral()
		google()
		maven { url = uri("https://plugins.gradle.org/m2/") }
	}
	dependencies {
		classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
	}
}

plugins {
		kotlin("multiplatform") version "1.4.30"
		kotlin("plugin.serialization") version "1.4.30"
}

apply<KorgeGradlePlugin>()

korge {
	id = "org.liamjd.herschel"

	dependencyMulti("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

	targetJvm()
	targetJs()
	targetDesktop()
//	targetIos()
//	targetAndroidIndirect() // targetAndroidDirect()
}
