package pl.kkurczewski.quizapp

/**
 * Create a quiz with repeating questions after specified factor
 */
class RepeatingQuiz(
        private val questions: MutableList<Question>,
        private val repeatAfter: Int,
) : Quiz {

    private var currentQuestion: Question? = null

    init {
        questions.shuffle()
    }

    override fun answerQuestion(answers: Set<Int>): Boolean {
        currentQuestion ?: return false
        val isCorrectAnswer = currentQuestion!!.correctAnswers == answers
        if (!isCorrectAnswer) {
            onAnswerIncorrect(currentQuestion!!)
        }
        return isCorrectAnswer
    }

    override fun nextQuestion(): Question? {
        currentQuestion = questions.removeFirstOrNull()
        return currentQuestion
    }

    private fun onAnswerIncorrect(currentQuestion: Question) {
        questions.add(minOf(repeatAfter, questions.size), currentQuestion)
    }
}