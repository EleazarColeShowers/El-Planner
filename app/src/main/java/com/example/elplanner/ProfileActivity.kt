package com.example.elplanner

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.ViewModelProvider
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay

class ProfileActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var taskViewModel: TaskViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        taskViewModel = ViewModelProvider.getTaskViewModel(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
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
                        ProfilePage(auth, taskViewModel)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(auth: FirebaseAuth, taskViewModel: TaskViewModel) {
    val navController = rememberNavController()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Header()
                Spacer(modifier = Modifier.height(32.dp))
                TaskProgress(taskViewModel)
                Spacer(modifier = Modifier.height(40.dp))
                Settings()
                Spacer(modifier = Modifier.height(24.dp))
                Accounts()
                Spacer(modifier = Modifier.height(24.dp))
                ElAbout(auth)
            }
        }
        EnhancedBottomBar(navController)
    }
}

@Composable
fun Header() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    var username by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    if (currentUser != null) {
        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        LaunchedEffect(userId) {
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val snapshotValue = snapshot.value
                        if (snapshotValue is String) {
                            username = snapshotValue
                            with(sharedPreferences.edit()) {
                                putString("username", username)
                                apply()
                            }
                        } else {
                            username = "User"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to fetch username: ${error.message}")
                    username = "Error"
                }
            })
        }
    }

    if (username == null) {
        username = sharedPreferences.getString("username", "Loading...")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        // Profile title
        Text(
            text = "Profile",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Profile card
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF8875FF), Color(0xFFA890FF))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = username?.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User info
                    Column {
                        Text(
                            text = "Welcome back,",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = username ?: "Loading...",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.email ?: "",
                            color = Color(0xFF8875FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskProgress(taskViewModel: TaskViewModel) {
    val taskList by taskViewModel.taskList.collectAsState()
    val tasksLeft = taskList.count { !it.completed }
    val tasksDone = taskList.count { it.completed }
    val totalTasks = taskList.size
    val progress = if (totalTasks > 0) tasksDone.toFloat() / totalTasks else 0f

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically()
    ) {
        Column {
            Text(
                text = "Task Progress",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tasks Left Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E1E2E),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFFF9680),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$tasksLeft",
                            style = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tasks Left",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }
                }

                // Tasks Done Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E1E2E),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = Color(0xFF80FFA3),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$tasksDone",
                            style = TextStyle(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Completed",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Overall Progress",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color(0xFF8875FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFF2A2A3E), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF8875FF), Color(0xFFA890FF))
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun Settings() {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Settings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = painterResource(id = R.drawable.seetingsicon),
                title = "App Settings",
                onClick = {
                    val intent = Intent(context, AppSetting::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Accounts() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        visible = true
    }

    // Username Dialog
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Change Account Name",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        placeholder = { Text("Enter new account name", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF8875FF),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF8875FF),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedOutlinedButton(
                            text = "Cancel",
                            onClick = { showDialog = false },
                            modifier = Modifier.weight(1f)
                        )

                        EnhancedGradientButton(
                            text = "Update",
                            onClick = {
                                if (newUsername.isNotEmpty()) {
                                    updateUsername(
                                        newUsername,
                                        onSuccess = {
                                            Toast.makeText(context, "Username updated!", Toast.LENGTH_SHORT).show()
                                            showDialog = false
                                        },
                                        onFailure = {
                                            Toast.makeText(context, "Failed to update username", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Please enter a valid username", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Password Dialog
    if (showPasswordDialog) {
        Dialog(onDismissRequest = { showPasswordDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Change Password",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        placeholder = { Text("Current password", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF8875FF),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF8875FF),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text("New password", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color(0xFF8875FF),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF8875FF),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedOutlinedButton(
                            text = "Cancel",
                            onClick = { showPasswordDialog = false },
                            modifier = Modifier.weight(1f)
                        )

                        EnhancedGradientButton(
                            text = "Update",
                            onClick = {
                                changePassword(
                                    oldPassword = oldPassword,
                                    newPassword = newPassword,
                                    onSuccess = {
                                        Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                        showPasswordDialog = false
                                        oldPassword = ""
                                        newPassword = ""
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Failed to update password", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Account",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = painterResource(id = R.drawable.profileicon),
                title = "Change account name",
                onClick = { showDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(
                icon = painterResource(id = R.drawable.changepassword),
                title = "Change account password",
                onClick = { showPasswordDialog = true }
            )
        }
    }
}

@Composable
fun ElAbout(auth: FirebaseAuth) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "El Planner",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem(
                icon = painterResource(id = R.drawable.about),
                title = "About Us",
                onClick = {
                    val intent = Intent(context, AboutUs::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(
                icon = painterResource(id = R.drawable.faq),
                title = "FAQ",
                onClick = {
                    val intent = Intent(context, FAQActivity::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Logout button
            Surface(
                onClick = {
                    auth.signOut()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("navigate_to", "Carousel")
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = null,
                        tint = Color(0xFFFF4949),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Log out",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF4949),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFFF4949),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.painter.Painter,
    title: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1E2E),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color(0xFF8875FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }
}

// Reusable Enhanced Components

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = {
            if (enabled) {
                pressed = true
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8875FF),
                                Color(0xFFA890FF)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.5f),
                                Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            )
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }
}

@Composable
fun OutlinedButton(
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

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }
}



// Firebase functions remain unchanged
fun updateUsername(newUsername: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    if (currentUser != null) {
        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")

        databaseRef.setValue(newUsername)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    } else {
        onFailure(Exception("User not authenticated"))
    }
}

fun changePassword(
    oldPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val credential = EmailAuthProvider.getCredential(user?.email ?: "", oldPassword)

    user?.reauthenticate(credential)?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        } else {
            onFailure()
        }
    }
}