package pl.kkurczewski.quizapp

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CompoundButton
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuizActivity : AppCompatActivity() {

    companion object {
        private const val REPEAT_QUESTION_AFTER = 5
        private const val PICK_FILE_RESULT_CODE = 200
        private const val SUPPORTED_MIME_TYPE = "text/plain"
    }

    // views
    private lateinit var titleView: TextView
    private lateinit var answersViewGroup: RadioGroup
    private lateinit var confirmAnswerBtn: Button
    private lateinit var nextQuestionBtn: Button
    private lateinit var fab: FloatingActionButton

    // dialogs
    private val toastReadFailed get() = Toast.makeText(baseContext, "Failed to load file", LENGTH_SHORT)

    // data
    private lateinit var currentQuiz: Quiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz_view)

        titleView = findViewById<TextView>(R.id.title).apply { text = "No file provided" }
        answersViewGroup = findViewById(R.id.answers)
        confirmAnswerBtn = findViewById<Button>(R.id.confirm_answer).apply {
            setOnClickListener {
                nextQuestionBtn.isEnabled = true
                confirmAnswerBtn.isEnabled = false

                val answers = answersViewGroup.children
                        .filterIsInstance<CompoundButton>()
                        .onEach { it.highlightAnswer() }
                        .mapIndexedNotNull { index, view -> if (view.isChecked) index else null }
                        .toSet()

                currentQuiz.answerQuestion(answers)
            }
        }
        nextQuestionBtn = findViewById<Button>(R.id.next_question).apply {
            setOnClickListener {
                confirmAnswerBtn.isEnabled = true
                nextQuestionBtn.isEnabled = false

                updateQuestion(currentQuiz.nextQuestion())
            }
        }
        fab = findViewById<FloatingActionButton>(R.id.fab).apply { setOnClickListener { pickFile() } }
        pickFile()
    }

    private fun pickFile() {
        val chooseFile = Intent(ACTION_GET_CONTENT)
                .apply { type = SUPPORTED_MIME_TYPE }
                .let { Intent.createChooser(it, "Choose a file") }
        startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FILE_RESULT_CODE -> {
                loadQuestions(data)
                updateQuestion(currentQuiz.nextQuestion())
            }
        }
    }

    private fun loadQuestions(data: Intent?) {
        val questionsFile = data?.data?.stringContent()
        if (questionsFile == null) {
            toastReadFailed.show()
            return
        }

        currentQuiz = RepeatingQuiz(QuestionFactory.fromString(questionsFile), REPEAT_QUESTION_AFTER)
    }

    private fun updateQuestion(currentQuestion: Question?) {
        when (currentQuestion?.question) {
            null -> {
                titleView.text = "No more questions"
                confirmAnswerBtn.visibility = GONE
                nextQuestionBtn.visibility = GONE
                answersViewGroup.visibility = GONE
            }
            else -> {
                titleView.text = currentQuestion.question
                confirmAnswerBtn.visibility = VISIBLE
                nextQuestionBtn.visibility = VISIBLE
                answersViewGroup.visibility = VISIBLE
                answerButtons(currentQuestion).forEach { answersViewGroup.addView(it, MATCH_PARENT, WRAP_CONTENT) }
            }
        }
    }

    private fun answerButtons(question: Question): List<CompoundButton> {
        answersViewGroup.removeAllViews()

        val isMultiSelection = question.correctAnswers.size > 1
        return question.answers.mapIndexed { index, view ->
            (if (isMultiSelection) AppCompatCheckBox(this) else AppCompatRadioButton(this)).apply {
                text = view
                // save metadata about correct answer in tag
                tag = question.correctAnswers.contains(index)
            }
        }
    }

    private fun CompoundButton.highlightAnswer() {
        when {
            // retrieve tag and check if correct answer
            tag as Boolean -> setTextColor(GREEN)
            isChecked -> setTextColor(RED)
        }
        isEnabled = false
    }

    private fun Uri.stringContent() = contentResolver.openInputStream(this)?.readBytes()?.decodeToString()
}