val composeVersion = rootProject.extra.get("compose_version") as String
val ktorVersion = rootProject.extra.get("ktor_version") as String
val roomVersion = rootProject.extra.get("room_version") as String

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.devtools.ksp")
	kotlin("plugin.serialization") version "1.6.21"
}

repositories {
	mavenCentral()
	maven("https://jitpack.io")
	google()
	maven {
		url = uri("https://maven.pkg.github.com/revanced/revanced-patcher")
		credentials {
			username =
				(project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")) as String
			password =
				(project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")) as String
		}
	}
}

val appId = "app.revanced.manager"

android {
	namespace = appId
	compileSdk = 32

	testOptions {
		unitTests.isReturnDefaultValues = true
	}

	defaultConfig {
		applicationId = appId
		minSdk = 26
		targetSdk = 32 // TODO: update to 33 when sources are available
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}
	applicationVariants.all {
		kotlin.sourceSets {
			getByName(name) {
				kotlin.srcDir("build/generated/ksp/$name/kotlin")
			}
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = JavaVersion.VERSION_11.toString()
		optIn("kotlin.RequiresOptIn")
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = composeVersion
	}
	packagingOptions {
		jniLibs {
			useLegacyPackaging = true
		}
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildToolsVersion = "33.0.0"
}

dependencies {
	// Core
	implementation("androidx.core:core-ktx:1.8.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
	implementation("androidx.activity:activity-compose:1.4.0")
	implementation("androidx.compose.ui:ui:$composeVersion")
	implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
	implementation("androidx.navigation:navigation-compose:2.5.0-rc02")

	// ReVanced
	implementation("app.revanced:revanced-patcher:2.0.4")

	// Signing & aligning
	implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
	implementation("com.android.tools.build:apksig:7.2.1")

	// Compose Destinations
	implementation("io.github.raamcosta.compose-destinations:core:1.5.5-beta")
	ksp("io.github.raamcosta.compose-destinations:ksp:1.5.5-beta")

	// Accompanist
	implementation("com.google.accompanist:accompanist-drawablepainter:0.24.8-beta")
	implementation("com.google.accompanist:accompanist-systemuicontroller:0.24.10-beta")

	// libsu
	implementation("com.github.topjohnwu.libsu:core:4.0.3")
	implementation("com.github.topjohnwu.libsu:io:4.0.3")

	// Room
	implementation("androidx.room:room-runtime:$roomVersion")
	annotationProcessor("androidx.room:room-compiler:$roomVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	ksp("androidx.room:room-compiler:$roomVersion")

	// HTTP client
	implementation("io.ktor:ktor-client-core:$ktorVersion")
	implementation("io.ktor:ktor-client-android:$ktorVersion")
	implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

	// Material 3 + 2
	implementation("androidx.compose.material3:material3-window-size-class:1.0.0-alpha13")
	implementation("androidx.compose.material3:material3:1.0.0-alpha13")
	implementation("androidx.compose.material:material:1.1.1")

	// Tests
	testImplementation(kotlin("test"))
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.3")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
	androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
	implementation(kotlin("script-runtime"))
}

fun org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions.optIn(library: String) {
	freeCompilerArgs = freeCompilerArgs + "-opt-in=$library"
}