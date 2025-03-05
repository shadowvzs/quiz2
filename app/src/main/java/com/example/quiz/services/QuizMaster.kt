package com.example.quiz.services

import com.example.quiz.models.BaseQuizItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

// This class manage the selecting a question, verify if the answer was correct or no, return the score
class QuizMaster(
    val allQuestions: Array<BaseQuizItem>,
    val maxQuestion: Int
) {
    // list of question id, which was already answered
    internal var answeredQuestions: MutableList<String> = mutableListOf()
    // list of failed question ids
    internal var failedQuestions: MutableList<String> = mutableListOf()
    // getter which will returns the remaining/non answered questions
    internal val remainingQuestions get() = allQuestions.filter {
        question -> !answeredQuestions.contains(question.id)
    }

    // getter, return if the current question should be the last
    val isLastQuestion get() = this.questionCounter >= maxQuestion
    // getter based on counting how many correct answer we saved into answeredQuestions
    val currentScore get() = answeredQuestions.count()
    val currentQuestionIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    // currently shown question
    val currentQuestion get() = allQuestions.getOrNull(currentQuestionIndex.value)
    // counting the shown questions since the init
    // private val _isLoading = MutableStateFlow(false)
    var questionCounter: Int = 0

    // select & assign a new question
    fun selectNewQuestion() {
        val newQuizItem: BaseQuizItem?

        if (questionCounter >= maxQuestion) {
            this.currentQuestionIndex.value = -1;
            return;
        }
        // +10% chance to get a failed question, if there are at least 1 failed question
        if (failedQuestions.size > 0 && Random.nextInt(0, 100) < 10) {
            val failedQuestionIndex = Random.nextInt(0, failedQuestions.count())
            val failedQuestionId = failedQuestions[failedQuestionIndex]
            newQuizItem = allQuestions.find { q -> q.id === failedQuestionId }
        } else {
            // else we just choose 1 question from the remaining question list (which can be failed one too)
            val questionIndex = Random.nextInt(0, remainingQuestions.count())
            newQuizItem = remainingQuestions[questionIndex]
        }

        if (newQuizItem == null) { return; }

        // we save the new question index into the currentQuestionIndex variable
        val itemIndex = allQuestions.indexOf(newQuizItem);
        this.currentQuestionIndex.value = itemIndex

        // increase the question counter to know, how many question was answered
        questionCounter++


    }

    // verify if the given answer is correct for the current question
    fun selectAnswer(answer: String) {
        if (currentQuestion == null) { return; }

        val question = currentQuestion!!

        // if the answer was correct then add to answered question list, else to the failed question list
        if (question.answer == answer) {
            answeredQuestions.add(0, question.id)
        } else if (!failedQuestions.contains(question.id)) {
            failedQuestions.add(0, question.id)
        }
    }

    // init or reset the question counter, failed and answered lists and request a new question
    fun init() {
        if (questionCounter == 1 && currentQuestion != null) {
            return
        }
        questionCounter = 0
        failedQuestions.clear()
        answeredQuestions.clear()
        this.selectNewQuestion()
    }
}