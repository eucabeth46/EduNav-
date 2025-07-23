package com.example.edunav

import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import android.app.Activity
import androidx.compose.ui.graphics.Color


class TutorialsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TutorialsScreen(this)
        }
    }
}

@Composable
fun TutorialsScreen( activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Tutorials", fontSize = 24.sp,color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Here you can learn Braille step by step.", fontSize = 18.sp,color = Color.White)

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            activity.setResult(Activity.RESULT_OK)  // Send result back
            activity.finish()                      // Close this screen
        }) {
            Text("Back", color = Color.White, fontSize = 24.sp)
        }
    }
}