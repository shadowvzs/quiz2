package com.example.quiz.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.quiz.Routes
import com.example.quiz.ui.theme.RedGradient1
import com.example.quiz.ui.theme.RedGradient2
import com.example.quiz.viewModels.QuizViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun NewScoreScreen(navController: NavController, viewModel: QuizViewModel, currentScore: Int) {
    var username by remember { mutableStateOf("") }

    Column (
        Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(
                    RedGradient1,
                    RedGradient2
                ),
                start = Offset.Zero,
                end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        ).padding(20.dp, 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentScore > viewModel.quizMaster.maxQuestion * 0.75) {
            Text(
                text = "Congratulation, you are a quiz master!",
                fontSize = 10.em,
                color = Color.Red,
                modifier = Modifier.padding(0.dp, 8.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Text(
            text = "Your score: $currentScore",
            fontSize = 5.em,
            color = Color.Blue,
            modifier = Modifier.padding(0.dp, 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Enter your name",
            fontSize = 10.em,
            color = Color.Yellow,
            modifier = Modifier.padding(0.dp, 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = username,
            textStyle = TextStyle(
                fontSize = 6.em
            ),
            onValueChange = { newText ->
                username = newText
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                MainScope().launch {
                    viewModel.scoreManager.add(currentScore, username)
                    username = ""
                    navController.navigate(Routes.highScoreScreen)
                }
            },
            modifier = Modifier.padding(0.dp, 8.dp),
            enabled = username.length in 4..23
        ) {
            Text(
                text = "Save",
                fontSize = 6.em,
            )
        }
    }
}