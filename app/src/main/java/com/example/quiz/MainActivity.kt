package com.example.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiz.screens.GameScreen
import com.example.quiz.screens.HomeScreen
import com.example.quiz.screens.NewScoreScreen
import com.example.quiz.screens.ScoreScreen
import com.example.quiz.ui.theme.BlueGradient1
import com.example.quiz.ui.theme.BlueGradient2
import com.example.quiz.viewModels.QuizViewModel

// The main and only activity, entry point of the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // render the app content
        setContent { QuizTheme() }
    }
}

// wrapper with handling the isLoading state, hosting the Navigation Controller, set the routes
@Composable
fun QuizTheme() {

    val viewModel = viewModel<QuizViewModel>();
    LaunchedEffect(key1 = true) {
        viewModel.loadData()
    }

    // we use the nav controller to able to go from one screen into the another
    // each screen like a different page in a SPA
    val navController = rememberNavController()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)

    // in the normal case, if we load bigger data and not nearly instant like here
    // then make sense to have a spinner until the app load those data (boot the required data)
    if (isLoading) {
        Column (
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
            ).padding(20.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "Loading...",
            )
        }
    } else {
        // Navigation routes:
        // - to Home Screen (initial screen, with the main menu)
        // - to new score screen (enter player name screen)
        // - to score screen (highScore record list screen)
        // - to game screen (the question answering/game screen)
        NavHost(navController = navController, startDestination = "homeScreen", builder = {
            composable(Routes.homeScreen) {
                HomeScreen(navController)
            }
            composable(Routes.newScoreScreen + "/{currentScore}") {
                val currentScore = it.arguments?.getString("currentScore")!!
                NewScoreScreen(navController, viewModel, currentScore.toInt())
            }
            composable(Routes.highScoreScreen) {
                ScoreScreen(navController, viewModel)
            }
            composable(Routes.gameScreen) {
                GameScreen(navController, viewModel)
            }
        })
    }
}
