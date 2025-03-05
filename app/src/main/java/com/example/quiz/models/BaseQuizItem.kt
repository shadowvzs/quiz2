package com.example.quiz.models

import com.google.gson.annotations.SerializedName

data class BaseQuizItem (
    val id: String,
    val question: String,
    val options: List<String>,
    val answer: String,
)