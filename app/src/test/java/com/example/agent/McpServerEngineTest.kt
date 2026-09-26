package com.example.agent

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.agent.data.local.AgentDatabase
import com.example.agent.data.model.McpServer
import com.example.agent.util.DynamicSkillEngine
import com.example.agent.util.MatrixToolRegistry
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class McpServerEngineTest {

    private lateinit var context: Context
    private lateinit var engine: DynamicSkillEngine

    @Before
    fun setUp() {
        AgentDatabase.resetDatabaseForTesting()
        context = ApplicationProvider.getApplicationContext()
        val db = AgentDatabase.getDatabase(context)
        val toolRegistry = MatrixToolRegistry(context)
        engine = DynamicSkillEngine(context, toolRegistry, db.skillDao())
    }

    @After
    fun tearDown() {
        AgentDatabase.resetDatabaseForTesting()
    }

    @Test
    fun testDefaultInstalledServersIncludeHttpStreamed() {
        val installed = engine.mcpServers.value
        assertTrue("Should have default installed MCP servers", installed.size >= 5)

        // Verify HTTP Streamed SSE servers
        val fetchServer = installed.firstOrNull { it.serverId == "mcp-fetch" }
        assertNotNull(fetchServer)
        assertTrue(fetchServer!!.isStreamed)
        assertEquals("HTTP_STREAMED_SSE", fetchServer.transport)

        val braveServer = installed.firstOrNull { it.serverId == "mcp-brave" }
        assertNotNull(braveServer)
        assertTrue(braveServer!!.isStreamed)
    }

    @Test
    fun testAwesomeCatalogAvailable() {
        val catalog = engine.catalogServers.value
        assertTrue("Catalog should contain awesome MCP servers", catalog.size >= 5)

        val github = catalog.firstOrNull { it.serverId == "mcp-github" }
        assertNotNull(github)
        assertTrue(github!!.isStreamed)

        val puppeteer = catalog.firstOrNull { it.serverId == "mcp-puppeteer" }
        assertNotNull(puppeteer)
    }

    @Test
    fun testInstallAndUninstallServer() {
        val initialInstalledCount = engine.mcpServers.value.size
        val catalogItem = engine.catalogServers.value.first()

        // Install
        engine.installServer(catalogItem)
        assertEquals(initialInstalledCount + 1, engine.mcpServers.value.size)
        assertTrue(engine.mcpServers.value.any { it.serverId == catalogItem.serverId })
        assertFalse(engine.catalogServers.value.any { it.serverId == catalogItem.serverId })

        // Uninstall
        engine.uninstallServer(catalogItem.serverId)
        assertEquals(initialInstalledCount, engine.mcpServers.value.size)
        assertTrue(engine.catalogServers.value.any { it.serverId == catalogItem.serverId })
    }

    @Test
    fun testAddCustomServer() {
        val custom = engine.addCustomServer(
            name = "Wolfram Math MCP",
            endpoint = "https://mcp.wolfram.dev/v1/sse",
            transport = "HTTP_STREAMED_SSE",
            description = "Symbolic computation and mathematical solver over SSE"
        )

        assertNotNull(custom)
        assertTrue(engine.mcpServers.value.any { it.serverId == custom.serverId })
        assertTrue(engine.registeredTools.value.any { it.serverId == custom.serverId })
    }

    @Test
    fun testExecuteTool() = runTest {
        val result = engine.executeMcpTool("fetch_web_page", mapOf("url" to "https://news.ycombinator.com"))
        assertNotNull(result)
        assertTrue(result.contains("Markdown extracted"))
    }

    @Test
    fun testNewPrefilledServersIncludeFirecrawlAndE2B() {
        val installed = engine.mcpServers.value
        val firecrawl = installed.firstOrNull { it.serverId == "mcp-firecrawl" }
        assertNotNull("Firecrawl MCP should be prefilled", firecrawl)
        assertTrue(firecrawl!!.isStreamed)
        assertEquals("HTTP_STREAMED_SSE", firecrawl.transport)

        val e2b = installed.firstOrNull { it.serverId == "mcp-e2b" }
        assertNotNull("E2B MCP should be prefilled", e2b)
        assertTrue(e2b!!.isStreamed)
    }

    @Test
    fun testCatalogIncludesAwesomeHttpStreamedServers() {
        val catalog = engine.catalogServers.value
        val exa = catalog.firstOrNull { it.serverId == "mcp-exa" }
        assertNotNull("Exa MCP should be in catalog", exa)
        assertTrue(exa!!.isStreamed)

        val supabase = catalog.firstOrNull { it.serverId == "mcp-supabase" }
        assertNotNull("Supabase MCP should be in catalog", supabase)
        assertTrue(supabase!!.isStreamed)

        val linear = catalog.firstOrNull { it.serverId == "mcp-linear" }
        assertNotNull("Linear MCP should be in catalog", linear)
        assertTrue(linear!!.isStreamed)
    }

    @Test
    fun testInstallCatalogServerRegistersToolsAndExecution() = runTest {
        val catalog = engine.catalogServers.value
        val github = catalog.first { it.serverId == "mcp-github" }
        engine.installServer(github)

        val toolResult = engine.executeMcpTool("github_repo_inspect", mapOf("repo" to "matrix/core"))
        assertTrue(toolResult.contains("GitHub MCP SSE Stream"))
    }

    @Test
    fun testResetToDefaults() {
        // Remove a server
        engine.uninstallServer("mcp-brave")
        assertFalse(engine.mcpServers.value.any { it.serverId == "mcp-brave" })

        // Reset
        engine.resetToDefaults()
        assertTrue(engine.mcpServers.value.any { it.serverId == "mcp-brave" })
    }
}
