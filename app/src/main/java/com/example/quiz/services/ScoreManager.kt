package com.example.quiz.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quiz.models.ScoreHistory
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Singleton for Preferences DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "preferencesDataStore"
)

// we save only a limited amount of highScore, like the Top10
const val MAX_SCORE_LIST_SIZE = 10

// Score manager will load the saved scores from the DataStore
// and allows to update the list if needed
class ScoreManager(private val context: Context) {
    // DataStore key for our score history (need a key because it is key/value pair storage)
    private val preferenceKey = stringPreferencesKey("scoreHistory")
    // when we load the saved score than we store in this variable
    val savedScores = mutableListOf<ScoreHistory>()

    // verify if the current score should be in the top list or no
    fun canBeOnTheRecordList(score: Int): Boolean {
        if (savedScores.size < MAX_SCORE_LIST_SIZE) {
            return true
        }
        return savedScores.map { record -> record.score }.minOf({ it }) < score
    }

    // add a new score and save it into the data store
    suspend fun add(score: Int, name: String) {
        // we should save the date when the record happened
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val current = LocalDateTime.now().format(formatter)

        // add the record to the end of the score list
        savedScores.add(ScoreHistory(
            score = score,
            date = current,
            name = name
        ))

        // sort it because the newly added score isn't in the right place
        savedScores.sortByDescending { it.score }

        // if there is more score record than the maximum, then remove the last one
        if (savedScores.size > MAX_SCORE_LIST_SIZE) {
            savedScores.removeAt(savedScores.lastIndex)
        }

        // update the data in the DataStore
        context.dataStore.edit { settings ->
            val text = Gson().toJson(savedScores)
            settings[preferenceKey] = text
        }
    }

    // load scores from the DataStore, load as a string then convert to a list of ScoreHistory
    suspend fun loadData() {
        val loadedJSON = context.dataStore.data.map {
            it[preferenceKey] ?: "[]"
        }.first()

        val parsedData = Gson().fromJson(loadedJSON, Array<ScoreHistory>::class.java).toList()
        savedScores.clear()
        savedScores.addAll(parsedData)
    }
}