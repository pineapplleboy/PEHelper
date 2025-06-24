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
import com.example.pehelper.ui.theme.PEHelperTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PEHelperTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomAppBar {
                            IconButton(onClick = { navController.navigate("auth") }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Auth")
                            }
                            IconButton(onClick = { navController.navigate("profile") }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "auth",
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        composable("auth") { AuthScreen() }
                        composable("profile") { ProfileScreen() }
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
        AuthScreen()
    }
}