package it.unisannio.studenti.mendillo.teamwork

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button

class SendButtonObserver( private val button: Button): TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0?.isEmpty() == true) {
            button.isEnabled = false
        }
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (p0?.isNotEmpty() == true){
            button.isEnabled = true
        }
        if (p0?.isEmpty() == true){
            button.isEnabled = false
        }
    }

    override fun afterTextChanged(p0: Editable?){}

}