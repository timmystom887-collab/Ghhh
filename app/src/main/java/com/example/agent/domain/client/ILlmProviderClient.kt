package com.example.agent.domain.client

import com.example.agent.data.remote.ChatRequest
import com.example.agent.data.remote.Content
import com.example.agent.data.remote.GeminiApi
import com.example.agent.data.remote.GeminiApiService
import com.example.agent.data.remote.GenerationConfig
import com.example.agent.data.remote.Part
import com.example.agent.data.remote.ThinkingConfig
import com.example.agent.domain.model.LlmErrorType
import com.example.agent.domain.model.LlmResult
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

interface ILlmProviderClient {
    suspend fun generateResponse(
        prompt: String,
        systemInstruction: String? = null,
        model: String = "gemini-2.5-flash",
        apiKey: String = "",
        thinkingLevel: String = "high"
    ): LlmResult
}

class GeminiProviderClient(
    private val api: GeminiApi = GeminiApiService.api
) : ILlmProviderClient {

    override suspend fun generateResponse(
        prompt: String,
        systemInstruction: String?,
        model: String,
        apiKey: String,
        thinkingLevel: String
    ): LlmResult {
        if (apiKey.isBlank()) {
            return LlmResult.Error(
                errorType = LlmErrorType.AUTHENTICATION_FAILED,
                message = "API key is missing or blank. Please configure a valid API key in settings."
            )
        }

        val request = ChatRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = systemInstruction?.let {
                Content(parts = listOf(Part(text = it)))
            },
            generationConfig = GenerationConfig(
                temperature = 0.7f,
                thinkingConfig = ThinkingConfig(thinkingLevel = thinkingLevel)
            )
        )

        return try {
            val response = api.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )
            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (candidateText.isNullOrBlank()) {
                LlmResult.Error(
                    errorType = LlmErrorType.SERVER_ERROR,
                    message = "Received empty response from Gemini API."
                )
            } else {
                LlmResult.Success(
                    text = candidateText,
                    promptTokens = response.usageMetadata?.promptTokenCount ?: 0,
                    completionTokens = response.usageMetadata?.candidatesTokenCount ?: 0
                )
            }
        } catch (e: HttpException) {
            val errorType = when (e.code()) {
                400 -> LlmErrorType.INVALID_REQUEST
                401, 403 -> LlmErrorType.AUTHENTICATION_FAILED
                429 -> LlmErrorType.RATE_LIMITED
                in 500..599 -> LlmErrorType.SERVER_ERROR
                else -> LlmErrorType.UNKNOWN
            }
            LlmResult.Error(
                errorType = errorType,
                message = "HTTP ${e.code()}: ${e.message()}",
                cause = e
            )
        } catch (e: SocketTimeoutException) {
            LlmResult.Error(
                errorType = LlmErrorType.TIMEOUT,
                message = "Request timed out after waiting for Gemini response.",
                cause = e
            )
        } catch (e: IOException) {
            LlmResult.Error(
                errorType = LlmErrorType.NETWORK_UNAVAILABLE,
                message = "Network error: unable to reach Gemini server. Please check internet connection.",
                cause = e
            )
        } catch (e: Exception) {
            LlmResult.Error(
                errorType = LlmErrorType.UNKNOWN,
                message = e.localizedMessage ?: "Unknown error occurred during LLM call.",
                cause = e
            )
        }
    }
}
