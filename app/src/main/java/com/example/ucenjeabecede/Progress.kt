package com.example.ucenjeabecede

import kotlinx.serialization.Serializable
@kotlinx.serialization.InternalSerializationApi
@Serializable
data class Progress(
    val completedLetters: List<String> = emptyList()
)
