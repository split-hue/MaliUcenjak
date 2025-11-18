package com.example.ucenjeabecede

import android.content.Context
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@kotlinx.serialization.InternalSerializationApi
class ProgressRepository(context: Context) {
    private val store = context.progressStore


    val progressFlow: Flow<Progress>
        get() = store.data


    suspend fun addLetter(letter: String) {
        store.updateData { current ->
            if (letter in current.completedLetters) current
            else current.copy(completedLetters = current.completedLetters + letter)
        }
    }


    suspend fun removeLetter(letter: String) {
        store.updateData { current ->
            current.copy(completedLetters = current.completedLetters - letter)
        }
    }


    suspend fun reset() {
        store.updateData { Progress() }
    }
//****************************************to je sam za teste
    suspend fun setLetters(letters: List<String>) {
        store.updateData { Progress(letters) }
    }

    suspend fun clearLetters() {
        store.updateData { Progress(emptyList()) }
    }


}