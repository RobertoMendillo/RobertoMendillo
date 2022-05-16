package it.unisannio.studenti.mendillo.myfirstapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

private const val EXTRA_ANSWER_IS_TRUE = "it.unisannio.studenti.mendillo.myfirstapplication.answer_is_true"
private const val EXTRA_ANSWER_SHOW = "it.unisannio.studenti.mendillo.myfirstapplication.answer_shown"

class CheatActivity : AppCompatActivity() {

    // Contiene la risposta corretta
    private var answerIsTrue = false
    // Text view che contiene la risposta corretta alla domanda
    private lateinit var answerTextView: TextView
    // Pulsante per mostrare la risposta corretta alla domanda
    private lateinit var showAnswerButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }

    }

    /**
     * Funzione che crea un INTENT, mette un extra contenente il valore booleano che
     * indica che l'utente ha visualizzato la risposta e lo restituisce alla mainActivity
     */
    private fun setAnswerShownResult(isAnswerShown: Boolean){
        val data = Intent().apply { putExtra(EXTRA_ANSWER_SHOW, isAnswerShown) }
        setResult(Activity.RESULT_OK, data)
    }

    /* Un companion object permette di creare un Intent configurato opportunamente con gli
    extra di cui CheatActivity ha bisogno.
    In paricolare esso permette di accedere alle funzioni senza avere una istanza di una classe,
    in modo simile alle funzioni statiche java.
     */
    companion object{
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent{
            return Intent(packageContext, CheatActivity::class.java).apply{
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}