package com.example.pehelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pehelper.presentation.screen.AuthScreen
import com.example.pehelper.presentation.screen.ProfileScreen
import com.example.pehelper.presentation.screen.SplashScreen
import com.example.pehelper.ui.theme.PEHelperTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PEHelperTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        composable("splash") { SplashScreen(navController = navController) }
                        composable("auth") { AuthScreen(navController = navController) }
                        composable("profile") { ProfileScreen(navController = navController) }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PEHelperTheme {
        SplashScreen(rememberNavController())
    }
}