package cl.checkhelph.app.domain

import cl.checkhelph.app.data.Moment
import cl.checkhelph.app.data.Question
import cl.checkhelph.app.data.QuestionCategory
import java.text.Normalizer

/** Búsqueda por palabras clave: ignora mayúsculas, tildes y palabras vacías, y prioriza las preguntas más usadas. */
object QuestionSearch {
    private val STOP_WORDS = setOf(
        "como", "que", "donde", "cual", "cuando", "para", "con", "del", "los", "las", "una", "uno",
        "por", "hago", "debo", "tengo", "puedo", "hay", "esta", "este", "son", "mis", "sus",
    )
    private val SPLIT = Regex("[^a-z0-9]+")
    private val MARKS = Regex("\\p{Mn}+")

    fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD).replace(MARKS, "")

    fun tokens(text: String): List<String> =
        normalize(text).split(SPLIT).filter { it.length >= 3 && it !in STOP_WORDS }

    private fun matches(queryToken: String, word: String): Boolean =
        word == queryToken ||
            word.startsWith(queryToken) ||
            (queryToken.length >= 5 && queryToken.startsWith(word.take(5)) && word.length >= 5)

    private fun score(query: List<String>, q: Question): Int {
        val titleWords = tokens(q.question)
        val answerWords = tokens(q.answer)
        return query.sumOf { t ->
            when {
                titleWords.any { matches(t, it) } -> 3
                answerWords.any { matches(t, it) } -> 1
                else -> 0
            }
        }
    }

    fun search(query: String, questions: List<Question>): List<Question> {
        val q = tokens(query)
        if (q.isEmpty()) return emptyList()
        return questions
            .map { it to score(q, it) }
            .filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<Question, Int>> { it.second }.thenByDescending { it.first.uses })
            .map { it.first }
    }

    /** Autocompletado mientras se escribe: hasta [limit] coincidencias. */
    fun suggestions(query: String, questions: List<Question>, limit: Int = 5) = search(query, questions).take(limit)

    /** Sugerencias del momento del turno, de las más usadas a las menos. */
    fun forMoment(moment: Moment?, questions: List<Question>, limit: Int = 4): List<Question> =
        questions
            .filter { it.category == QuestionCategory.TURNO && (moment == null || it.moment == moment) }
            .sortedByDescending { it.uses }
            .take(limit)
}
