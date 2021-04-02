package hu.bme.aut.suliseged.data

import androidx.room.*

@Dao
interface SulisFeladatDao {
    @Query("SELECT * FROM sulisFeladat")
    fun getAll(): List<SulisFeladat>

    @Insert
    fun insert(sulisFeladatok: SulisFeladat): Long

    @Update
    fun update(sulisFeladat: SulisFeladat)

    @Delete
    fun deleteItem(sulisFeladat: SulisFeladat)

    @Query("SELECT * FROM sulisFeladat WHERE id=:id")
    fun getItemById(id: Long): SulisFeladat

    @Query("SELECT * FROM sulisFeladat WHERE is_done=0")
    fun getUndone(): List<SulisFeladat>
}