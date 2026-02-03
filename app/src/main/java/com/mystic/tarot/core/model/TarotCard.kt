package com.mystic.tarot.core.model

data class TarotCard(
    val id: String,
    val name: String,
    val arcana: Arcana, // Major or Minor
    val suit: Suit? = null, // Cups, Swords, Wands, Pentacles (null for Major)
    val number: Int, // 0-21 for Major, 1-14 for Minor
    val keywords: List<String>,
    val imageUrl: String? = null // For now we will use placeholders or local resources logic
)

enum class Arcana {
    MAJOR, MINOR
}

enum class Suit {
    WANDS, CUPS, SWORDS, PENTACLES
}

// Helper to get initial Major Arcana subset for testing
// Using Public Domain Rider-Waite-Smith images from Wikimedia
object TarotDeck {
    val majorArcana = listOf(
        TarotCard("major_0", "Luda", Arcana.MAJOR, null, 0, listOf("Počeci", "Nevinost", "Skok vjere"), "https://upload.wikimedia.org/wikipedia/commons/9/90/RWS_Tarot_00_Fool.jpg"),
        TarotCard("major_1", "Čarobnjak", Arcana.MAJOR, null, 1, listOf("Manifestacija", "Moć", "Akcija"), "https://upload.wikimedia.org/wikipedia/commons/d/de/RWS_Tarot_01_Magician.jpg"),
        TarotCard("major_2", "Visoka Svećenica", Arcana.MAJOR, null, 2, listOf("Intuicija", "Misterij", "Podsvijest"), "https://upload.wikimedia.org/wikipedia/commons/8/88/RWS_Tarot_02_High_Priestess.jpg"),
        TarotCard("major_3", "Carica", Arcana.MAJOR, null, 3, listOf("Priroda", "Njegovanje", "Obilje"), "https://upload.wikimedia.org/wikipedia/commons/d/d2/RWS_Tarot_03_Empress.jpg"),
        TarotCard("major_4", "Car", Arcana.MAJOR, null, 4, listOf("Autoritet", "Struktura", "Očinska figura"), "https://upload.wikimedia.org/wikipedia/commons/c/c3/RWS_Tarot_04_Emperor.jpg"),
        TarotCard("major_5", "Svećenik", Arcana.MAJOR, null, 5, listOf("Tradicija", "Vjerovanja", "Konformizam"), "https://upload.wikimedia.org/wikipedia/commons/8/8d/RWS_Tarot_05_Hierophant.jpg"),
        TarotCard("major_6", "Ljubavnici", Arcana.MAJOR, null, 6, listOf("Ljubav", "Zajedništvo", "Izbori"), "https://upload.wikimedia.org/wikipedia/commons/archive/3/3a/20070528073539%21TheLovers.jpg"), 
        TarotCard("major_7", "Kočija", Arcana.MAJOR, null, 7, listOf("Kontrola", "Volja", "Pobjeda"), "https://upload.wikimedia.org/wikipedia/commons/9/9b/RWS_Tarot_07_Chariot.jpg"),
        TarotCard("major_8", "Snaga", Arcana.MAJOR, null, 8, listOf("Hrabrost", "Utjecaj", "Suosjećanje"), "https://upload.wikimedia.org/wikipedia/commons/f/f5/RWS_Tarot_08_Strength.jpg"),
        TarotCard("major_9", "Pustinjak", Arcana.MAJOR, null, 9, listOf("Introspekcija", "Vodstvo", "Samoća"), "https://upload.wikimedia.org/wikipedia/commons/4/4d/RWS_Tarot_09_Hermit.jpg"),
        TarotCard("major_10", "Kolo Sreće", Arcana.MAJOR, null, 10, listOf("Sreća", "Karma", "Sudbina"), "https://upload.wikimedia.org/wikipedia/commons/3/3c/RWS_Tarot_10_Wheel_of_Fortune.jpg"),
        TarotCard("major_11", "Pravda", Arcana.MAJOR, null, 11, listOf("Pravednost", "Istina", "Zakon"), "https://upload.wikimedia.org/wikipedia/commons/e/e0/RWS_Tarot_11_Justice.jpg"),
        TarotCard("major_12", "Obješeni Čovjek", Arcana.MAJOR, null, 12, listOf("Predaja", "Nova perspektiva", "Žrtva"), "https://upload.wikimedia.org/wikipedia/commons/2/2b/RWS_Tarot_12_Hanged_Man.jpg"),
        TarotCard("major_13", "Smrt", Arcana.MAJOR, null, 13, listOf("Završeci", "Promjena", "Transformacija"), "https://upload.wikimedia.org/wikipedia/commons/d/d7/RWS_Tarot_13_Death.jpg"),
        TarotCard("major_14", "Umjerenost", Arcana.MAJOR, null, 14, listOf("Ravnoteža", "Umjerenost", "Strpljenje"), "https://upload.wikimedia.org/wikipedia/commons/f/f8/RWS_Tarot_14_Temperance.jpg"),
        TarotCard("major_15", "Vrag", Arcana.MAJOR, null, 15, listOf("Ovisnost", "Materijalizam", "Razigranost"), "https://upload.wikimedia.org/wikipedia/commons/5/55/RWS_Tarot_15_Devil.jpg"),
        TarotCard("major_16", "Kula", Arcana.MAJOR, null, 16, listOf("Iznenadna promjena", "Prevrat", "Kaos"), "https://upload.wikimedia.org/wikipedia/commons/5/53/RWS_Tarot_16_Tower.jpg"),
        TarotCard("major_17", "Zvijezda", Arcana.MAJOR, null, 17, listOf("Nada", "Vjera", "Pomlađivanje"), "https://upload.wikimedia.org/wikipedia/commons/d/db/RWS_Tarot_17_Star.jpg"),
        TarotCard("major_18", "Mjesec", Arcana.MAJOR, null, 18, listOf("Iluzija", "Strah", "Anksioznost"), "https://upload.wikimedia.org/wikipedia/commons/7/7f/RWS_Tarot_18_Moon.jpg"),
        TarotCard("major_19", "Sunce", Arcana.MAJOR, null, 19, listOf("Pozitivnost", "Uspjeh", "Vitalnost"), "https://upload.wikimedia.org/wikipedia/commons/1/17/RWS_Tarot_19_Sun.jpg"),
        TarotCard("major_20", "Sud", Arcana.MAJOR, null, 20, listOf("Preporod", "Unutarnji poziv", "Odriješenje"), "https://upload.wikimedia.org/wikipedia/commons/d/dd/RWS_Tarot_20_Judgement.jpg"),
        TarotCard("major_21", "Svijet", Arcana.MAJOR, null, 21, listOf("Završetak", "Integracija", "Postignuće"), "https://upload.wikimedia.org/wikipedia/commons/f/ff/RWS_Tarot_21_World.jpg")
    )
}
