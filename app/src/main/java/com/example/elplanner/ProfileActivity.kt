package com.example.elplanner

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elplanner.data.TaskViewModel
import com.example.elplanner.data.ViewModelProvider
import com.example.elplanner.ui.theme.ElPlannerTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.format.TextStyle

class ProfileActivity: ComponentActivity() {
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
                    color = Color.Black
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ProfilePage()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePage(){
    val navController = rememberNavController()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            Header()
            Spacer(modifier = Modifier.height(25.dp))
//            NavHost(navController = navController, startDestination = "Settings" ) {
//                composable("Settings") { Settings() }
//                composable("Accounts"){ Accounts()}
//
//            }
            Settings()
            Accounts()
            ElAbout()
        }
        BottomBar(navController)
    }
}

@Composable
fun Header() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    var username by remember { mutableStateOf<String?>(null) }
    if (currentUser != null) {
        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        LaunchedEffect(userId) {
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = snapshot.getValue(String::class.java) ?: "User"
                }
                override fun onCancelled(error: DatabaseError) {
                    username = "Error"
                }
            })
        }
    }
    Column(Modifier.fillMaxWidth(0.9f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 13.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Welcome ${username ?: "Loading..."}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start
            )
        }
    }
}

// TODO: add two buttons for complete and incomplete tasks"

@Composable
fun Settings(){
    val settingsIcon= painterResource(id = R.drawable.seetingsicon)
    val nextIcon= painterResource(id = R.drawable.nexticon)
    Column(Modifier.fillMaxWidth()){
        Text(
            text = "Settings",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color= Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = settingsIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "App Settings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = nextIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun Accounts(){
    val profileIcon= painterResource(id = R.drawable.profileicon)
    val passwordIcon= painterResource(id = R.drawable.changepassword)
    val nextIcon= painterResource(id = R.drawable.nexticon)
    Column(Modifier.fillMaxWidth()){
        Text(
            text = "Account",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color= Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = profileIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Change account name",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = nextIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = passwordIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Change account password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = nextIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ElAbout(){
    val aboutIcon= painterResource(id = R.drawable.about)
    val faqIcon= painterResource(id = R.drawable.faq)
    val logoutIcon= painterResource(id = R.drawable.logout)
    val nextIcon= painterResource(id = R.drawable.nexticon)
    Column(Modifier.fillMaxWidth()){
        Text(
            text = "El Planner",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color= Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = aboutIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "About US",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = nextIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = faqIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "FAQ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = nextIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(17.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = logoutIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Log out",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFFF4949),
                modifier = Modifier.weight(1f)
            )

        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}