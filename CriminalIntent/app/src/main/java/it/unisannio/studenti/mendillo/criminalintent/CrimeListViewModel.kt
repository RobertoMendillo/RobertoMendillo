package it.unisannio.studenti.mendillo.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }

    companion object{
        fun addCrime(crime: Crime){
            CrimeRepository.get().addCrime(crime)
        }
    }

}