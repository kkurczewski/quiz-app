package pl.kkurczewski.quizapp

object QuestionFactory {
    private const val QUESTION_TOKEN = "Q:"
    private const val CORRECT_ANSWERS_TOKEN = "A:"
    private const val CORRECT_ANSWERS_SEPARATOR = ","
    private const val ANSWER_TOKEN = "-"

    fun fromString(str: String): MutableList<Question> {
        return str.split(QUESTION_TOKEN).mapNotBlank { question ->
            val lines = question.split('\n').mapNotBlank { it.trimStart() }
            val title = lines[0]
            val correctAnswers = parseCorrectAnswers(lines[1])
            val answers = parseAnswers(lines.drop(2))
            Question(title, answers, correctAnswers)
        }.toMutableList()
    }

    private fun parseCorrectAnswers(correctAnswerLine: String) = correctAnswerLine
            .removePrefix(CORRECT_ANSWERS_TOKEN)
            .trimStart()
            .split(CORRECT_ANSWERS_SEPARATOR)
            .mapNotNull { it.toIntOrNull() }
            .map { it.dec() }
            .toSet()

    private fun parseAnswers(answersLines: List<String>) = answersLines
            .map { it.removePrefix(ANSWER_TOKEN) }
            .map { it.trimStart() }
            .toSet()

    private fun <T> List<String>.mapNotBlank(transform: (String) -> T) = this.filter { it.isNotBlank() }.map(transform)
}