package com.example.elplanner

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.elplanner.ui.theme.ElPlannerTheme

class FAQActivity : ComponentActivity() {
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
                        FAQPage()
                    }
                }
            }
        }
    }
}

@Composable
fun FAQPage() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FAQ",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF8875FF), // Purple shade
            modifier = Modifier.padding(bottom = 16.dp, top = 20.dp)
        )

        // Each FAQ item
        FAQItem(
            question = "What is El Planner?",
            answer = "El Planner is a task management and productivity app designed to help you organize your tasks, set reminders, and track your progress."
        )

        FAQItem(
            question = "How do I add a new task?",
            answer = "Tap on the '+' button at the bottom of the screen, fill in the task details, and click save."
        )

        FAQItem(
            question = "How do I add category to a task?",
            answer = "To add category to a task, swipe right on the task in your list and choose the category you want to place it in."
        )

        FAQItem(
            question = "How do I delete a task?",
            answer = "To delete a task, swipe left on the task in your list."
        )

        FAQItem(
            question = "Can I sync tasks across devices?",
            answer = "Currently, tasks are saved locally. Syncing across devices is a feature coming in future updates."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Still have questions?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8875FF),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:[eleato2020@gmail.com]")
                }
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8875FF))
        ) {
            Text(
                text = "Send Email",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Version 1.0 | © 2024 El Planner",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}