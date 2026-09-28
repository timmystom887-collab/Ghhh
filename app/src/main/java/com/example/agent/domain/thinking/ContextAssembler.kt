package com.example.agent.domain.thinking

import com.example.agent.data.local.entity.KnowledgeEntity
import com.example.agent.data.local.entity.MemoryEntity

data class AssembledContext(
    val relevantMemories: List<MemoryEntity>,
    val relevantKnowledge: List<KnowledgeEntity>,
    val formattedContextString: String,
    val tokenBudgetUsed: Int
)

class ContextAssembler(
    private val maxTokenBudget: Int = 1500
) {

    /**
     * Assembles optimal context for LLM reasoning by scoring semantic keyword overlap,
     * chronological decay, and fitting items within a token budget.
     */
    fun assemble(
        query: String,
        allMemories: List<MemoryEntity>,
        allKnowledge: List<KnowledgeEntity>
    ): AssembledContext {
        val queryKeywords = query.lowercase()
            .split(Regex("[\\s,?.!]+"))
            .filter { it.length > 2 && !STOP_WORDS.contains(it) }

        // 1. Score Memories
        val scoredMemories = allMemories.map { memory ->
            var score = 0.0f
            val lowerContent = memory.content.lowercase()
            queryKeywords.forEach { kw ->
                if (lowerContent.contains(kw)) score += 1.0f
            }
            // Chronological recency bonus (last 24 hours)
            val ageMs = System.currentTimeMillis() - memory.timestamp
            val ageHours = ageMs / (1000.0 * 60 * 60)
            if (ageHours < 24) score += 0.5f

            memory to score
        }.sortedByDescending { it.second }
            .filter { it.second > 0.0f }
            .map { it.first }

        // 2. Score Knowledge Items
        val scoredKnowledge = allKnowledge.map { item ->
            var score = 0.0f
            val lowerText = (item.title + " " + item.content + " " + item.tags).lowercase()
            queryKeywords.forEach { kw ->
                if (lowerText.contains(kw)) score += 1.5f
            }
            if (item.isPinned) score += 1.0f

            item to score
        }.sortedByDescending { it.second }
            .filter { it.second > 0.0f }
            .map { it.first }

        // 3. Assemble and pack within token budget
        val selectedMemories = mutableListOf<MemoryEntity>()
        val selectedKnowledge = mutableListOf<KnowledgeEntity>()
        var estimatedTokens = 0

        for (k in scoredKnowledge) {
            val cost = (k.title.length + k.content.length) / 4
            if (estimatedTokens + cost <= maxTokenBudget * 0.7) {
                selectedKnowledge.add(k)
                estimatedTokens += cost
            }
        }

        for (m in scoredMemories) {
            val cost = m.content.length / 4
            if (estimatedTokens + cost <= maxTokenBudget) {
                selectedMemories.add(m)
                estimatedTokens += cost
            }
        }

        val builder = StringBuilder()
        if (selectedKnowledge.isNotEmpty()) {
            builder.append("\n[Relevant Matrix Knowledge Vault]:\n")
            selectedKnowledge.forEach {
                builder.append("• ${it.title}: ${it.content.take(200)}\n")
            }
        }
        if (selectedMemories.isNotEmpty()) {
            builder.append("\n[User Neural Memory Records]:\n")
            selectedMemories.forEach {
                builder.append("• [${it.category}] ${it.content}\n")
            }
        }

        return AssembledContext(
            relevantMemories = selectedMemories,
            relevantKnowledge = selectedKnowledge,
            formattedContextString = builder.toString(),
            tokenBudgetUsed = estimatedTokens
        )
    }

    companion object {
        private val STOP_WORDS = setOf(
            "the", "and", "a", "an", "is", "in", "it", "to", "for", "with", "on", "at", "by", "this", "that", "from"
        )
    }
}
