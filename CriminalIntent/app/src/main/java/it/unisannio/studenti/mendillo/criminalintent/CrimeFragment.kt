package it.unisannio.studenti.mendillo.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"
private const val REQUEST_DATE = "DialogDate"
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1

class CrimeFragment : Fragment(), FragmentResultListener {

    private lateinit var crime : Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy{
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }
    private lateinit var reportButton : Button
    private lateinit var suspectButton : Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        Log.d(TAG, "args bundle crime Id: $crimeId")
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onStart() {
        super.onStart()

        /*
        Here we create an anonymous class that implements the verbose TextWatcher interface.
        TextWatcher has three functions, but we only care about one: onTextChanged()
         */
        val titleWatcher = object : TextWatcher {
            /*
            we call toString() in the char sequence that is the user's input.
            This function returns a string, which we than use to set the Crime's title
             */
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // this space is intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ){
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?){
                // This space intentionally left blank
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener{_, isChecked -> crime.isSolved = isChecked}
        }

        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date, REQUEST_DATE)
                .show(childFragmentManager, REQUEST_DATE)
            }

        reportButton.setOnClickListener{
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.crime_report_subject))
            }.also{ intent ->
                //startActivity(intent)
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener{
               pickContact.launch(null)
            }

            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            // la condizione dovrebbe essere resolvedActivity == null ma per qualche motivo il metodo resolveActivity restituisce sempre null,
            // facendo si che il pulsante sia sempre disabilitato
            if(resolvedActivity != null){
                isEnabled = false
            }
        }

    }

    val pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
            contactUri ->
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = contactUri?.let{
            requireActivity().contentResolver.query(it, queryFields, null, null, null)
        }
        cursor?.use{
            // Verify cursor contains at least one result
            if(it.count > 0){
                // Pull out first column of the first row of data, that's our suspect name
                it.moveToFirst()
                val suspect = it.getString(0)

                crime.suspect = suspect
                crimeDetailViewModel.saveCrime(crime)
                suspectButton.text = suspect
            }
        }
    }

    companion object{
        fun newInstance(crimeId: UUID): CrimeFragment{
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    /**
     * Function to configure the fragment view. On this function I inflate the layout for the
     * fragment view and return the inflated View to the hosting activity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe (
            viewLifecycleOwner, Observer{ crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
        })
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)

    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        //solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.apply{
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String{
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if(crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when(requestKey){
            REQUEST_DATE -> {
                Log.d(TAG, "received result for $requestKey")
                crime.date = DatePickerFragment.getSelectedDate(result)
                updateUI()
            }
        }
    }
}