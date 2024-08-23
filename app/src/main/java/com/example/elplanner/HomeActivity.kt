package com.example.elplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.ui.theme.ElPlannerTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "This is the home activity",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = Color.White
                            )
                        )

//                        val navController = rememberNavController()
//                        NavHost(navController = navController, startDestination = "splash") {
//                            composable("splash") { SplashPage(navController)}
//                            composable("Carousel") { Carousel(navController= navController) }
//                            composable("Welcome"){ WelcomePage(navController)}
//                            composable("CreateAccount"){ CreateAccountPage()}
//                        }
                    }
                }
            }
        }
    }
}
