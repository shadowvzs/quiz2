package com.example.quiz.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz.models.BaseQuizItem
import com.example.quiz.services.QuizMaster
import com.example.quiz.services.ScoreManager
import com.example.quiz.utils.readJSONFromAssets
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Singleton instance which have persistent state during the whole app lifecycle
// This is a model for the view which contains everything what we use in the view
// included the QuizMaster (game business logic), ScoreManager (manage the score records)
class QuizViewModel(
    application: Application
): AndroidViewModel(application) {
    private val context: Context
        get() = this.getApplication<Application>().applicationContext
    private val _isLoading = MutableStateFlow(false)
    private var _isLoaded = false

    lateinit var quizMaster: QuizMaster
    var scoreManager: ScoreManager = ScoreManager(application.applicationContext)
    val isLoading = _isLoading.asStateFlow()

    // load all required information for the aoo
    fun loadData() {
        // in case if it was loaded already then we not load again the data
        // since this is inside the LaunchEffect in the MainActivity, this can
        // triggered even during the app orientation change (eg. portrait -> landscape)
        // however the loaded information remain the same
        if (_isLoaded) { return; }

        viewModelScope.launch {
            // if isLoading is true then we can show different UI elements if needed
            _isLoading.value = true
            // load highScores from the DataStore
            scoreManager.loadData()
            // load the quiz questions from the JSON file
            val jsonString = readJSONFromAssets(context, "questions.json")
            // parse the JSON string to Array<BaseQuizItem>
            val allQuestions = Gson().fromJson(jsonString, Array<BaseQuizItem>::class.java)
            // pass the loaded questions to QuizMaster which will handle the game logic
            quizMaster = QuizMaster(allQuestions = allQuestions, maxQuestion = 10)
            // set the loading to false, so the UI can react according to it
            _isLoading.value = false
            // set isLoaded to true, so cannot loaded again the same information
            _isLoaded = true
        }
    }
}