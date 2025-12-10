plugins {
    alias(libs.plugins.runique.android.library.compose)
}

android {
    namespace = "com.example.core.presentation.designsystem"
}

dependencies {
    implementation(projects.core.presentation.ui)
    implementation(projects.core.domain)
    implementation(libs.coil.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    api(libs.androidx.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}