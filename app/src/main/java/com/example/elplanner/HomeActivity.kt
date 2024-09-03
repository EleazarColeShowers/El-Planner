package com.example.elplanner

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.ui.theme.ElPlannerTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale


class HomeActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Index()
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Index() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            HomePage()
            NavHost(navController = navController, startDestination = "EmptyPage") {
                composable("EmptyPage") { EmptyPage() }
                composable("AddTask"){ AddTask(navController)}
                composable("DateTime"){ DateTime(navController)}
                composable("TimeView"){ TimeView()}
            }
        }
        BottomBar(navController)
    }
}

@Composable
fun HomePage() {
    val menu = painterResource(id = R.drawable.menu)

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTask(navController: NavController) {
    var task by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val setTime= painterResource(id = R.drawable.focusicon)
    val flagTask= painterResource(id = R.drawable.priorityflag)
    val saveTask= painterResource(id =R.drawable.send)
    val tagTask = painterResource(id = R.drawable.tag)
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
//            horizontalAlignment = Alignment.CenterHorizontally,
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
                value = task,
                onValueChange = { task = it },
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
                value = description,
                onValueChange = { description = it },
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
                Image(
                    painter = setTime,
                    contentDescription = null,
                    modifier= Modifier
                        .size(24.dp)
                        .clickable { navController.navigate("DateTime") }
                )
                Spacer(modifier = Modifier.width(24.dp))
                Image(
                    painter = tagTask,
                    contentDescription = null,
                    modifier= Modifier
                        .size(24.dp)
                        .clickable { }
                )
                Spacer(modifier = Modifier.width(24.dp))
                Image(
                    painter = flagTask,
                    contentDescription = null,
                    modifier= Modifier
                        .size(24.dp)
                        .clickable { }
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = saveTask,
                    contentDescription = null,
                    modifier= Modifier
                        .size(24.dp)
                        .clickable { }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTime(navController: NavController){
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
            CalendarView(onDateSelected = {

            })
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ){
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
                            navController.navigate("TimeView")
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
fun CalendarView(onDateSelected: (String) -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
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

        val daysOfWeek = remember { DayOfWeek.values() }
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in daysOfWeek) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dayOfWeek.name.take(3),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
        val daysInMonth = currentMonth.lengthOfMonth()
        var day = 1
        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in daysOfWeek) {
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
                                            today == date -> Color(0xFF8875FF) // Highlight current day
                                            selectedDate == date -> Color(0xFF8875FF) // Highlight selected day
                                            else -> Color.Transparent
                                        }
                                    )
                                    .padding(3.dp)
                                    .clickable {
                                        selectedDate = date
                                        // Trigger the lambda to pass the selected date
                                        onDateSelected("${date.dayOfMonth}, ${date.monthValue}, ${date.year}")
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

@Composable
fun TimeView() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(350.dp)  // Adjusted height to accommodate the time picker
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

            // Digital Time Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),  // Space between "Choose Time" and time picker
                horizontalArrangement = Arrangement.Center
            ) {
                TimeSelector()
            }

            // Row with Cancel and Save buttons
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
                            // Handle Cancel click
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
                            // Handle Save click
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
fun TimeSelector() {
    val hours = (1..12).toList()
    val minutes = (0..59).toList()
    val amPm = listOf("AM", "PM")

    var selectedHour by remember { mutableStateOf(hours.first()) }
    var selectedMinute by remember { mutableStateOf(minutes.first()) }
    var selectedAmPm by remember { mutableStateOf(amPm.first()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours
        NumberPicker(
            items = hours,
            selectedItem = selectedHour,
            onItemSelected = { selectedHour = it }
        )

        Text(
            text = ":",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Minutes
        NumberPicker(
            items = minutes,
            selectedItem = selectedMinute,
            onItemSelected = { selectedMinute = it }
        )

        Spacer(modifier = Modifier.width(16.dp))

        // AM/PM
        NumberPicker(
            items = amPm,
            selectedItem = selectedAmPm,
            onItemSelected = { selectedAmPm = it }
        )
    }
}

@Composable
fun <T> NumberPicker(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit
) {
    // Apply a gradient to fade out the top and bottom items
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color(0x80000000),
            Color.Black,
            Color(0x80000000),
            Color.Transparent
        ),
        startY = 0f,
        endY = 150f
    )

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(150.dp) // Adjust height as necessary
            .background(gradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .width(60.dp)
                .height(150.dp),  // Adjust height as necessary
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(items) { _, item ->
                Text(
                    text = item.toString(),
                    color = if (item == selectedItem) Color.White else Color.Gray,
                    fontSize = if (item == selectedItem) 24.sp else 18.sp,
                    fontWeight = if (item == selectedItem) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onItemSelected(item) }
                )
            }
        }
    }
}