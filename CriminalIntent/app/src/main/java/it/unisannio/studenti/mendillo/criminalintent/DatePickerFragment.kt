package it.unisannio.studenti.mendillo.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*
private const val ARG_DATE = "date"
private const val ARG_REQUEST_CODE = "requestCode"
private const val RESULT_DATE_KEY = ""

class DatePickerFragment: DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener {
               _: DatePicker, year: Int, month: Int, day: Int ->
            val resultDate: Date = GregorianCalendar(year, month, day).time

            //create our result Bundle
            val result = Bundle().apply {
                putSerializable(RESULT_DATE_KEY, resultDate)
            }

            val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                parentFragmentManager.setFragmentResult(resultRequestCode, result)
        }
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initiateYear = calendar.get(Calendar.YEAR)
        val initiateMonth = calendar.get(Calendar.MONTH)
        val initiateDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initiateYear,
            initiateMonth,
            initiateDay
        )
    }

    companion object{
        fun newInstance(date: Date, requestCode: String): DatePickerFragment{
            val args = Bundle().apply {
                putString(ARG_REQUEST_CODE, requestCode)
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply { arguments = args }
        }

        fun getSelectedDate(result: Bundle) = result.getSerializable(RESULT_DATE_KEY) as Date
    }

}

