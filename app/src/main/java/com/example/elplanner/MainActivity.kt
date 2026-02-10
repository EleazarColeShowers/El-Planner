package com.example.elplanner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.ViewModelProvider
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var taskViewModel: TaskViewModel

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        taskViewModel = ViewModelProvider.getTaskViewModel(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212) // Darker, richer background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val navigateTo = (LocalContext.current as Activity).intent.getStringExtra("navigate_to")
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = if (navigateTo == "Carousel") "Carousel" else "splash"
                        ) {
                            composable("splash") { SplashPage(navController, auth, taskViewModel) }
                            composable("Carousel") { Carousel(navController = navController) }
                            composable("Welcome") { WelcomePage(navController) }
                            composable("CreateAccount") { CreateAccountPage(auth) }
                            composable("Login") { LoginPage(auth, taskViewModel) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashPage(navController: NavController, auth: FirebaseAuth, taskViewModel: TaskViewModel) {
    val splashIcon = painterResource(id = R.drawable.splashicon)
    val context = LocalContext.current

    // Animations
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = splashIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "El Planner",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.alpha(alpha)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Organize your life",
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.alpha(alpha)
            )
        }
    }

    LaunchedEffect(Unit) {
        taskViewModel.syncRoomTasksToFirebase()
        delay(2000)
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email != null) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra("navigate_to", "TaskPage")
            }
            context.startActivity(intent)
        } else {
            navController.navigate("Carousel") {
                popUpTo("splash") { inclusive = true }
            }
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

val items = listOf(
    CarouselItem(
        0,
        R.drawable.dailyroutine,
        null,
        "Manage your tasks",
        "You can easily manage all of your daily activities"
    ),
    CarouselItem(
        1,
        R.drawable.taskmanage,
        null,
        "Create a daily routine",
        "In El Planner you can create your personal routine to stay productive"
    ),
    CarouselItem(
        2,
        R.drawable.organizetask,
        null,
        "Organize your tasks",
        "You can organize your daily tasks by adding your tasks into separate categories"
    ),
)

@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    navController: NavController
) {
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Carousel content
            Column(
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                HorizontalPager(
                    count = items.size,
                    state = pagerState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val item = items[page]

                    // Animate page transitions
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffset
                    val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(-1f, 1f))
                    val alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(-1f, 1f))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scale)
                            .alpha(alpha)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Image with card effect
                        Card(
                            modifier = Modifier
                                .height(320.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
                        ) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.contentDescriptionResId?.let { stringResource(it) },
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Page indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .width(if (isSelected) 32.dp else 8.dp)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isSelected) Color(0xFF8875FF) else Color.Gray.copy(alpha = 0.3f)
                                        )
                                        .animateContentSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = item.title,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = item.message,
                            style = TextStyle(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier.fillMaxWidth(0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Enhanced button with ripple effect
            AnimatedButton(
                text = "GET STARTED",
                onClick = { navController.navigate("Welcome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 48.dp)
            )
        }
    }
}

@Composable
fun WelcomePage(navController: NavController) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "El Planner",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Please login to your account or create new account to continue",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedButton(
                    text = "LOGIN",
                    onClick = { navController.navigate("Login") },
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedOutlinedButton(
                    text = "CREATE ACCOUNT",
                    onClick = { navController.navigate("CreateAccount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPage(auth: FirebaseAuth) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Create Account",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign up to get started",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            EnhancedTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            EnhancedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            EnhancedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedButton(
                text = "REGISTER",
                onClick = {
                    performSignUp(
                        auth,
                        context as ComponentActivity,
                        email,
                        password,
                        usernameTxt = username,
                        onSuccess = {
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.putExtra("username", username)
                            context.startActivity(intent)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(auth: FirebaseAuth, taskViewModel: TaskViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Welcome Back",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Login to continue",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            EnhancedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            EnhancedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedButton(
                text = "LOGIN",
                onClick = {
                    performLogin(
                        auth,
                        context as ComponentActivity,
                        email,
                        password,
                        onSuccess = { userId ->
                            taskViewModel.loadUserTasks(userId)
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Enhanced Reusable Components

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color(0xFF8875FF),
            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
            focusedBorderColor = Color(0xFF8875FF),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            cursorColor = Color(0xFF8875FF),
            focusedContainerColor = Color(0xFF1E1E2E),
            unfocusedContainerColor = Color(0xFF1E1E2E)
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 16.sp
        ),
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF8875FF),
                            Color(0xFFA890FF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun AnimatedOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = modifier
            .scale(scale)
            .height(56.dp)
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF8875FF),
                        Color(0xFFA890FF)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

// Helper function for lerp
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

// Keep original auth functions
fun performSignUp(
    auth: FirebaseAuth,
    context: ComponentActivity,
    email: String,
    password: String,
    usernameTxt: String,
    onSuccess: () -> Unit
) {
    if (email.isEmpty() || password.isEmpty() || usernameTxt.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(context) { task ->
            if (task.isSuccessful) {
                createUser(username = usernameTxt)
                val intent = Intent(context, HomeActivity::class.java)
                context.startActivity(intent)
                Toast.makeText(context, "Successfully signed up", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
}

fun createUser(username: String) {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val database: DatabaseReference = Firebase.database.reference

    database.child("users").child(currentUser?.uid.toString()).setValue(username)
        .addOnSuccessListener {
            Log.d("###", "data saved ")
        }
        .addOnFailureListener {
            Log.d("###", "data failed ${it.message}")
        }
}

fun performLogin(
    auth: FirebaseAuth,
    context: ComponentActivity,
    email: String,
    password: String,
    onSuccess: (String) -> Unit,
) {
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(context) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    onSuccess(userId)
                }
                Toast.makeText(context, "Successfully logged in", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
}