package pl.kkurczewski.quizapp

import org.junit.Test

class QuestionFactoryTest {
    @Test
    fun `should parse serialized questions`() {
        val questionData = QuestionFactory.fromString("""
            Q: Lorem ipsum dolor met?
            A: 2,3
            - consectetur
            - adipiscing
            - eiusmod tempor
            - incididunt
            
            Q:Duis aute irure dolor in reprehenderit?
            A:1
            -in voluptate
            -velit esse cillum
            -dolore eu fugiat
            -occaecat
            -cupidatat
            -sunt in culpa
        """.trimIndent())

        assertEquals(questionData.size, 2)
        val (firstQuestion, secondQuestion) = questionData
        assertEquals(firstQuestion.question, "Lorem ipsum dolor met?")
        assertEquals(firstQuestion.correctAnswers, setOf(1, 2))
        assertEquals(firstQuestion.answers, setOf("consectetur", "adipiscing", "eiusmod tempor", "incididunt"))

        assertEquals(secondQuestion.question, "Duis aute irure dolor in reprehenderit?")
        assertEquals(secondQuestion.correctAnswers, setOf(0))
        assertEquals(secondQuestion.answers, setOf(
                "in voluptate",
                "velit esse cillum",
                "dolore eu fugiat",
                "occaecat",
                "cupidatat",
                "sunt in culpa"
        ))
    }

    private fun <T> assertEquals(expected: T, actual: T) {
        assert(actual == expected) { "Expected <$expected> to be equal <$actual>" }
    }
}