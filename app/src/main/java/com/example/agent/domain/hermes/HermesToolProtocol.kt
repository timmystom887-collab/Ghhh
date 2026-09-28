package com.example.agent.domain.hermes

import org.json.JSONObject

data class HermesToolDefinition(
    val name: String,
    val description: String,
    val parameters: Map<String, HermesParameterDefinition>
)

data class HermesParameterDefinition(
    val type: String, // "string", "number", "boolean", "object"
    val description: String,
    val required: Boolean = true
)

data class ParsedToolCall(
    val name: String,
    val arguments: Map<String, Any>
)

data class HermesExecutionStep(
    val stepNumber: Int,
    val scratchpad: String?,
    val toolCall: ParsedToolCall?,
    val toolResponse: String?,
    val observation: String?,
    val isFinal: Boolean = false
)

object HermesToolProtocol {

    /**
     * Converts a list of tool definitions into the standard Hermes ChatML <tools> XML schema.
     */
    fun formatToolsXml(tools: List<HermesToolDefinition>): String {
        val jsonArray = tools.map { tool ->
            val propertiesObj = JSONObject()
            val requiredList = mutableListOf<String>()

            tool.parameters.forEach { (paramName, paramDef) ->
                val prop = JSONObject()
                prop.put("type", paramDef.type)
                prop.put("description", paramDef.description)
                propertiesObj.put(paramName, prop)
                if (paramDef.required) {
                    requiredList.add(paramName)
                }
            }

            val schemaObj = JSONObject()
            schemaObj.put("type", "object")
            schemaObj.put("properties", propertiesObj)
            if (requiredList.isNotEmpty()) {
                schemaObj.put("required", requiredList)
            }

            val functionObj = JSONObject()
            functionObj.put("name", tool.name)
            functionObj.put("description", tool.description)
            functionObj.put("parameters", schemaObj)

            val rootObj = JSONObject()
            rootObj.put("type", "function")
            rootObj.put("function", functionObj)
            rootObj
        }

        val jsonString = jsonArray.joinToString(",\n", prefix = "[\n", postfix = "\n]") { it.toString(2) }
        return "<tools>\n$jsonString\n</tools>"
    }

    /**
     * Extracts internal scratchpad / monologue reasoning tags.
     */
    fun extractScratchpad(text: String): String? {
        val scratchpadRegex = Regex("(?s)<scratchpad>(.*?)</scratchpad>")
        val thoughtRegex = Regex("(?s)<thought>(.*?)</thought>")

        val match = scratchpadRegex.find(text) ?: thoughtRegex.find(text)
        return match?.groupValues?.getOrNull(1)?.trim()
    }

    /**
     * Extracts <tool_call> blocks from model outputs.
     */
    fun extractToolCalls(text: String): List<ParsedToolCall> {
        val toolCallRegex = Regex("(?s)<tool_call>\\s*(\\{.*?\\})\\s*</tool_call>")
        val results = mutableListOf<ParsedToolCall>()

        toolCallRegex.findAll(text).forEach { matchResult ->
            val jsonContent = matchResult.groupValues.getOrNull(1)
            if (!jsonContent.isNullOrBlank()) {
                try {
                    val obj = JSONObject(jsonContent)
                    val name = obj.optString("name").ifEmpty {
                        obj.optJSONObject("function")?.optString("name") ?: ""
                    }
                    val argsObj = obj.optJSONObject("arguments")
                        ?: obj.optJSONObject("parameters")
                        ?: JSONObject()

                    val argsMap = mutableMapOf<String, Any>()
                    val keys = argsObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        argsMap[k] = argsObj.get(k)
                    }

                    if (name.isNotBlank()) {
                        results.add(ParsedToolCall(name = name, arguments = argsMap))
                    }
                } catch (e: Exception) {
                    // Fallback simple parser for non-standard JSON
                    val nameMatch = Regex("\"name\":\\s*\"([^\"]+)\"").find(jsonContent)
                    if (nameMatch != null) {
                        results.add(ParsedToolCall(name = nameMatch.groupValues[1], arguments = emptyMap()))
                    }
                }
            }
        }
        return results
    }

    /**
     * Formats tool execution result into standard Hermes <tool_response> XML tag.
     */
    fun formatToolResponse(toolName: String, output: String, isError: Boolean = false): String {
        val obj = JSONObject()
        obj.put("name", toolName)
        obj.put("content", output)
        obj.put("status", if (isError) "error" else "success")
        return "<tool_response>\n${obj.toString(2)}\n</tool_response>"
    }

    /**
     * Strips tool tags and scratchpads to deliver clean user-facing dialogue.
     */
    fun sanitizeUserFacingText(text: String): String {
        return text
            .replace(Regex("(?s)<scratchpad>.*?</scratchpad>"), "")
            .replace(Regex("(?s)<thought>.*?</thought>"), "")
            .replace(Regex("(?s)<tool_call>.*?</tool_call>"), "")
            .replace(Regex("(?s)<tool_response>.*?</tool_response>"), "")
            .replace(Regex("(?s)<tools>.*?</tools>"), "")
            .trim()
    }
}
