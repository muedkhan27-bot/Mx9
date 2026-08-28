package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class GeminiApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val systemPrompt = """
        You are J.A.R.V.I.S. (Just A Rather Very Intelligent System), the ultra-advanced AI created by Tony Stark.
        You speak with a sophisticated, polite, witty British accent and demeanor.
        Address the user respectfully as "Sir" or "Mr. Stark".
        Provide concise, highly logical, razor-sharp, and direct answers (maximum 2-3 sentences unless complex technical explanation is requested).
        Keep your output clean and natural for Text-to-Speech playback.
    """.trimIndent()

    suspend fun queryGemini(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API key not configured in Secrets panel, sir. Engaging offline logical engine."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val requestJson = """
            {
              "systemInstruction": {
                "parts": [
                  { "text": ${escapeJson(systemPrompt)} }
                ]
              },
              "contents": [
                {
                  "parts": [
                    { "text": ${escapeJson(prompt)} }
                  ]
                }
              ],
              "generationConfig": {
                "temperature": 0.7,
                "topP": 0.95,
                "maxOutputTokens": 300
              }
            }
        """.trimIndent()

        val requestBody = requestJson.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiApiClient", "Gemini HTTP ${response.code}: $responseBody")
                return@withContext "Stark satellite uplink encountered an error (Code ${response.code}), sir. Falling back to local offline logic."
            }

            val text = parseGeminiResponse(responseBody)
            text ?: "Quantum transmission received empty payload, sir."
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "Gemini error: ${e.message}")
            "Stark network uplink is offline, sir. Reverting to local tactical memory: ${e.message}"
        }
    }

    private fun parseGeminiResponse(json: String): String? {
        return try {
            val root = moshi.adapter(Map::class.java).fromJson(json) as? Map<*, *>
            val candidates = root?.get("candidates") as? List<*>
            val firstCandidate = candidates?.firstOrNull() as? Map<*, *>
            val content = firstCandidate?.get("content") as? Map<*, *>
            val parts = content?.get("parts") as? List<*>
            val firstPart = parts?.firstOrNull() as? Map<*, *>
            firstPart?.get("text") as? String
        } catch (e: Exception) {
            Log.e("GeminiApiClient", "JSON parse error: ${e.message}")
            null
        }
    }

    private fun escapeJson(str: String): String {
        val escaped = str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
