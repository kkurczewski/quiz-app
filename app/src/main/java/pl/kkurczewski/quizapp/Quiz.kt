package pl.kkurczewski.quizapp

interface Quiz {
    /**
     * Answer question and change current question
     */
    fun answerQuestion(answers: Set<Int>): Boolean

    /**
     * Switch to next question
     */
    fun nextQuestion(): Question?
}