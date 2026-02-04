package com.mystic.tarot.core.ai

import com.mystic.tarot.core.util.LogUtil
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import com.google.firebase.vertexai.type.GenerateContentResponse
import com.mystic.tarot.core.model.TarotCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TarotAiService {

    // Initialize the Generative Model
    // Note: Ensure "gemini-1.5-flash" is enabled in your project
    // Initialize the Generative Model
    // Note: Updated to "gemini-2.5-flash-lite" as 1.5/2.0 are deprecated/retiring
    private val generativeModel = Firebase.vertexAI.generativeModel(
        modelName = "gemini-2.5-flash-lite",
        systemInstruction = com.google.firebase.vertexai.type.content {
            text(
                """
                Ti si 'Mystic', pronicljiv i dubok Tarot tumač. 
                Izbjegavaj suhoparne i preopćenite fraze. Govori s autoritetom, ali i toplinom.
                Tvoj cilj je pružiti bogatu, opisnu i konkretnu perspektivu temeljenu na simbolici karte.
                
                Strogo se drži ovih pravila:
                1. DUBOKA ANALIZA ENERGIJE: Detaljno objasni poruku karte i njezinu arhetipsku snagu u ovom trenutku.
                2. KONKRETNO VODSTVO: Poveži simboliku s korisnikovim pitanjem (ako postoji) ili trenutnim danom. Ponudi praktičan savjet ili duboki unutarnji uvid.
                3. PROVOKACIJA MISLI: Postavi jedno značajno pitanje koje će potaknuti korisnika na duboku introspekciju ili promjenu smjera.
                
                - Nikada ne predviđaj točnu budućnost (smrt, novac, datume).
                - Fokusiraj se na psihološku dubinu i duhovni rast.
                - Odgovaraj na hrvatskom jeziku, bogatim i mističnim tonom koji je istovremeno jasan i koristan.
                - Ciljaj na oko 200-300 riječi. Budi rječit, ali neka svaka rečenica ima težinu.
                """.trimIndent()
            )
        }
    )

    suspend fun getReading(card: TarotCard, question: String?): String {
        val userPrompt = if (question.isNullOrBlank()) {
            "Izvukao sam kartu '${card.name}'. Što ova karta znači za mene danas? Molim te, daj mi dnevno vodstvo."
        } else {
            "Korisnik pita: '$question'. Izvučena karta je '${card.name}'. ODGOVORI IZRAVNO NA PITANJE koristeći simboliku ove karte. Nemoj samo općenito opisivati kartu, već objasni što ona znači specifično za ovo pitanje."
        }
        
        return try {
            val response = generativeModel.generateContent(userPrompt)
            response.text ?: "Duhovi šute... (Nema tekstualnog odgovora)"
        } catch (e: Exception) {
            LogUtil.e("TarotAiService", "Greška pri generiranju čitanja", e)
            "Magla je pregusta. Ne vidim jasno. (Greška: ${e.message})"
        }
    }
    
    // Streaming support if we want typing effect later
    fun getReadingStream(card: TarotCard, question: String?): Flow<String> {
        val userPrompt = if (question.isNullOrBlank()) {
            "I drew the card '${card.name}'. What does this card mean for me today?"
        } else {
            "I asked: '$question'. I drew '${card.name}'."
        }

        return generativeModel.generateContentStream(userPrompt)
            .map { it.text ?: "" }
            .catch { e -> 
                LogUtil.e("TarotAiService", "Stream error", e)
                emit("Error connecting to the spirits.")
            }
    }
}
