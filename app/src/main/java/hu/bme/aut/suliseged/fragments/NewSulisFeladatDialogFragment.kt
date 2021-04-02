package hu.bme.aut.suliseged.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.suliseged.MainActivity
import hu.bme.aut.suliseged.R
import hu.bme.aut.suliseged.data.SulisFeladat
import kotlinx.android.synthetic.main.dialog_uj_feladatlista_elem.*
import kotlinx.android.synthetic.main.dialog_uj_feladatlista_elem.view.*
import java.util.*


class NewSulisFeladatDialogFragment : DialogFragment() {
    interface NewSulisFeladatDialogListener {
        fun onSulisFeladatCreated(newItem: SulisFeladat)
    }

    private lateinit var listener: NewSulisFeladatDialogListener

    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var deadlineDatePicker: DatePicker
    private lateinit var categorySpinner: Spinner
    private lateinit var alreadyDoneCheckBox: CheckBox

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewSulisFeladatDialogListener
            ?: throw RuntimeException("Activity must implement the NewSulisFeladatDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.uj_sulis_feladat)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onSulisFeladatCreated(getSulisFeladat())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = nameEditText.text.isNotEmpty()

    private fun getDateFrom(picker: DatePicker): Long? {
        val value: Long;

        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, picker.year);
        c.set(Calendar.MONTH, picker.month)
        c.set(Calendar.DAY_OF_MONTH, picker.dayOfMonth)

        value = c.timeInMillis / 1000;

        return value;
   }

    private fun getSulisFeladat() = SulisFeladat(
        id = null,
        name = nameEditText.text.toString(),
        description = descriptionEditText.text.toString(),
        deadline = getDateFrom(deadlineDatePicker),
        category = SulisFeladat.Category.getByOrdinal(categorySpinner.selectedItemPosition)
            ?: SulisFeladat.Category.ZH,
        isDone = alreadyDoneCheckBox.isChecked
    )

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_uj_feladatlista_elem, null)
        nameEditText = contentView.findViewById(R.id.SulisFeladatNameEditText)
        descriptionEditText = contentView.findViewById(R.id.SulisFeladatDescriptionEditText)
        deadlineDatePicker = contentView.findViewById(R.id.SulisFeladatDeadlineEditText)
        categorySpinner = contentView.findViewById(R.id.SulisFeladatCategorySpinner)
        categorySpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )
        )
        alreadyDoneCheckBox = contentView.findViewById(R.id.SulisFeladatIsDoneCheckBox)
        return contentView
    }

    fun legyenedatum(){
        SulisFeladatDeadlineEditText.visibility
    }

    companion object {
        const val TAG = "NewSulisFeladatDialogFragment"
    }
}