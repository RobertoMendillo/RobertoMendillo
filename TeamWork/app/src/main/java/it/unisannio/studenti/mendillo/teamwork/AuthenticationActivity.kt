package it.unisannio.studenti.mendillo.teamwork

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AuthenticationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_authentication)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.authentication_fragment_container)
        Log.d("AuthActivity", currentFragment.toString())
        val fragment = LoginFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.authentication_fragment_container, fragment)
            .commit()
    }






}