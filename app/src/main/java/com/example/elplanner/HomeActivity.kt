package com.example.elplanner

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.data.TaskDatabase
import com.example.elplanner.data.TaskItem
import com.example.elplanner.data.TaskRepository
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.TaskViewModelFactory
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.firebase.auth.FirebaseAuth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        val repository = TaskRepository(taskDao)
        setContent {
            ElPlannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val taskViewModel: TaskViewModel by viewModels {
                        TaskViewModelFactory(application, repository)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Index(auth, taskViewModel)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Index(auth: FirebaseAuth, taskViewModel: TaskViewModel) {
    val navController = rememberNavController()
    val application = HomeActivity().application // Or `this.application` in an Activity
    val taskList by taskViewModel.taskList.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            HomePage(auth)
            SearchBar(
                hint = "Search books...",
                onTextChange = { query ->
                    searchQuery.value = query
                },
                onSearchClicked = {
                    println("Search for: ${searchQuery.value}")
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            NavHost(navController = navController, startDestination = if (taskList.isEmpty()) "EmptyPage" else "TaskPage") {
                composable("EmptyPage") { EmptyPage() }
                composable("AddTask"){ AddTask(navController, taskViewModel)}
                composable("DateTime"){ DateTime(navController, taskViewModel)}
                composable("TimeView"){ TimeView(navController, taskViewModel)}
                composable("PriorityFlag"){ PriorityFlag(navController, taskViewModel)}
                composable("TaskPage"){ TaskPage(navController, taskViewModel, searchQuery.value)}
            }

        }
        BottomBar(navController)
    }
}

@Composable
fun HomePage(auth: FirebaseAuth) {
    val menu = painterResource(id = R.drawable.menu)
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = menu,
                contentDescription = null,
                modifier = Modifier
                    .size(42.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = "Index",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text ="LOG OUT",
                style = TextStyle(
                    color = Color(0xFF8875FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        auth.signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("navigate_to", "Carousel")
                        context.startActivity(intent)
                    }
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    textState: MutableState<String> = remember { mutableStateOf("") }
) {
    val text = textState.value

    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(color = Color.Black, shape = RoundedCornerShape(6.dp))
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = {
                textState.value = it
                onTextChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp, start = 8.dp),
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Gray,
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
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked()
                }
            )
        )

        IconButton(onClick = { onSearchClicked() }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun EmptyPage(){
    val checkList= painterResource(id = R.drawable.checklisthome)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 75.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = checkList,
            contentDescription = null,
            modifier = Modifier.size(227.dp)
        )
        Spacer(modifier = Modifier.height(39.dp))
        Text(
            text = "What do you want to do today?",
            style = TextStyle(
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light
            ),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Tap + to add your tasks",
            style = TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            ),
            textAlign = TextAlign.Center,
        )
    }

}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf("Index", "Calendar", "Focus", "Profile")
    val icons = mapOf(
        "Index" to R.drawable.indexicon,
        "Calendar" to R.drawable.calendaricon,
        "Focus" to R.drawable.focusicon,
        "Profile" to R.drawable.profileicon
    )

    val selectedIndex = remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier.fillMaxWidth(),

    ) {
        BottomNavigation(
            backgroundColor = Color(0xFF363636),
            contentColor = Color.White,
            modifier = Modifier
                .height(114.dp)
                .padding(top = 12.dp)
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = icons[item] ?: R.drawable.menu),
                            contentDescription = item,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            item,
                            color = Color.White,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                        )
                    },
                    selected = selectedIndex.intValue == index,
                    onClick = { selectedIndex.intValue = index }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-36).dp)
                .size(64.dp)
                .background(Color(0xFF8875FF), shape = CircleShape)
                .clickable {
                    navController.navigate("AddTask")
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.addicon),
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTask(navController: NavController, taskViewModel: TaskViewModel) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color(0xFF363636))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add Task",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = taskViewModel.task,
                onValueChange = { taskViewModel.task = it },
                label = { Text("Task") },
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedBorderColor = Color(0xFF8875FF),
                    unfocusedBorderColor = Color.White
                ),
                textStyle = TextStyle(
                    color = Color.White,
                ),

            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = taskViewModel.description,
                onValueChange = { taskViewModel.description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedBorderColor = Color(0xFF8875FF),
                    unfocusedBorderColor = Color.White
                ),
                textStyle = TextStyle(
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier= Modifier.fillMaxWidth()){
                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color(0xFF8875FF), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "task", taskViewModel.task
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "description", taskViewModel.description
                            )
                            navController.navigate("DateTime")
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Choose Date",
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTime(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(450.dp)
                .background(Color(0xFF363636))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            CalendarView(onDateSelected = { date ->
                selectedDate = date
            })

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            color = Color(0xFF8875FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color(0xFF8875FF), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            selectedDate?.let { date ->
                                // Save task, description, and date to the savedStateHandle
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "task",
                                    task
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "description",
                                    description
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "selectedDate",
                                    date.toString()
                                )

                                // Navigate to TimeView
                                navController.navigate("TimeView")
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Choose Time",
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(onDateSelected: (LocalDate) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Month",
                    tint = Color.White
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${currentMonth.year}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Month",
                    tint = Color.White
                )
            }
        }
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
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (week == 0 && dayOfWeek < firstDayOfMonth || day > daysInMonth) {
                            Spacer(modifier = Modifier.fillMaxSize())
                        } else {
                            val date = currentMonth.atDay(day)
                            Box(
                                modifier = Modifier
                                    .background(
                                        when {
                                            today == date -> Color(0xFF8875FF)
                                            selectedDate == date -> Color(0xFF8875FF)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .padding(3.dp)
                                    .clickable {
                                        selectedDate = date
                                        onDateSelected(date)
                                    }
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
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
fun TimeView(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(250.dp)
                .background(Color(0xFF363636))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Time",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                DigitalTime(onTimeSelected = { hour, minute ->
                    selectedTime = Pair(hour, minute)
                })
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            color = Color(0xFF8875FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color(0xFF8875FF), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            selectedTime?.let { time ->
                                val timeString = "${time.first}:${time.second}"

                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "task",
                                    task
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "description",
                                    description
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "selectedTime",
                                    timeString
                                )
                                selectedDate?.let { date ->
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "selectedDate",
                                        date
                                    )
                                }
                                navController.navigate("PriorityFlag")
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Save",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalTime(onTimeSelected: (Int, Int) -> Unit) {
    MyAppTheme {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = true,
        )
        Column {
            TimeInput(
                state = timePickerState,
            )
            onTimeSelected(timePickerState.hour, timePickerState.minute)
        }
    }
}

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFF121212),
            surface = Color(0xFF121212),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White,
        ),
        content = content
    )
}

@Composable
fun PriorityFlag(navController: NavController, taskViewModel: TaskViewModel) {
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    val selectedTime = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedTime")

    val priorityFlag = painterResource(id = R.drawable.priorityflag)
    var selectedColumn by remember { mutableStateOf(-1) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(360.dp)
                .background(Color(0xFF363636))
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Task Priority",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(22.dp))

            @Composable
            fun PriorityColumn(index: Int, number: String) {
                val backgroundColor = if (selectedColumn == index) Color(0xFF8875FF) else Color(0xFF272727)

                Column(
                    modifier = Modifier
                        .size(64.dp)
                        .background(backgroundColor)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { selectedColumn = index }, // Update selected column
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(7.dp))
                    Image(
                        painter = priorityFlag,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = number,
                        color = Color.White,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PriorityColumn(index = 0, number = "1")
                PriorityColumn(index = 1, number = "2")
                PriorityColumn(index = 2, number = "3")
                PriorityColumn(index = 3, number = "4")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PriorityColumn(index = 4, number = "5")
                PriorityColumn(index = 5, number = "6")
                PriorityColumn(index = 6, number = "7")
                PriorityColumn(index = 7, number = "8")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PriorityColumn(index = 8, number = "9")
                PriorityColumn(index = 9, number = "10")
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Cancel",
                        style = TextStyle(
                            color = Color(0xFF8875FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .width(153.dp)
                        .background(Color(0xFF8875FF), shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 25.dp)
                        .height(40.dp)
                        .clickable {
                            if (selectedColumn != -1) {
                                taskViewModel.selectedPriority = selectedColumn + 1

                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "task",
                                    task
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "description",
                                    description
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "selectedDate",
                                    selectedDate
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "selectedTime",
                                    selectedTime
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "priorityFlag",
                                    selectedColumn + 1
                                )

                                navController.navigate("TaskPage")

                            }
                            Log.d(
                                "AddTask",
                                "Selected Date: $selectedDate, Selected Time: $selectedTime"
                            )
                            Log.d(
                                "AddTask",
                                "Task: $task, Description: $description, priorityflag: $priorityFlag"
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Save",
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

@Composable
fun TaskPage(navController: NavController, taskViewModel: TaskViewModel, searchQuery: String) {
    val taskList by taskViewModel.taskList.collectAsState()
    Log.d("TaskPage", "Task list size in Composable: ${taskList.size}")
    val task = navController.previousBackStackEntry?.savedStateHandle?.get<String>("task")
    val description = navController.previousBackStackEntry?.savedStateHandle?.get<String>("description")
    val selectedDate = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedDate")
    val selectedTime = navController.previousBackStackEntry?.savedStateHandle?.get<String>("selectedTime")
    val priorityFlag = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("priorityFlag")
    Log.d("AddTask", "Task: $task, Description: $description, Priorityflag: $priorityFlag")
    LaunchedEffect(task) {
        task?.let { it ->
            if (taskList.none { it.task == task }) {  // Avoid adding duplicate tasks
                if (description != null) {
                    taskViewModel.addTask(it, description, selectedDate ?: "", selectedTime ?: "", priorityFlag)
                }
            }
        }
    }
    var filteredTasks= if(searchQuery.isNotBlank()){
        taskList.filter {
            it.task.contains(searchQuery, ignoreCase = true)||
                    it.date.contains(searchQuery, ignoreCase = true)
        }
    }else{
        taskList
    }
    if (taskList.isEmpty()) {
        Text(
            text = "No tasks available.",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            // Use the collected taskList and map it to TaskRow
            items(filteredTasks) { taskItem ->
                Log.d("TaskPage", "Rendering TaskRow for: ${taskItem.task}")
                TaskRow(taskItem)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TaskRow(taskItem: TaskItem) {
    val priorityFlag = painterResource(id = R.drawable.priorityflag)

    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(Color(0xFF363636), shape = RoundedCornerShape(6.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = taskItem.task,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Space between items
            ){
                Text(
                    text = "${taskItem.date} at ${taskItem.time}",
                    color = Color.White,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
                )

                Row(
                    modifier = Modifier
                        .width(42.dp)
                        .height(29.dp)
                        .background(Color(0xFF363636))
                        .border(width = 1.dp, color = Color(0xFF8875FF), shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(8.dp)),
                ) {
                    Spacer(modifier = Modifier.height(7.dp))
                    Image(
                        painter = priorityFlag,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${taskItem.priorityFlag}",
                        color = Color.White,
                        style = TextStyle(fontSize = 12.sp),

                    )
                }

            }
        }
    }
}