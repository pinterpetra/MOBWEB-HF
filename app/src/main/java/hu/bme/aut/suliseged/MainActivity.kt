package hu.bme.aut.suliseged

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import hu.bme.aut.suliseged.adapter.SulisAdapter
import hu.bme.aut.suliseged.data.SulisFeladat
import hu.bme.aut.suliseged.data.SulisFeladatlistaAdatbazis
import hu.bme.aut.suliseged.fragments.NewSulisFeladatDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread
import hu.bme.aut.suliseged.R.id.action_settings
import hu.bme.aut.suliseged.fragments.DatePickerDialogFragment

class MainActivity : AppCompatActivity(), SulisAdapter.SulisFeladatClickListener,
    NewSulisFeladatDialogFragment.NewSulisFeladatDialogListener, DatePickerDialogFragment.OnDateSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SulisAdapter
    private lateinit var database: SulisFeladatlistaAdatbazis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener{
            NewSulisFeladatDialogFragment().show(
                supportFragmentManager,
                NewSulisFeladatDialogFragment.TAG
            )
        }

        button_naptar_nezetbe.setOnClickListener {
            DatePickerDialogFragment()
                .show(supportFragmentManager, "DATE_TAG")
        }

        database = Room.databaseBuilder(
            applicationContext,
            SulisFeladatlistaAdatbazis::class.java,
            "sulis-feladatok"
        ).build()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = SulisAdapter(this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val pref = prefs.getBoolean("csaknemkesz", false)

        thread {
            val items = if(pref)  database.sulisFeladatDao().getUndone()  else database.sulisFeladatDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onItemChanged(item: SulisFeladat) {
        thread {
            database.sulisFeladatDao().update(item)

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val pref = prefs.getBoolean("csaknemkesz", false)


            runOnUiThread {
                    if (pref && item.isDone) {
                        adapter.deleteItem(item)
                    }
                }

            Log.d("MainActivity", "SulisFeladat update was successful")
        }
    }

    override fun onSulisFeladatCreated(newItem: SulisFeladat) {
        thread {
            val newId = database.sulisFeladatDao().insert(newItem)
            val newSulisFeladat = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newSulisFeladat)
            }
        }
    }

    override fun onItemDeleted(oldItem: SulisFeladat) {
        thread {
            database.sulisFeladatDao().deleteItem(oldItem)
            runOnUiThread {
                adapter.deleteItem(oldItem)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                this.startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDateSelected(year: Int, month: Int, day: Int) {
        //egyeb
    }
}

