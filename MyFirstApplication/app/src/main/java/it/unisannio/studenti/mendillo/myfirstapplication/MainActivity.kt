package it.unisannio.studenti.mendillo.myfirstapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider

// Utilizzato per riconoscere l'activity nel logcat
private const val TAG = "MainActivity"

// Utilizzato per mappare lo stato delle activity view dell'oggetto nel bundle
private const val KEY_INDEX = "index"

// Utilizzato per riconoscere da quale activity è stato restiuito il dato associato a questa stringa
private const val EXTRA_ANSWER_SHOW = "it.unisannio.studenti.mendillo.myfirstapplication.answer_shown"

class MainActivity : AppCompatActivity() {

    // Recupera l'istanza di View Model associata all'activity o ne crea una nel caso non esistesse
    private val quizViewModel: QuizViewModel by lazy{
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    /*
    Dichiaro le variabili sotto di tipo Button e dico al compilatore
    che le inizializzerò più tardi con un valore non nullo usando lo statement
    'lateinit'
     */
    private lateinit var trueButton : Button
    private lateinit var falseButton : Button
    private lateinit var nextButton : Button
    private lateinit var cheatButton : Button
    private lateinit var questionTextView : TextView


    /* Oggetto di tipo ActivityResultLauncher che permette di richiedere il lancio di un'activity.
    * È possibile specificare il risultato atteso dall'activity
     */
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOW, false) ?: false
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")  // Visualizzo la chiamata alla funzione onCreate nel logcat
        setContentView(R.layout.activity_main)  // Imposto la content view dell'activity recuperando la risorsa associata

        /*
        Recupero lo stato della view nel caso sia stata creata in precedenza. In questo caso recupero il valore di index
         */
        val index = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.index = index

        /* Inizializzo gli oggetti Button recuperando la loro View attraverso l'Id
        che gli ho assegnato in precedenza
         */
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)


        // Impostiamo un listner sugli oggetti Button.
        trueButton.setOnClickListener {
            checkAnswer(true)
        }
        falseButton.setOnClickListener {
            checkAnswer(false)
        }
        nextButton.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            /*
            Oggetto utilizzato per comunicare con il SO.
            In questo caso stiamo creando un intent per comunicare all'ActivityManager di avviare un'activity
             */
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)

            // startActivityForResult(intent, REQUEST_CODE_CHEAT) deprecated
            getContent.launch(intent)
        }

        updateQuestion()
    }

    override fun onStart(){
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause(){
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop(){
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        /*
        Inserisco nell'oggetto bundle l'indice in modo da poterlo recuperare in seguito
         */
        outState.putInt(KEY_INDEX, quizViewModel.index)
    }


    /*
    Questa funzione accede alla lista di domande, seleziona l'oggetto
    specificato dall'indice, recupera la stringa associata e la salva
    nella variabile questionTextResId.
    Dopodiché modifica il testo dell'oggetto TextView con la stringa
    recuperata.
     */
    private fun updateQuestion(){
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer : Boolean){
        val correctAnswer = quizViewModel.currentQuestionAnswer

        /*
        val messageResId = if (userAnswer == correctAnswer){

            R.string.correct_toast
        }
        else{
            R.string.incorrect_toast
        }
    */
        /*
        MessageResId rappresenta il messaggio da mostrare per comunicare all'utente
        se la risposta che ha dato è corretta o meno.
        È pari a judgment se l'utente ha utilizzato il pulsante cheat, true se ha risposto in modo
        corretto e false in caso contrario.
         */
        val messageResId = when{
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }
}
