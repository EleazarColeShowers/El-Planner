package com.example.elplanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
                            ProfilePage(auth, taskViewModel)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(auth: FirebaseAuth,taskViewModel: TaskViewModel){
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
            TaskProgress(taskViewModel)
            Spacer(modifier = Modifier.height(32.dp))
            Settings()
            Accounts()
            ElAbout(auth)
        }
        BottomBar(navController)
    }
}

@Composable
fun Header() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    var username by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    if (currentUser != null) {
        val userId = currentUser.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId")
        LaunchedEffect(userId) {
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fetchedUsername = snapshot.getValue(String::class.java) ?: "User"
                    username = fetchedUsername
                    with(sharedPreferences.edit()) {
                        putString("username", fetchedUsername)
                        apply()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    username = "Error"
                }
            })
        }
    }
    if (username == null) {
        username = sharedPreferences.getString("username", "Loading...")
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
fun TaskProgress(taskViewModel: TaskViewModel){
    val taskList by taskViewModel.taskList.collectAsState()
    val tasksLeft = taskList.count { !it.completed }
    val tasksDone = taskList.count { it.completed }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
        ){
        Row(
            Modifier
                .width(154.dp)
                .height(58.dp)
                .background(Color(0xFF363636)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "$tasksLeft tasks left",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )
        }
        Row(
            Modifier
                .width(154.dp)
                .height(58.dp)
                .background(Color(0xFF363636)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$tasksDone tasks done",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            )

        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Accounts(){
    val context= LocalContext.current
    val profileIcon= painterResource(id = R.drawable.profileicon)
    val passwordIcon= painterResource(id = R.drawable.changepassword)
    val nextIcon= painterResource(id = R.drawable.nexticon)
    var showDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(225.dp)
                    .background(
                        color = Color(0xFF363636),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Change Account Name",
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newUsername,
                        onValueChange = { newUsername = it },
                        placeholder = { Text("Enter new account name", color = Color.Gray) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White
                        ),

                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
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
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Update", color = Color.White)
                    }
                }
            }
        }
    }

    if (showPasswordDialog) {
        Dialog(onDismissRequest = { showPasswordDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        color = Color(0xFF363636),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .height(311.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Change Account Password",
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        placeholder = { Text("Enter old password", color = Color.Gray) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White
                        ),
                        visualTransformation = PasswordVisualTransformation() // For hiding password input
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = { Text("Enter new password", color = Color.Gray) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedBorderColor = Color(0xFF8875FF),
                            unfocusedBorderColor = Color.White
                        ),
                        visualTransformation = PasswordVisualTransformation() // For hiding password input
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            changePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword,
                                onSuccess = {
                                    Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    showPasswordDialog = false
                                },
                                onFailure = {
                                    Toast.makeText(context, "Old password incorrect", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(text = "Update", color = Color.White)
                    }
                }
            }
        }
    }

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
                .fillMaxWidth()
                .clickable { showDialog = true }
            ,
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
                .fillMaxWidth()
                .clickable { showPasswordDialog = true },
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
fun ElAbout(auth: FirebaseAuth){
    val aboutIcon= painterResource(id = R.drawable.about)
    val faqIcon= painterResource(id = R.drawable.faq)
    val logoutIcon= painterResource(id = R.drawable.logout)
    val nextIcon= painterResource(id = R.drawable.nexticon)
    val context= LocalContext.current
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
                .fillMaxWidth()
                .clickable {
                    auth.signOut()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("navigate_to", "Carousel")
                    context.startActivity(intent)
                },
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