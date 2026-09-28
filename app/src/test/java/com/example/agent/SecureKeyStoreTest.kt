package com.example.agent

import androidx.test.core.app.ApplicationProvider
import com.example.agent.util.SecureKeyStoreManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SecureKeyStoreTest {

    private lateinit var keyStoreManager: SecureKeyStoreManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        keyStoreManager = SecureKeyStoreManager(context)
        keyStoreManager.clearAllSecrets()
    }

    @Test
    fun testSaveAndRetrieveSecret() {
        val apiKey = "AIzaSyTestApiKey1234567890abcdefghij"
        keyStoreManager.saveSecret("gemini_key", apiKey)

        assertTrue(keyStoreManager.contains("gemini_key"))
        val retrieved = keyStoreManager.getSecret("gemini_key")
        assertEquals(apiKey, retrieved)
    }

    @Test
    fun testRemoveSecret() {
        keyStoreManager.saveSecret("temp_secret", "secret_value_123")
        assertTrue(keyStoreManager.contains("temp_secret"))

        keyStoreManager.removeSecret("temp_secret")
        assertFalse(keyStoreManager.contains("temp_secret"))
        assertEquals("", keyStoreManager.getSecret("temp_secret", ""))
    }

    @Test
    fun testBlankSecretRemoval() {
        keyStoreManager.saveSecret("blank_target", "some_secret")
        assertTrue(keyStoreManager.contains("blank_target"))

        keyStoreManager.saveSecret("blank_target", "")
        assertFalse(keyStoreManager.contains("blank_target"))
    }
}
