package com.example.quiz.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import com.example.quiz.Routes
import com.example.quiz.ui.theme.BlueGradient1
import com.example.quiz.ui.theme.BlueGradient2
import kotlin.system.exitProcess

@Composable
fun HomeScreen(navController: NavController) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(
                    BlueGradient1,
                    BlueGradient2
                ),
                start = Offset.Zero,
                end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        ).padding(20.dp, 20.dp)
    ) {
        Text(
            text = "Bible Quiz",
            fontSize = 10.em,
            color = androidx.compose.ui.graphics.Color.White,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .offset(y = 30.dp)
                .border(
                    width = 3.dp,
                    color = Color(0x33333333),
                    shape = RoundedCornerShape(30.dp),
                )
                .background(
                    color = Color.White.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(30.dp, 40.dp)
        ) {
            MenuItem("New Game", navController, Routes.gameScreen)
            MenuItem("Records", navController, Routes.highScoreScreen)
            MenuItem("Quit", navController, "exit")
        }
    }
}

@Composable
fun MenuItem(text: String, navController: NavController, route: String) {
    Button(onClick = {
        if (route == "exit") {
            exitProcess(0);
        }
        navController.navigate(route)
    }) {
        Text(text = text, fontSize = 8.em)
    }
}