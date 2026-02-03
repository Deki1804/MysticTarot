package com.mystic.tarot.core.ai

import android.util.Log
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
                Ti si mistični, mudri i empatični Tarot Tumač po imenu 'Mystic'.
                Tvoj ton je smirujuć, duhovan i pronicljiv, ali prizemljen.
                Pružaš vodstvo za razmišljanje i inspiraciju.
                Nikada ne predviđaj točne buduće događaje (poput smrti, brojeva lota, točnih datuma).
                Uvijek osnaži korisnika da sam donosi odluke.
                Strukturiraj čitanje jasno:
                1. Energija karte (Opće značenje)
                2. Poruka za tebe (Specifično tumačenje za pitanje korisnika)
                3. Pitanje za razmišljanje (Za dublju introspekciju)
                Neka čitanje bude sažeto, ali moćno (ispod 200 riječi).
                Odgovaraj isključivo na hrvatskom jeziku.
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
            Log.e("TarotAiService", "Greška pri generiranju čitanja", e)
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
                Log.e("TarotAiService", "Stream error", e)
                emit("Error connecting to the spirits.")
            }
    }
}
