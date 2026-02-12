package com.example.elplanner

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.ViewModelProvider
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class AppSetting : ComponentActivity() {
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
                        SettingsContent(taskViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent(taskViewModel: TaskViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }

    // Load preferences
    val sharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    var notificationsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true))
    }
    var taskRemindersEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("task_reminders_enabled", true))
    }
    var soundEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("sound_enabled", true))
    }
    var vibrationEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("vibration_enabled", true))
    }
    var autoSyncEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("auto_sync_enabled", true))
    }
    var darkModeEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("dark_mode_enabled", true))
    }
    var showCompletedTasks by remember {
        mutableStateOf(sharedPreferences.getBoolean("show_completed_tasks", true))
    }

    var showClearDataDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    // Save preference helper
    fun savePreference(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
        ) {
            SettingsHeader(onBackClick = {
                (context as? ComponentActivity)?.finish()
            })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Notifications Section
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                SettingsSection(title = "Notifications") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        description = "Receive notifications about tasks",
                        checked = notificationsEnabled,
                        onCheckedChange = {
                            notificationsEnabled = it
                            savePreference("notifications_enabled", it)
                            Toast.makeText(context, "Notifications ${if (it) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Task Reminders",
                        description = "Get reminders for upcoming tasks",
                        checked = taskRemindersEnabled,
                        onCheckedChange = {
                            taskRemindersEnabled = it
                            savePreference("task_reminders_enabled", it)
                        },
                        enabled = notificationsEnabled
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchItem(
                        icon = Icons.Default.Call,
                        title = "Sound",
                        description = "Play sound for notifications",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            savePreference("sound_enabled", it)
                        },
                        enabled = notificationsEnabled
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchItem(
                        icon = Icons.Default.Star,
                        title = "Vibration",
                        description = "Vibrate for notifications",
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            savePreference("vibration_enabled", it)
                        },
                        enabled = notificationsEnabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Task Preferences Section
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                SettingsSection(title = "Task Preferences") {
                    SettingsSwitchItem(
                        icon = Icons.Default.CheckCircle,
                        title = "Show Completed Tasks",
                        description = "Display completed tasks in the list",
                        checked = showCompletedTasks,
                        onCheckedChange = {
                            showCompletedTasks = it
                            savePreference("show_completed_tasks", it)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsSwitchItem(
                        icon = Icons.Default.Settings,
                        title = "Auto-Sync",
                        description = "Automatically sync tasks with cloud",
                        checked = autoSyncEnabled,
                        onCheckedChange = {
                            autoSyncEnabled = it
                            savePreference("auto_sync_enabled", it)
                            if (it) {
                                taskViewModel.syncRoomTasksToFirebase()
                                Toast.makeText(context, "Syncing tasks...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsActionItem(
                        icon = Icons.Default.Refresh,
                        title = "Sync Now",
                        description = "Manually sync all your tasks",
                        onClick = {
                            taskViewModel.syncRoomTasksToFirebase()
                            Toast.makeText(context, "Syncing tasks...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Data & Storage Section
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
            ) {
                SettingsSection(title = "Data & Storage") {
                    SettingsActionItem(
                        icon = Icons.Default.Delete,
                        title = "Clear Local Cache",
                        description = "Free up storage space",
                        onClick = {
                            context.cacheDir.deleteRecursively()
                            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SettingsActionItem(
                        icon = Icons.Default.Warning,
                        title = "Clear All Data",
                        description = "Delete all tasks and settings",
                        onClick = {
                            showClearDataDialog = true
                        },
                        isDangerous = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Info
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "El Planner",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "Version 1.0.0",
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        ConfirmationDialog(
            title = "Clear All Data?",
            message = "This will permanently delete all your tasks and settings. This action cannot be undone.",
            confirmText = "Delete All",
            onConfirm = {
//                taskViewModel.clearAllTasks()
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, "All data cleared", Toast.LENGTH_LONG).show()
                showClearDataDialog = false

                // Navigate back to home
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            onDismiss = {
                showClearDataDialog = false
            }
        )
    }
}

@Composable
fun SettingsHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Settings",
            style = TextStyle(
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = TextStyle(
                color = Color(0xFF8875FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E2E),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color(0xFF8875FF) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = description,
                style = TextStyle(
                    color = if (enabled) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        CustomSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}

@Composable
fun SettingsActionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDangerous: Boolean = false
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                pressed = true
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDangerous) Color(0xFFFF4949) else Color(0xFF8875FF),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyle(
                    color = if (isDangerous) Color(0xFFFF4949) else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = description,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(100)
            pressed = false
        }
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .background(
                if (checked && enabled) Color(0xFF8875FF) else Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            )
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .offset(x = (20.dp * thumbPosition))
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF4949),
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EnhancedOutlinedButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    DangerButton(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DangerButton(
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
        color = Color(0xFFFF4949)
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
//
//@Composable
//fun EnhancedOutlinedButton(
//    text: String,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var pressed by remember { mutableStateOf(false) }
//    val scale by animateFloatAsState(
//        targetValue = if (pressed) 0.95f else 1f,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
//    )
//
//    Surface(
//        onClick = {
//            pressed = true
//            onClick()
//        },
//        modifier = modifier
//            .scale(scale)
//            .height(56.dp)
//            .border(
//                width = 2.dp,
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        Color(0xFF8875FF),
//                        Color(0xFFA890FF)
//                    )
//                ),
//                shape = RoundedCornerShape(16.dp)
//            ),
//        shape = RoundedCornerShape(16.dp),
//        color = Color.Transparent
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = text,
//                style = TextStyle(
//                    color = Color.White,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    letterSpacing = 1.sp
//                )
//            )
//        }
//    }
//
//    LaunchedEffect(pressed) {
//        if (pressed) {
//            delay(100)
//            pressed = false
//        }
//    }
//}