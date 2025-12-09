plugins {
    alias(libs.plugins.runique.android.feature.ui)
}

android {
    namespace = "com.example.analytics.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}