package com.example.quiz.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.quiz.Routes
import com.example.quiz.ui.theme.RedGradient1
import com.example.quiz.ui.theme.RedGradient2
import com.example.quiz.viewModels.QuizViewModel

@Composable
fun ScoreScreen(navController: NavController, viewModel: QuizViewModel) {
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
        Text(
            text = "HighScore",
            fontSize = 10.em,
            color = Color.Yellow,
            modifier = Modifier.padding(0.dp, 8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.scoreManager.savedScores.size > 0) {
                items(viewModel.scoreManager.savedScores) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.score.toString(),
                            color = Color.Black,
                            fontSize = 6.em,
                            fontWeight = FontWeight(700)
                        )
                        Text(
                            text = " - ${it.name} ",
                            color = Color.DarkGray,
                            fontSize = 6.em,
                        )
                        Text(
                            text = "(${it.date.slice(IntRange(0, 9))})",
                            color = Color.DarkGray,
                            fontSize = 5.em,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "No saved scores...",
                        fontSize = 8.em,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(20.dp, 40.dp)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { navController.navigate(Routes.homeScreen) },
                    modifier = Modifier.padding(0.dp, 8.dp)
                ) {
                    Text(
                        text = "Back",
                        fontSize = 6.em,
                    )
                }
            }
        }
    }
}