package com.example.elplanner

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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


    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        taskViewModel = ViewModelProvider.getTaskViewModel(this)

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
                        val navigateTo = (LocalContext.current as Activity).intent.getStringExtra("navigate_to")

                        val userId = FirebaseAuth.getInstance().currentUser?.uid

                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = if (navigateTo == "Carousel") "Carousel" else "splash") {
                            composable("splash") { SplashPage(navController, auth, taskViewModel)}
                            composable("Carousel") { Carousel(navController= navController) }
                            composable("Welcome"){ WelcomePage(navController)}
                            composable("CreateAccount"){ CreateAccountPage(auth)}
                            composable("Login"){ LoginPage(auth, taskViewModel) }
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
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
        taskViewModel.syncRoomTasksToFirebase()
        delay(1500)
        val currentUser = auth.currentUser
        if(currentUser != null && !currentUser.isAnonymous && currentUser.email != null){
            val intent = Intent(context, HomeActivity::class.java).apply {
                putExtra("navigate_to", "TaskPage")
            }
            context.startActivity(intent)
        } else {
            navController.navigate("carousel") {
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

val items =
    listOf(
        CarouselItem(0, R.drawable.dailyroutine, null, "Manage your tasks", "You can easily manage all of your daily activities"),
        CarouselItem(1, R.drawable.taskmanage, null, "Create a daily routine", "In El Planner you can create your personal routine to stay productive"),
        CarouselItem(2, R.drawable.organizetask, null,"Organize your tasks", "You can organize your daily tasks by adding your tasks into separate categories"),
    )

@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    navController: NavController
) {
    val pagerState = rememberPagerState()

    // Use a Column to stack carousel content and the button
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween  // Space out elements between top and bottom
    ) {
        // Carousel content at the top
        Column(
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 84.dp)
        ) {
            HorizontalPager(
                count = items.size,
                state = pagerState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val item = items[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(500.dp),  // Set a fixed height for carousel content
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
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(3) { index ->
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
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = item.title,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(36.dp))
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
        }

        // Button at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 150.dp),  // Optional bottom padding
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
fun WelcomePage(navController: NavController) {
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
                            .clickable {navController.navigate("Login") },
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
                            .clickable { navController.navigate("CreateAccount") },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountPage(auth: FirebaseAuth) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(53.dp))

        Text(
            text = "Register",
            style = TextStyle(
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(23.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color(0xFF8875FF)
            ),
            textStyle = TextStyle(color = Color.White)
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color(0xFF8875FF)
            ),
            textStyle = TextStyle(color = Color.White)
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color(0xFF8875FF)
            ),
            textStyle = TextStyle(color = Color.White),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(80.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFF8875FF), shape = RoundedCornerShape(20.dp))
                    .clickable {
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
                    }
                    .padding(vertical = 10.dp),  // Adjusted padding for consistency
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Register",
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(auth: FirebaseAuth, taskViewModel: TaskViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(53.dp))

        Text(
            text = "Login",
            style = TextStyle(
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(23.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color(0xFF8875FF)
            ),
            textStyle = TextStyle(color = Color.White)
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color(0xFF8875FF)
            ),
            textStyle = TextStyle(color = Color.White),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(80.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color(0xFF8875FF), shape = RoundedCornerShape(20.dp))
                    .clickable {
                        performLogin(
                            auth,
                            context as ComponentActivity,
                            email,
                            password,
                            onSuccess = {  userId ->
                                // Once logged in, load tasks for this user
                                taskViewModel.loadUserTasks(userId)
                                val intent = Intent(context, HomeActivity::class.java)
//                                intent.putExtra("username", username)
                                context.startActivity(intent)
                            }
                        )
                    }
                    .padding(vertical = 10.dp),  // Adjusted padding for better alignment
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
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


fun performSignUp(auth: FirebaseAuth, context: ComponentActivity, email: String, password: String, usernameTxt: String, onSuccess: () -> Unit) {
    if (email.isEmpty() || password.isEmpty() || usernameTxt.isEmpty()) {
        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context) { task ->
        if (task.isSuccessful) {
            createUser(username= usernameTxt)
                val uid = auth.currentUser?.uid
                createUser(username = usernameTxt)

            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)

            Toast.makeText(context, "Successfully sign up", Toast.LENGTH_SHORT).show()
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

    database.child("users").child(currentUser?.uid.toString()).setValue(username).addOnSuccessListener {
        Log.d("###","data saved ")
    }.addOnFailureListener {
        Log.d("###","data failed ${it.message}")
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
