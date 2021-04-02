package hu.bme.aut.suliseged

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.room.Room
import hu.bme.aut.suliseged.data.SulisFeladat
import hu.bme.aut.suliseged.data.SulisFeladatlistaAdatbazis
import kotlinx.android.synthetic.main.activity_edit.*
import java.util.*
import kotlin.concurrent.thread

class EditActivity : AppCompatActivity() {
    private lateinit var database: SulisFeladatlistaAdatbazis
    private lateinit var feladat: SulisFeladat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fel_id = intent.extras!!.getLong("FELADAT_ID")

        database = Room.databaseBuilder(
            applicationContext,
            SulisFeladatlistaAdatbazis::class.java,
            "sulis-feladatok"
        ).build()

        editSulisFeladatCategorySpinner.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )
        )

        thread {
           feladat = database.sulisFeladatDao().getItemById(fel_id)

            runOnUiThread {
                editSulisFeladatNameEditText.setText(feladat.name)
                editSulisFeladatCategorySpinner.setSelection(feladat.category.ordinal)
                editSulisFeladatDescriptionEditText.setText(feladat.description)
                editSulisFeladatIsDoneCheckBox.isChecked = feladat.isDone

                feladat.deadline?.let {
                    val date = Calendar.getInstance()
                    date.timeInMillis = it * 1000



                    editSulisFeladatDeadlineEditText.init(date[Calendar.YEAR], date[Calendar.MONTH], date[Calendar.DAY_OF_MONTH], null)
                }
            }
        }
    }

    private fun getDateFrom(picker: DatePicker): Long? {
        val value: Long;

        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, picker.year);
        c.set(Calendar.MONTH, picker.month)
        c.set(Calendar.DAY_OF_MONTH, picker.dayOfMonth)

        value = c.timeInMillis / 1000;

        return value;
    }

    override fun onPause() {
        super.onPause()

        feladat.name = editSulisFeladatNameEditText.text.toString()
        feladat.description = editSulisFeladatDescriptionEditText.text.toString()
        feladat.deadline = getDateFrom(editSulisFeladatDeadlineEditText)
        feladat.category = SulisFeladat.Category.getByOrdinal(editSulisFeladatCategorySpinner.selectedItemPosition)
            ?: SulisFeladat.Category.ZH
        feladat.isDone = editSulisFeladatIsDoneCheckBox.isChecked

        thread {
            database.sulisFeladatDao().update(feladat)
        }
    }

}