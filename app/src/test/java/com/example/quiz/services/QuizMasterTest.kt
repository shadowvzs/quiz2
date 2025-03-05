package com.example.quiz.services

import com.example.quiz.models.BaseQuizItem
import org.junit.Test
import org.junit.jupiter.api.Assertions

class QuizMasterTest {
    private val store = QuizData()
    private val maxQuestion = 10
    private val allQuestion = store.questions.size

    @Test
    fun defaultQuizMasterData() {
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        Assertions.assertEquals(quizMaster.maxQuestion, maxQuestion)
        Assertions.assertEquals(quizMaster.allQuestions, store.questions)
        Assertions.assertEquals(quizMaster.currentQuestionIndex.value, -1)
        Assertions.assertEquals(quizMaster.currentQuestion, null)
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 0)
        Assertions.assertEquals(quizMaster.questionCounter, 0)
    }

    @Test
    fun initQuizMaster() {
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        quizMaster.init()
        Assertions.assertEquals(quizMaster.maxQuestion, maxQuestion)
        Assertions.assertEquals(quizMaster.allQuestions, store.questions)
        Assertions.assertNotEquals(quizMaster.currentQuestionIndex.value, -1)
        Assertions.assertEquals(quizMaster.currentQuestion, store.questions[quizMaster.currentQuestionIndex.value])
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 0)
        Assertions.assertEquals(quizMaster.questionCounter, 1)
    }

    @Test
    fun testTheMaxQuestionLimitForTheQuizMaster() {
        val maxQuestion = 1
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        quizMaster.init()
        Assertions.assertEquals(quizMaster.maxQuestion, maxQuestion)
        Assertions.assertEquals(quizMaster.allQuestions, store.questions)
        Assertions.assertNotEquals(quizMaster.currentQuestionIndex.value, -1)
        Assertions.assertEquals(quizMaster.currentQuestion, store.questions[quizMaster.currentQuestionIndex.value])
        Assertions.assertEquals(quizMaster.isLastQuestion, true)
        Assertions.assertEquals(quizMaster.currentScore, 0)
        Assertions.assertEquals(quizMaster.questionCounter, 1)
        quizMaster.selectNewQuestion()
        Assertions.assertEquals(quizMaster.questionCounter, 1)
        Assertions.assertNull(quizMaster.currentQuestion)
    }

    @Test
    fun wrongAnswerToTheCurrentQuestion() {
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        quizMaster.init()

        val currentQuestion = quizMaster.currentQuestion!!
        val wrongOption = currentQuestion.options.first {
            answer -> answer != currentQuestion.answer
        }
        Assertions.assertEquals(quizMaster.questionCounter, 1)
        quizMaster.selectAnswer(wrongOption)
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 0)
        Assertions.assertEquals(quizMaster.questionCounter, 1)
        Assertions.assertEquals(quizMaster.answeredQuestions.size, 0)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion)
        Assertions.assertArrayEquals(quizMaster.failedQuestions.toTypedArray(), arrayOf(currentQuestion.id))
    }

    @Test
    fun goodAnswerToTheCurrentQuestion() {
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        quizMaster.init()

        val currentQuestion = quizMaster.currentQuestion!!
        quizMaster.selectAnswer(currentQuestion.answer)
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 1)
        Assertions.assertEquals(quizMaster.failedQuestions.size, 0)
        Assertions.assertArrayEquals(quizMaster.answeredQuestions.toTypedArray(), arrayOf(currentQuestion.id))
        Assertions.assertEquals(quizMaster.questionCounter, 1)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion - 1)
    }

    @Test
    fun happyPathWithAThreeGoodAnswerAndOneWrongScore() {
        val maxQuestion = 4
        val quizMaster = QuizMaster(store.questions, maxQuestion)
        quizMaster.init()

        // first question, already verified above
        val currentQuestion = quizMaster.currentQuestion!!
        quizMaster.selectAnswer(currentQuestion.answer)
        quizMaster.selectNewQuestion()

        // second question
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 1)
        Assertions.assertEquals(quizMaster.failedQuestions.size, 0)
        Assertions.assertEquals(quizMaster.answeredQuestions.size, 1)
        Assertions.assertEquals(quizMaster.questionCounter, 2)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion - 1)
        quizMaster.selectAnswer(quizMaster.currentQuestion!!.answer)
        quizMaster.selectNewQuestion()

        // third question
        Assertions.assertEquals(quizMaster.isLastQuestion, false)
        Assertions.assertEquals(quizMaster.currentScore, 2)
        Assertions.assertEquals(quizMaster.failedQuestions.size, 0)
        Assertions.assertEquals(quizMaster.answeredQuestions.size, 2)
        Assertions.assertEquals(quizMaster.questionCounter, 3)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion - 2)
        val wrongOption = currentQuestion.options.first {
                answer -> answer != currentQuestion.answer
        }
        quizMaster.selectAnswer(wrongOption)
        quizMaster.selectNewQuestion()

        // fourth question
        Assertions.assertEquals(quizMaster.isLastQuestion, true)
        Assertions.assertEquals(quizMaster.currentScore, 2)
        Assertions.assertEquals(quizMaster.failedQuestions.size, 1)
        Assertions.assertEquals(quizMaster.answeredQuestions.size, 2)
        Assertions.assertEquals(quizMaster.questionCounter, 4)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion - 2)
        quizMaster.selectAnswer(quizMaster.currentQuestion!!.answer)
        quizMaster.selectNewQuestion()

        // final score
        Assertions.assertEquals(quizMaster.currentScore, 3)
        Assertions.assertEquals(quizMaster.failedQuestions.size, 1)
        Assertions.assertEquals(quizMaster.answeredQuestions.size, 3)
        Assertions.assertEquals(quizMaster.questionCounter, 4)
        Assertions.assertEquals(quizMaster.remainingQuestions.size, allQuestion - 3)
        Assertions.assertEquals(quizMaster.currentScore, 3)
        Assertions.assertNull(quizMaster.currentQuestion)
    }
}