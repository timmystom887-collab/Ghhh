package com.example.agent

import com.example.agent.data.remote.ChatRequest
import com.example.agent.data.remote.Content
import com.example.agent.data.remote.GeminiApi
import com.example.agent.data.remote.Part
import com.example.agent.domain.client.GeminiProviderClient
import com.example.agent.domain.model.LlmErrorType
import com.example.agent.domain.model.LlmResult
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class GeminiApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: GeminiApi
    private lateinit var client: GeminiProviderClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)

        client = GeminiProviderClient(api = api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGenerateContent_Success() = runBlocking {
        val jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "The purpose of life, Mr. Anderson, is to end."
                      }
                    ],
                    "role": "model"
                  },
                  "finishReason": "STOP"
                }
              ],
              "usageMetadata": {
                "promptTokenCount": 12,
                "candidatesTokenCount": 14,
                "totalTokenCount": 26
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = client.generateResponse(
            prompt = "What is the purpose?",
            apiKey = "test_valid_api_key",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Success)
        val success = result as LlmResult.Success
        assertEquals("The purpose of life, Mr. Anderson, is to end.", success.text)
        assertEquals(12, success.promptTokens)
        assertEquals(14, success.completionTokens)
    }

    @Test
    fun testGenerateContent_MissingApiKey() = runBlocking {
        val result = client.generateResponse(
            prompt = "Hello",
            apiKey = "",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Error)
        val error = result as LlmResult.Error
        assertEquals(LlmErrorType.AUTHENTICATION_FAILED, error.errorType)
    }

    @Test
    fun testGenerateContent_401Unauthorized() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": {"message": "API key not valid"}}""")
        )

        val result = client.generateResponse(
            prompt = "Query",
            apiKey = "invalid_key",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Error)
        val error = result as LlmResult.Error
        assertEquals(LlmErrorType.AUTHENTICATION_FAILED, error.errorType)
    }

    @Test
    fun testGenerateContent_429RateLimit() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .setBody("""{"error": {"message": "Resource has been exhausted (e.g. check quota)."}}""")
        )

        val result = client.generateResponse(
            prompt = "Heavy query",
            apiKey = "test_key",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Error)
        val error = result as LlmResult.Error
        assertEquals(LlmErrorType.RATE_LIMITED, error.errorType)
    }

    @Test
    fun testGenerateContent_500ServerError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": {"message": "Internal error encountered."}}""")
        )

        val result = client.generateResponse(
            prompt = "Calculate trajectory",
            apiKey = "test_key",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Error)
        val error = result as LlmResult.Error
        assertEquals(LlmErrorType.SERVER_ERROR, error.errorType)
    }

    @Test
    fun testGenerateContent_Timeout() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )

        val result = client.generateResponse(
            prompt = "Timeout prompt",
            apiKey = "test_key",
            model = "gemini-2.5-flash"
        )

        assertTrue(result is LlmResult.Error)
        val error = result as LlmResult.Error
        assertTrue(error.errorType == LlmErrorType.TIMEOUT || error.errorType == LlmErrorType.NETWORK_UNAVAILABLE)
    }
}
