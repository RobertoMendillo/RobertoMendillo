package it.unisannio.studenti.mendillo.myfirstapplication

import androidx.annotation.StringRes

/**
 * Modella una domanda
 *
 */
data class Question (@StringRes val textResId: Int, val reply:Boolean){

    private var question = textResId
    private var answer = reply

    fun getAnswer() : Boolean{
        return this.answer
    }

}