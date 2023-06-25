package com.diegulog.intellifit.ui

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.diegulog.intellifit.R
import com.diegulog.intellifit.databinding.ActivityMainBinding
import com.diegulog.intellifit.ui.login.LoginViewModel
import com.google.android.material.color.DynamicColors
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            }
        )
        configUi()

    }

    private fun configUi() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val graph = navController.navInflater.inflate(R.navigation.main_nav_graph)
        // Comprueba si el usuario ha iniciado sesión y establece la startDestination correspondiente.
        if (viewModel.getSessionToken() != null) {
            graph.setStartDestination(R.id.HomeFragment)
        } else {
            graph.setStartDestination(R.id.LoginFragment)
        }
        navController.graph = graph
    }

    override fun onBackPressed() {
        if(!onSupportNavigateUp())
            super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}