package pl.kkurczewski.quizapp

data class Question(
        val question: String,
        val answers: Set<String>,
        val correctAnswers: Set<Int>,
)