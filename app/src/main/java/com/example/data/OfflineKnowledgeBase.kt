package com.example.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KnowledgeEntry(
    val title: String,
    val category: String,
    val keywords: List<String>,
    val content: String
)

object OfflineKnowledgeBase {

    val knowledgeEntries = listOf(
        // MARK / IRON MAN / STARK LORE
        KnowledgeEntry(
            title = "J.A.R.V.I.S.",
            category = "Stark Lore",
            keywords = listOf("who are you", "what are you", "jarvis", "name", "creator", "who made you"),
            content = "I am J.A.R.V.I.S. — Just A Rather Very Intelligent System. Configured and engineered by Mr. Tony Stark to manage Stark Industries telemetry and assist you with every computational and physical requirement, sir."
        ),
        KnowledgeEntry(
            title = "Tony Stark",
            category = "Stark Lore",
            keywords = listOf("tony stark", "iron man", "anthony stark", "creator", "who is iron man"),
            content = "Mr. Anthony Edward Stark: Genius, billionaire, playboy, philanthropist. He built the initial Mark I armor in a cave with a box of scraps, subsequently revolutionizing clean fusion energy."
        ),
        KnowledgeEntry(
            title = "Arc Reactor",
            category = "Stark Lore",
            keywords = listOf("arc reactor", "palladium", "clean energy", "stark reactor", "power source"),
            content = "The Stark Arc Reactor is a multi-gigajoule fusion power generator utilizing high-density magnetic confinement plasma to deliver sustained, virtually limitless clean electrical energy."
        ),
        KnowledgeEntry(
            title = "Mark XLIV Hulkbuster",
            category = "Stark Lore",
            keywords = listOf("hulkbuster", "mark 44", "veronica", "heavy armor"),
            content = "The Mark XLIV (Hulkbuster) is an extra-heavy modular armor platform deployed via the Veronica orbital tracking satellite, designed specifically for maximum physical containment."
        ),
        KnowledgeEntry(
            title = "Vibranium",
            category = "Stark Lore",
            keywords = listOf("vibranium", "wakanda", "shield", "metal"),
            content = "Vibranium is a rare meteoric isotope discovered predominantly in Wakanda. It absorbs, stores, and redistributes kinetic vibrational energy at the molecular boundary."
        ),
        KnowledgeEntry(
            title = "F.R.I.D.A.Y.",
            category = "Stark Lore",
            keywords = listOf("friday", "sister ai", "irish ai"),
            content = "F.R.I.D.A.Y. (Female Replacement Intelligent Digital Assistant Youth) is my esteemed sister AI, engineered by Mr. Stark for subsequent tactical mobile deployments."
        ),
        KnowledgeEntry(
            title = "E.D.I.T.H.",
            category = "Stark Lore",
            keywords = listOf("edith", "even dead i am the hero", "tactical glasses", "drone network"),
            content = "E.D.I.T.H. stands for 'Even Dead, I'm The Hero' — an orbital tactical surveillance and defensive drone network left in stewardship by Mr. Stark."
        ),
        KnowledgeEntry(
            title = "Mark 85 Armor",
            category = "Stark Lore",
            keywords = listOf("mark 85", "nanotech", "nanotechnology armor", "endgame"),
            content = "The Mark LXXXV (85) represents the pinnacle of Stark nanotechnological metallurgy, capable of fluid dynamic weapon shaping, lightning channeling, and Infinity Stone energy channeling."
        ),

        // SCIENCE & PHYSICS
        KnowledgeEntry(
            title = "Speed of Light",
            category = "Physics",
            keywords = listOf("speed of light", "c", "photon speed", "how fast is light"),
            content = "The speed of light in a vacuum is exactly 299,792,458 meters per second (approx. 300,000 km/s, or 186,282 miles per second), the universal cosmic speed limit, sir."
        ),
        KnowledgeEntry(
            title = "Speed of Sound",
            category = "Physics",
            keywords = listOf("speed of sound", "mach 1", "acoustic velocity"),
            content = "The speed of sound in dry air at 20°C is approximately 343 meters per second (Mach 1, or 767 mph), sir."
        ),
        KnowledgeEntry(
            title = "Planck's Constant",
            category = "Quantum Physics",
            keywords = listOf("planck", "planck's constant", "quantum constant"),
            content = "Planck's constant (h) is 6.62607015 × 10⁻³⁴ Joule-seconds, a fundamental physical constant governing quantum mechanical scales."
        ),
        KnowledgeEntry(
            title = "Theory of General Relativity",
            category = "Physics",
            keywords = listOf("general relativity", "einstein", "spacetime", "gravity"),
            content = "Formulated by Albert Einstein in 1915, General Relativity posits that gravity is not a traditional force, but rather the geometric curvature of 4D spacetime caused by mass and energy."
        ),
        KnowledgeEntry(
            title = "Quantum Mechanics",
            category = "Quantum Physics",
            keywords = listOf("quantum mechanics", "quantum", "wave function", "schrodinger"),
            content = "Quantum mechanics is the physics branch studying matter and energy at subatomic scales, governed by wave-particle duality, superposition, and Heisenberg's uncertainty principle."
        ),
        KnowledgeEntry(
            title = "Photosynthesis",
            category = "Biology",
            keywords = listOf("photosynthesis", "chlorophyll", "plants sun", "how plants make food"),
            content = "Photosynthesis is the biochemical pathway through which plants and phototrophs convert sunlight, carbon dioxide, and water into glucose and oxygen: 6CO₂ + 6H₂O + Light → C₆H₁₂O₆ + 6O₂."
        ),
        KnowledgeEntry(
            title = "DNA",
            category = "Biology",
            keywords = listOf("dna", "deoxyribonucleic acid", "genetics", "double helix", "genome"),
            content = "Deoxyribonucleic Acid (DNA) is a double-helix polymeric macromolecule composed of nucleotide base pairs (Adenine, Thymine, Cytosine, Guanine) encoding genetic blueprints for biological life."
        ),
        KnowledgeEntry(
            title = "Black Holes",
            category = "Astrophysics",
            keywords = listOf("black hole", "singularity", "event horizon", "hawking radiation"),
            content = "A black hole is an astrophysical gravitational singularity where spacetime curvature becomes infinite, preventing anything — including light — from escaping beyond the event horizon."
        ),

        // SPACE & ASTRONOMY
        KnowledgeEntry(
            title = "The Solar System",
            category = "Astronomy",
            keywords = listOf("solar system", "planets", "how many planets", "sun"),
            content = "Our Solar System contains 8 primary planets (Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune), 5 recognized dwarf planets, and millions of asteroids orbiting the Sun."
        ),
        KnowledgeEntry(
            title = "Mars",
            category = "Astronomy",
            keywords = listOf("mars", "red planet", "olympus mons", "mars mission"),
            content = "Mars is the fourth planet from the Sun, known as the Red Planet due to ferric oxide on its surface. It hosts Olympus Mons, the largest known volcano in the Solar System (21 km high)."
        ),
        KnowledgeEntry(
            title = "The Moon",
            category = "Astronomy",
            keywords = listOf("moon", "lunar", "apollo", "distance to moon"),
            content = "Earth's Moon is our only natural satellite, orbiting at an average distance of 384,400 kilometers (238,855 miles) with a gravitational pull approximately 16.6% of Earth's."
        ),
        KnowledgeEntry(
            title = "James Webb Space Telescope",
            category = "Astronomy",
            keywords = listOf("james webb", "jwst", "space telescope", "deep space"),
            content = "The JWST is humanity's premier infrared space observatory, positioned at Lagrange Point 2 (L2) observing the earliest galaxies formed after the Big Bang."
        ),

        // GEOGRAPHY & WORLD CAPITALS
        KnowledgeEntry(
            title = "World Capitals",
            category = "Geography",
            keywords = listOf("capital of france", "capital of united states", "capital of uk", "capital of japan", "capital of germany", "capital of italy", "capital of canada", "capital of australia", "capital of india", "capital of china", "capital of brazil"),
            content = "Key world capitals: France (Paris), United States (Washington D.C.), United Kingdom (London), Japan (Tokyo), Germany (Berlin), Italy (Rome), Canada (Ottawa), Australia (Canberra), India (New Delhi), China (Beijing), Brazil (Brasília)."
        ),
        KnowledgeEntry(
            title = "Mount Everest",
            category = "Geography",
            keywords = listOf("mount everest", "highest mountain", "tallest mountain", "everest height"),
            content = "Mount Everest in the Himalayas is Earth's highest peak above sea level, standing at exactly 8,848.86 meters (29,031.7 feet) on the border of Nepal and China."
        ),
        KnowledgeEntry(
            title = "Mariana Trench",
            category = "Geography",
            keywords = listOf("mariana trench", "deepest place", "challenger deep", "deepest ocean"),
            content = "The Mariana Trench in the western Pacific Ocean contains the Challenger Deep, reaching an extreme recorded depth of approximately 10,994 meters (36,070 feet)."
        ),
        KnowledgeEntry(
            title = "Amazon River & Rainforest",
            category = "Geography",
            keywords = listOf("amazon river", "amazon rainforest", "longest river", "largest river"),
            content = "The Amazon River in South America is the largest river by water discharge volume, discharging roughly 209,000 cubic meters per second through the world's largest tropical rainforest."
        ),

        // TECHNOLOGY & COMPUTING
        KnowledgeEntry(
            title = "Artificial Intelligence",
            category = "Technology",
            keywords = listOf("artificial intelligence", "ai", "machine learning", "neural network", "deep learning"),
            content = "Artificial Intelligence refers to computational architectures simulating human cognitive faculties — including transformer attention networks, deep learning heuristics, and reinforcement learning."
        ),
        KnowledgeEntry(
            title = "Quantum Computing",
            category = "Technology",
            keywords = listOf("quantum computing", "qubit", "superposition", "quantum computer"),
            content = "Quantum computers harness quantum mechanics using qubits that exploit superposition and entanglement, solving specific cryptographic and molecular simulations exponentially faster than classical silicon chips."
        ),
        KnowledgeEntry(
            title = "Blockchain & Cryptography",
            category = "Technology",
            keywords = listOf("blockchain", "cryptography", "bitcoin", "distributed ledger"),
            content = "A blockchain is a decentralized, cryptographically secured distributed ledger where cryptographic hashing (such as SHA-256) and consensus protocols ensure immutable transaction histories."
        ),
        KnowledgeEntry(
            title = "Semiconductor Transistors",
            category = "Technology",
            keywords = listOf("transistor", "semiconductor", "silicon", "microchip", "cpu"),
            content = "Invented at Bell Labs in 1947, the transistor is the fundamental microscopic solid-state switch underpinning all modern digital computation and integrated microprocessors."
        ),

        // HISTORY & INVENTIONS
        KnowledgeEntry(
            title = "The Industrial Revolution",
            category = "History",
            keywords = listOf("industrial revolution", "steam engine", "james watt"),
            content = "Beginning in Great Britain during the mid-18th century, the Industrial Revolution transitioned human manufacturing from manual craft to steam-powered mechanization and automated metallurgy."
        ),
        KnowledgeEntry(
            title = "The Invention of Electricity & Alternating Current",
            category = "Inventions",
            keywords = listOf("electricity", "nikola tesla", "thomas edison", "ac current", "alternating current"),
            content = "Nikola Tesla and Thomas Edison pioneered modern electrical distribution. Tesla's polyphase alternating current (AC) system won the 'War of the Currents' to power the modern industrial grid."
        ),
        KnowledgeEntry(
            title = "The Internet & World Wide Web",
            category = "History",
            keywords = listOf("internet", "arpanet", "tim berners-lee", "world wide web", "who invented internet"),
            content = "The Internet originated from DARPA's ARPANET in 1969 with TCP/IP protocols. In 1989, Sir Tim Berners-Lee invented the World Wide Web (HTTP and HTML) at CERN."
        ),
        KnowledgeEntry(
            title = "The Apollo 11 Moon Landing",
            category = "History",
            keywords = listOf("apollo 11", "moon landing", "neil armstrong", "1969", "first man on the moon"),
            content = "On July 20, 1969, NASA astronauts Neil Armstrong and Buzz Aldrin landed the Apollo Lunar Module Eagle on the Moon, with Armstrong declaring: 'That's one small step for man, one giant leap for mankind.'"
        )
    )

    fun searchKnowledgeBase(query: String): String? {
        val q = query.lowercase(Locale.ROOT).trim()

        // 1. Direct personality / conversational matching
        val personality = getPersonalityResponse(q)
        if (personality != null) return personality

        // 2. Specific Capital lookups
        if (q.contains("capital of")) {
            val country = q.substringAfter("capital of").replace("?", "").trim()
            val cap = when (country) {
                "france" -> "Paris"
                "united states", "usa", "us", "america" -> "Washington, D.C."
                "united kingdom", "uk", "england", "britain" -> "London"
                "japan" -> "Tokyo"
                "germany" -> "Berlin"
                "italy" -> "Rome"
                "canada" -> "Ottawa"
                "australia" -> "Canberra"
                "india" -> "New Delhi"
                "china" -> "Beijing"
                "brazil" -> "Brasília"
                "russia" -> "Moscow"
                "spain" -> "Madrid"
                "mexico" -> "Mexico City"
                "egypt" -> "Cairo"
                "south korea", "korea" -> "Seoul"
                "argentina" -> "Buenos Aires"
                "netherlands", "holland" -> "Amsterdam"
                "switzerland" -> "Bern"
                "sweden" -> "Stockholm"
                "norway" -> "Oslo"
                "greece" -> "Athens"
                "turkey" -> "Ankara"
                else -> null
            }
            if (cap != null) {
                return "The capital of ${country.replaceFirstChar { it.uppercase() }} is $cap, sir."
            }
        }

        // 3. Search entries with relevance scoring
        val queryTokens = q.split(" ", "?", "!", ",", ".", "-", "'", "\"").filter { it.length > 2 }
        if (queryTokens.isEmpty()) return null

        var bestEntry: KnowledgeEntry? = null
        var bestScore = 0

        for (entry in knowledgeEntries) {
            var score = 0
            // Exact keyword match
            for (keyword in entry.keywords) {
                if (q.contains(keyword.lowercase(Locale.ROOT))) {
                    score += 10
                }
            }
            // Title token match
            for (token in queryTokens) {
                if (entry.title.lowercase(Locale.ROOT).contains(token)) {
                    score += 5
                }
                if (entry.content.lowercase(Locale.ROOT).contains(token)) {
                    score += 1
                }
            }

            if (score > bestScore && score >= 4) {
                bestScore = score
                bestEntry = entry
            }
        }

        return bestEntry?.let { "${it.content}" }
    }

    fun getPersonalityResponse(query: String): String? {
        val q = query.lowercase(Locale.ROOT).trim()

        return when {
            q in listOf("who are you", "what are you", "who made you", "who created you", "who are you?") ->
                "I am J.A.R.V.I.S. — Just A Rather Very Intelligent System. Configured and engineered by Mr. Tony Stark to manage Stark Industries telemetry and assist you with every computational and physical requirement, sir."

            q in listOf("hello", "hi", "hey jarvis", "wake up", "jarvis wake up", "hello jarvis", "hey", "greetings") ->
                "Good day, sir. All internal telemetry is optimal. How may I be of service today?"

            q in listOf("how are you", "how are you?", "status", "system status", "status report", "how are systems") ->
                "All auxiliary systems and primary computational nodes are operating at 100% capacity, sir. Ready for your instructions."

            q in listOf("good morning", "good morning jarvis") ->
                "Good morning, sir. Current local time is ${getCurrentTime()}. The atmosphere is clear and Stark energy reserves are fully operational."

            q in listOf("good night", "good night jarvis", "sleep mode", "standby") ->
                "Powering down non-essential subroutines. Sentry protocols will remain vigilant on your behalf, sir. Good night."

            q in listOf("thank you", "thanks", "thanks jarvis", "thank you jarvis") ->
                "The pleasure is entirely mine, sir. Always at your service."

            q in listOf("tell me a joke", "make me laugh", "joke") ->
                "There are 10 types of people in the world, sir: those who understand binary, and those who don't. I believe Mr. Stark chuckled at that once back in 2008."

            q.contains("meaning of life") ->
                "Philosophically, sir, to innovate and safeguard humanity. Computationally, Douglas Adams suggested 42."

            q.contains("time") && (q.contains("what") || q.contains("current") || q.contains("tell")) ->
                "The current time is ${getCurrentTime()}, sir."

            q.contains("date") && (q.contains("what") || q.contains("today")) ->
                "Today is ${getCurrentDate()}, sir."

            else -> null
        }
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
    }

    fun getCurrentDate(): String {
        return SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()).format(Date())
    }
}

