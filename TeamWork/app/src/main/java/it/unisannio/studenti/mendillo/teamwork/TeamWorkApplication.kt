package it.unisannio.studenti.mendillo.teamwork

import android.app.Application


/**
 * The application instance does not get constantly destroyed and re-created, like activity or fragment classes.
 */
class TeamWorkApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        GroupRepository.initialize(this)
    }

}