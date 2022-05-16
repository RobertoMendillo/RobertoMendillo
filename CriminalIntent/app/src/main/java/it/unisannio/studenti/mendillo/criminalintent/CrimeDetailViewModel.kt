package it.unisannio.studenti.mendillo.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel(): ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    //Stores the ID of the crime currently displayed by crimeFragment
    private val crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = Transformations.switchMap(crimeIdLiveData){ crimeId ->
        crimeRepository.getCrime(crimeId)
    }

    fun loadCrime(crimeId: UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime){
        crimeRepository.updateCrime(crime)
    }

}