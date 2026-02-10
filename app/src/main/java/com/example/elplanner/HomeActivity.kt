package com.example.elplanner

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.data.TaskItem
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.ViewModelProvider
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import kotlin.jvm.java

class HomeActivity : ComponentActivity() {
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
                    EnhancedIndex(auth, taskViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnhancedIndex(auth: FirebaseAuth, taskViewModel: TaskViewModel) {
    val navController = rememberNavController()
    val taskList by taskViewModel.taskList.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val activity = LocalActivity.current
                val navigateTo = activity?.intent?.getStringExtra("navigate_to")

                EnhancedHomePage(auth)
                EnhancedSearchBar(
                    hint = "Search tasks...",
                    onTextChange = { query ->
                        searchQuery.value = query
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))

                NavHost(
                    navController = navController,
                    startDestination = if (taskList.isEmpty()) "EmptyPage" else "TaskPage"
                ) {
                    composable("EmptyPage") { EnhancedEmptyPage() }
                    composable("AddTask") { EnhancedAddTask(navController, taskViewModel) }
                    composable("DateTime") { EnhancedDateTime(navController, taskViewModel) }
                    composable("TimeView") { EnhancedTimeView(navController, taskViewModel) }
                    composable("PriorityFlag") { EnhancedPriorityFlag(navController, taskViewModel) }
                    composable("TaskPage") { EnhancedTaskPage(navController, taskViewModel, searchQuery.value) }
                }

                LaunchedEffect(navigateTo) {
                    if (navigateTo == "TaskPage") {
                        navController.navigate("TaskPage")
                    }
                }
            }
            EnhancedBottomBar(navController)
        }
    }
}

@Composable
fun EnhancedHomePage(auth: FirebaseAuth) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = "My Tasks",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF8875FF), Color(0xFFA890FF))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = auth.currentUser?.email?.first()?.uppercaseChar()?.toString() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedSearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onTextChange: (String) -> Unit,
    textState: MutableState<String> = remember { mutableStateOf("") }
) {
    val text = textState.value
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1E2E)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused) Color(0xFF8875FF) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = text,
                onValueChange = {
                    textState.value = it
                    onTextChange(it)
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = hint,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                )
            )

            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    textState.value = ""
                    onTextChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedEmptyPage() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.checklisthome),
                    contentDescription = null,
                    modifier = Modifier.size(240.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "What do you want to do today?",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tap + to add your tasks",
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EnhancedBottomBar(navController: NavController) {
    val context = LocalContext.current
    val items = listOf("Index", "Profile")
    val icons = mapOf(
        "Index" to R.drawable.indexicon,
        "Profile" to R.drawable.profileicon
    )
    val selectedIndex = remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            color = Color(0xFF1E1E2E),
            shadowElevation = 16.dp
        ) {
            BottomNavigation(
                backgroundColor = Color(0xFF1E1E2E),
                contentColor = Color.White,
                modifier = Modifier.height(72.dp),
                elevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = icons[item] ?: R.drawable.menu),
                                contentDescription = item,
                                modifier = Modifier.size(24.dp),
                                tint = if (selectedIndex.intValue == index) Color(0xFF8875FF) else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                item,
                                color = if (selectedIndex.intValue == index) Color(0xFF8875FF) else Color.Gray,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedIndex.intValue == index) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        },
                        selected = selectedIndex.intValue == index,
                        onClick = {
                            selectedIndex.intValue = index
                            when (item) {
                                "Profile" -> {
                                    val intent = Intent(context, ProfileActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "Index" -> {
                                    val intent = Intent(context, HomeActivity::class.java)
                                    context.startActivity(intent)
                                }
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("AddTask") },
                modifier = Modifier.size(64.dp),
                containerColor = Color(0xFF8875FF),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.addicon),
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAddTask(navController: NavController, taskViewModel: TaskViewModel) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
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
                        text = "Add Task",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = taskViewModel.task,
                        onValueChange = { taskViewModel.task = it },
                        label = { Text("Task Title") },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = taskViewModel.description,
                        onValueChange = { taskViewModel.description = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
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
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EnhancedGradientButton(
                        text = "Choose Date & Time",
                        onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("task", taskViewModel.task)
                            navController.currentBackStackEntry?.savedStateHandle?.set("description", taskViewModel.description)
                            navController.navigate("DateTime")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnhancedDateTime(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
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
                        text = "Select Date",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EnhancedCalendarView(onDateSelected = { date ->
                        selectedDate = date
                    })

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedOutlinedButton(
                            text = "Cancel",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        )

                        EnhancedGradientButton(
                            text = "Next",
                            onClick = {
                                selectedDate?.let { date ->
                                    navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("description", description)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedDate", date.toString())
                                    navController.navigate("TimeView")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedDate != null
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnhancedCalendarView(onDateSelected: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Month/Year selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = Color(0xFF8875FF)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${currentMonth.year}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Month",
                    tint = Color(0xFF8875FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Day labels
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
        val daysInMonth = currentMonth.lengthOfMonth()
        var day = 1

        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in DayOfWeek.entries) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (week == 0 && dayOfWeek < firstDayOfMonth || day > daysInMonth) {
                            Spacer(modifier = Modifier.fillMaxSize())
                        } else {
                            val date = currentMonth.atDay(day)
                            val isSelected = selectedDate == date
                            val isToday = today == date

                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        selectedDate = date
                                        onDateSelected(date)
                                    },
                                shape = CircleShape,
                                color = when {
                                    isSelected -> Color(0xFF8875FF)
                                    isToday -> Color(0xFF8875FF).copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = when {
                                            isSelected -> Color.White
                                            isToday -> Color(0xFF8875FF)
                                            else -> Color.White
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            day++
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnhancedTimeView(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose Time",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EnhancedDigitalTime(onTimeSelected = { hour, minute ->
                        selectedTime = Pair(hour, minute)
                    })

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedOutlinedButton(
                            text = "Cancel",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        )

                        EnhancedGradientButton(
                            text = "Next",
                            onClick = {
                                selectedTime?.let { time ->
                                    val timeString = String.format("%02d:%02d", time.first, time.second)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("description", description)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedTime", timeString)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedDate", selectedDate)
                                    navController.navigate("PriorityFlag")
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedDigitalTime(onTimeSelected: (Int, Int) -> Unit) {
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    TimeInput(
        state = timePickerState,
        colors = TimePickerDefaults.colors(
            clockDialColor = Color(0xFF2A2A3E),
            selectorColor = Color(0xFF8875FF),
            containerColor = Color.Transparent,
            periodSelectorBorderColor = Color(0xFF8875FF),
            clockDialSelectedContentColor = Color.White,
            clockDialUnselectedContentColor = Color.White.copy(alpha = 0.6f),
            periodSelectorSelectedContainerColor = Color(0xFF8875FF),
            periodSelectorUnselectedContainerColor = Color.Transparent,
            periodSelectorSelectedContentColor = Color.White,
            periodSelectorUnselectedContentColor = Color.White.copy(alpha = 0.6f),
            timeSelectorSelectedContainerColor = Color(0xFF8875FF),
            timeSelectorUnselectedContainerColor = Color(0xFF2A2A3E),
            timeSelectorSelectedContentColor = Color.White,
            timeSelectorUnselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
    )

    onTimeSelected(timePickerState.hour, timePickerState.minute)
}

@Composable
fun EnhancedPriorityFlag(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    val selectedTime = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedTime")
    var selectedPriority by remember { mutableStateOf(-1) }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Task Priority",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Priority grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (row in 0..2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                for (col in 0..3) {
                                    val index = row * 4 + col
                                    if (index < 10) {
                                        EnhancedPriorityBox(
                                            number = index + 1,
                                            isSelected = selectedPriority == index,
                                            onClick = { selectedPriority = index },
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else if (row == 2 && col > 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedOutlinedButton(
                            text = "Cancel",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f)
                        )

                        EnhancedGradientButton(
                            text = "Save",
                            onClick = {
                                if (selectedPriority != -1) {
                                    taskViewModel.selectedPriority = selectedPriority + 1
                                    navController.currentBackStackEntry?.savedStateHandle?.set("task", task)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("description", description)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedDate", selectedDate)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedTime", selectedTime)
                                    navController.currentBackStackEntry?.savedStateHandle?.set("priorityFlag", selectedPriority + 1)
                                    navController.navigate("TaskPage")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedPriority != -1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedPriorityBox(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        onClick = {
            pressed = true
            onClick()
        },
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF8875FF) else Color(0xFF2A2A3E),
        shadowElevation = if (isSelected) 8.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.priorityflag),
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = number.toString(),
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
fun EnhancedTaskPage(navController: NavController, taskViewModel: TaskViewModel, searchQuery: String) {
    val taskList by taskViewModel.taskList.collectAsState()
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    val selectedTime = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedTime")
    val priorityFlag = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("priorityFlag")
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            taskViewModel.loadUserTasks(userId)
        } else {
            navController.navigate("EmptyPage")
        }
    }

    LaunchedEffect(task) {
        task?.let {
            if (taskList.none { it.task == task }) {
                if (description != null && userId != null) {
                    taskViewModel.addTask(it, description, selectedDate ?: "", selectedTime ?: "", priorityFlag, category = "", userId = userId)
                }
            }
        }
    }

    val filteredTasks = if (searchQuery.isNotBlank()) {
        taskList.filter {
            it.task.contains(searchQuery, ignoreCase = true) ||
                    it.date.contains(searchQuery, ignoreCase = true)
        }
    } else {
        taskList
    }

    if (taskList.isEmpty()) {
        EnhancedEmptyPage()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(filteredTasks, key = { it.id }) { taskItem ->
                EnhancedTaskRow(
                    taskItem = taskItem,
                    taskViewModel = taskViewModel,
                    navController = navController
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnhancedTaskRow(
    taskItem: TaskItem,
    taskViewModel: TaskViewModel,
    navController: NavController
) {
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    val dismissState = rememberDismissState { dismissValue ->
        when (dismissValue) {
            DismissValue.DismissedToStart -> {
                taskViewModel.deleteTask(taskItem)
                true
            }
            DismissValue.DismissedToEnd -> {
                showCategoryDialog = true
                false
            }
            else -> false
        }
    }

    if (showCategoryDialog) {
        EnhancedCategoryDialog(
            onDismiss = { showCategoryDialog = false },
            onCategorySelected = { category ->
                taskViewModel.updateTask(taskItem.copy(category = category))
                showCategoryDialog = false
            }
        )
    }

    if (showDescriptionDialog) {
        EnhancedDescriptionDialog(
            taskItem = taskItem,
            onDismiss = { showDescriptionDialog = false }
        )
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val color = when (dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Color(0xFF8875FF).copy(alpha = 0.3f)
                DismissDirection.EndToStart -> Color.Red.copy(alpha = 0.3f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = when (dismissState.dismissDirection) {
                    DismissDirection.StartToEnd -> Alignment.CenterStart
                    DismissDirection.EndToStart -> Alignment.CenterEnd
                    else -> Alignment.CenterStart
                }
            ) {
                when (dismissState.dismissDirection) {
                    DismissDirection.StartToEnd -> Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    DismissDirection.EndToStart -> Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    else -> Unit
                }
            }
        },
        dismissContent = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1E2E),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDescriptionDialog = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Completion checkbox
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                taskViewModel.updateTask(taskItem.copy(completed = !taskItem.completed))
                            },
                        shape = CircleShape,
                        color = if (taskItem.completed) Color(0xFF8875FF) else Color.Transparent,
                        border = if (!taskItem.completed) {
                            androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.3f))
                        } else null
                    ) {
                        if (taskItem.completed) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = taskItem.task,
                            color = if (taskItem.completed) Color.White.copy(alpha = 0.5f) else Color.White,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${taskItem.date} • ${taskItem.time}",
                                    color = Color.White.copy(alpha = 0.5f),
                                    style = TextStyle(
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Category badge
                                if (!taskItem.category.isNullOrEmpty()) {
                                    val (categoryIcon, categoryColor) = getCategoryIconAndColor(taskItem.category)

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = categoryColor
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = categoryIcon,
                                                contentDescription = taskItem.category,
                                                modifier = Modifier.size(14.dp),
                                                tint = Color.Unspecified
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = taskItem.category ?: "",
                                                color = Color.Black,
                                                style = TextStyle(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }

                                // Priority badge
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2A2A3E),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8875FF))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.priorityflag),
                                            contentDescription = null,
                                            tint = Color(0xFF8875FF),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${taskItem.priorityFlag}",
                                            color = Color.White,
                                            style = TextStyle(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun getCategoryIconAndColor(category: String?): Pair<Painter, Color> {
    return when (category) {
        "Grocery" -> Pair(painterResource(id = R.drawable.grocery), Color(0xFFCCFF80))
        "Work" -> Pair(painterResource(id = R.drawable.work), Color(0xFFFF9680))
        "Sport" -> Pair(painterResource(id = R.drawable.sport), Color(0xFF80FFFF))
        "Design" -> Pair(painterResource(id = R.drawable.design), Color(0xFF80FFD9))
        "University" -> Pair(painterResource(id = R.drawable.university), Color(0xFF809CFF))
        "Social" -> Pair(painterResource(id = R.drawable.social), Color(0xFFFF80EB))
        "Music" -> Pair(painterResource(id = R.drawable.music), Color(0xFFFC80FF))
        "Health" -> Pair(painterResource(id = R.drawable.health), Color(0xFF80FFA3))
        "Movie" -> Pair(painterResource(id = R.drawable.movie), Color(0xFF80D1FF))
        "Home" -> Pair(painterResource(id = R.drawable.homeicon), Color(0xFFFFCC80))
        else -> Pair(painterResource(id = R.drawable.profileicon), Color.Gray)
    }
}

@Composable
fun EnhancedDescriptionDialog(taskItem: TaskItem, onDismiss: () -> Unit) {
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
                Text(
                    text = "Task Details",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = taskItem.task,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8875FF)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = taskItem.description ?: "No description available",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                EnhancedGradientButton(
                    text = "Close",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EnhancedCategoryDialog(onDismiss: () -> Unit, onCategorySelected: (String) -> Unit) {
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
                Text(
                    text = "Choose Category",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                val categories = listOf(
                    listOf(
                        Triple("Grocery", R.drawable.grocery, Color(0xFFCCFF80)),
                        Triple("Work", R.drawable.work, Color(0xFFFF9680)),
                        Triple("Sport", R.drawable.sport, Color(0xFF80FFFF))
                    ),
                    listOf(
                        Triple("Design", R.drawable.design, Color(0xFF80FFD9)),
                        Triple("University", R.drawable.university, Color(0xFF809CFF)),
                        Triple("Social", R.drawable.social, Color(0xFFFF80EB))
                    ),
                    listOf(
                        Triple("Music", R.drawable.music, Color(0xFFFC80FF)),
                        Triple("Health", R.drawable.health, Color(0xFF80FFA3)),
                        Triple("Movie", R.drawable.movie, Color(0xFF80D1FF))
                    ),
                    listOf(
                        Triple("Home", R.drawable.homeicon, Color(0xFFFFCC80))
                    )
                )

                categories.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { (name, icon, color) ->
                            EnhancedCategoryItem(
                                name = name,
                                icon = painterResource(id = icon),
                                color = color,
                                onClick = { onCategorySelected(name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                EnhancedOutlinedButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EnhancedCategoryItem(
    name: String,
    icon: Painter,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = {
                pressed = true
                onClick()
            },
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = color
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = icon,
                    contentDescription = name,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            color = Color.White,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
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
fun EnhancedGradientButton(
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
fun EnhancedOutlinedButton(
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