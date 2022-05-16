package it.unisannio.studenti.mendillo.myfirstapplication

import androidx.lifecycle.ViewModel

// Utilizzato per riconoscere l'oggetto nel logcat
private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel(){

    // Indice della collezione
    var index = 0
    // Booleano che tiene traccia dell'uso del pulsante Cheat!
    var isCheater = false
    /*
    Lista delle domande da mostrare
     */
    private val questionBank = listOf(
        Question(R.string.question_australia,false ),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, true),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    /*
    Restituisce la risposta corretta alla domanda posta
     */
    val currentQuestionAnswer: Boolean
        get() = questionBank[index].getAnswer()

    /*
    Resituisce l'indice della collezione di domande per permettere
    alla mainActivity di riprendere dalla domanda visualizzata prima
    della chiusura
     */
    val currentQuestionText : Int
        get() = questionBank[index].textResId

    /*
    Funzione che incrementa l'indice della collezione delle domande
    per poter visualizzare la domanda successiva
     */
    fun moveToNext(){
        index = (index+1) % questionBank.size
    }
}
