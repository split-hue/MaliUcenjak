package com.example.ucenjeabecede


import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@kotlinx.serialization.InternalSerializationApi
// Serializer for Progress using kotlinx.serialization
object ProgressSerializer : Serializer<Progress> {
    override val defaultValue: Progress = Progress()


    override suspend fun readFrom(input: InputStream): Progress {
        return try {
            val text = input.readBytes().toString(Charsets.UTF_8)
            Json.decodeFromString(Progress.serializer(), text)
        } catch (e: SerializationException) {
            defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }


    override suspend fun writeTo(t: Progress, output: OutputStream) {
        withContext(Dispatchers.IO) {
            val text = Json.encodeToString(Progress.serializer(), t)
            output.write(text.toByteArray(Charsets.UTF_8))
        }
    }
}


// DataStore property on Context
@kotlinx.serialization.InternalSerializationApi
val Context.progressStore: DataStore<Progress> by dataStore(
    fileName = "napredek.json",
    serializer = ProgressSerializer,
    corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { Progress() })
)