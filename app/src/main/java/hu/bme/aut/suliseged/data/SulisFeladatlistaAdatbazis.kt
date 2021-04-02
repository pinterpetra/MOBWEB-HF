package hu.bme.aut.suliseged.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SulisFeladat::class], version = 1)
@TypeConverters(value = [SulisFeladat.Category::class])
abstract class SulisFeladatlistaAdatbazis : RoomDatabase() {
    abstract fun sulisFeladatDao(): SulisFeladatDao

    //szerkeszthetoseghez
    /*companion object {
        private var INSTANCE: SulisFeladatlistaAdatbazis? = null

        fun getInstance(context: Context): SulisFeladatlistaAdatbazis {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    SulisFeladatlistaAdatbazis::class.java, "sulis.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }*/
}