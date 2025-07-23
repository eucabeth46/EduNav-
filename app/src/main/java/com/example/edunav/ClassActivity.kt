package com.example.edunav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import android.app.Activity
import androidx.compose.runtime.Composable

class ClassActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message = intent.getStringExtra("message") ?: "No message received"
        setContent {
            ClassScreen(message, this)
        }
    }
}

@Composable
fun ClassScreen(message: String, activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Class", fontSize = 24.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is your class activity screen.",
            fontSize = 18.sp,color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }) {
            Text("Back", color = Color.White, fontSize = 24.sp)
        }

    }
}