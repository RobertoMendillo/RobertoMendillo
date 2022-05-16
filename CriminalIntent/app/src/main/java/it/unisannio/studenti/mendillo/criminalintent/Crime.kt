package it.unisannio.studenti.mendillo.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Models a Crime. This class represents the model layer of the application.
 *
 */
@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String ="",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = "") {





}