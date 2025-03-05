package com.example.quiz.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.quiz.Routes
import com.example.quiz.ui.theme.GreenGradient1
import com.example.quiz.ui.theme.GreenGradient2
import com.example.quiz.viewModels.QuizViewModel

@Composable
fun GameScreen(navController: NavController, viewModel: QuizViewModel) {
    // reset the quizMaster state when the user enter to this screen
    val quizMaster = viewModel.quizMaster
    var selectedAnswer by rememberSaveable { mutableStateOf("") }
    var showCorrectAnswer by rememberSaveable { mutableStateOf(false) }
    val currentItemIndex by quizMaster.currentQuestionIndex.collectAsStateWithLifecycle(0)
    val currentItem = quizMaster.allQuestions.getOrNull(currentItemIndex)
    var options by rememberSaveable { mutableStateOf(listOf<String>()) }

    // if you rotate the device then the activity remounted and launchEffect run again
    // if we already was example at second question, we not want to reset the current state of the game
    LaunchedEffect(Unit) {
        if (quizMaster.questionCounter < 2) {
            viewModel.quizMaster.init()
        }
    }

    // everytime if the current question was changed, we will shuffle the options
    LaunchedEffect(key1 = currentItemIndex) {
        if (currentItem != null) {
            options = currentItem.options.shuffled()
        }
    }

    if (viewModel.quizMaster.currentQuestion != null) {
        Column (
            Modifier.fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GreenGradient1,
                            GreenGradient2
                        ),
                        start = Offset.Zero,
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp, 10.dp)
                .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(38.dp),
            ) {
                val quizItemOptions = currentItem!!.options
                item {
                    Text(
                        text = currentItem.question,
                        fontSize = 9.em,
                        modifier = Modifier.padding(10.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Text(
                        text = "Questions: ${quizMaster.questionCounter}/${quizMaster.maxQuestion}",
                        fontSize = 4.em,
                        modifier = Modifier.padding(10.dp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
                // show the options/potential answers
                items(options) {
                    // in case the user submitted the answer then we will highlight the correct answer
                    if (showCorrectAnswer && it == currentItem.answer) {
                        Button(
                            onClick = { selectedAnswer = it },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Text(
                                text = it,
                                fontSize = 7.em,
                            )
                        }
                    // highlight the selected answer
                    } else if (it == selectedAnswer) {
                        Button(
                            onClick = { selectedAnswer = it },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text(
                                text = it,
                                fontSize = 7.em,
                            )
                        }
                    // show the potential answer when user not selected that specific answer
                    } else {
                        Button(
                            onClick = { selectedAnswer = it },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Text(
                                text = it,
                                fontSize = 7.em,
                            )
                        }
                    }
                }
                // after the correct answer was shown, the user cannot tap to the submit & give up button
                if (!showCorrectAnswer) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            // show give up button, which navigate back to the main screen
                            FilledTonalButton(
                                onClick = {
                                    quizMaster.questionCounter = 0
                                    navController.navigate(Routes.homeScreen)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text(
                                    text = "Give Up",
                                    fontSize = 6.em,
                                )
                            }
                            // show the submit answer button after the user selected an answer
                            if (selectedAnswer != "") {
                                Button(
                                    onClick = {
                                        // if the user submit his answer then highlight the correct answer for 1 second
                                        showCorrectAnswer = true
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            showCorrectAnswer = false
                                            quizMaster.selectAnswer(selectedAnswer)
                                            // if it is the last question then navigate to the score screen
                                            if (quizMaster.isLastQuestion) {
                                                quizMaster.questionCounter = 0
                                                if (quizMaster.currentScore > 0 && viewModel.scoreManager.canBeOnTheRecordList(quizMaster.currentScore)) {
                                                    navController.navigate(Routes.newScoreScreen + "/${quizMaster.currentScore}")
                                                } else {
                                                    navController.navigate(Routes.highScoreScreen)
                                                }
                                            // if not the last question then we need to select a new one
                                            } else {
                                                quizMaster.selectNewQuestion()
                                                selectedAnswer = ""
                                            }
                                          }, 1000)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                                ) {
                                    Text(
                                        text = "Submit",
                                        fontSize = 6.em,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Text(text = "Loading...")
    }
}