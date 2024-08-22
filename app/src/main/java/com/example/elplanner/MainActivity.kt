package com.example.elplanner

import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
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
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "splash") {
                            composable("splash") { SplashPage(navController)}
                            composable("Carousel") { Carousel(navController= navController) }
                            composable("Welcome"){ WelcomePage()}
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SplashPage(navController: NavController){

    val splashIcon= painterResource(id = R.drawable.splashicon)
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = splashIcon,
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(19.dp))
        Text(
            text = "El Planner",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = Color.White
            )
        )

    }

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("carousel") {
            popUpTo("splash") { inclusive = true }
        }
    }


}

data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val contentDescriptionResId: Int?,
    val title: String,
    val message: String,
)

val items =
    listOf(
        CarouselItem(0, R.drawable.dailyroutine, null, "Manage your tasks", "You can easily manage all of your daily activities"),
        CarouselItem(1, R.drawable.taskmanage, null, "Create a daily routine", "In El Planner you can create your personal routine to stay productive"),
        CarouselItem(2, R.drawable.organizetask, null,"Organize your tasks", "You can organize your daily tasks by adding your tasks into separate categories"),
    )

@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    preferredItemWidth: Dp = 310.dp,
    itemSpacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    navController: NavController
) {
    val pagerState = rememberPagerState()

    Column(
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        modifier = modifier.padding(top = 14.dp)
    ) {
        HorizontalPager(
            count = items.size,
            state = pagerState,
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 84.dp)

        ) { page ->
            val item = items[page]
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .height(250.dp)
                        .fillMaxWidth(),
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.contentDescriptionResId?.let { stringResource(it) },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(100.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) {index->
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(4.dp)
                                .background(
                                    color = if (pagerState.currentPage == index) Color.White else Color.Gray,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
                Text(
                    text = item.title,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(42.dp))
                Text(
                    text = item.message,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier.fillMaxWidth(0.85f),
                    textAlign = TextAlign.Center
                )

            }

        }
        Spacer(modifier = Modifier.height(107.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color(0xFF8875FF), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 25.dp)
                    .height(40.dp)
                    .clickable { navController.navigate("Welcome") },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NEXT",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun WelcomePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 67.dp) // Padding from the bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 67.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Space items evenly within the column
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to El Planner",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 95.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(26.dp))
                Text(
                    text = "Please login to your account or create new account to continue",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Column with NEXT buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 17.dp), // Positioned 67.dp from the bottom
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Space between buttons
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color(0xFF8875FF), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 25.dp)
                            .height(40.dp)
                            .clickable { },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LOGIN",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                            .border(
                                width = 2.dp,
                                color = Color(0xFF8875FF),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 25.dp)
                            .height(40.dp)
                            .clickable { },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "CREATE AN ACCOUNT",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}