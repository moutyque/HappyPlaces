package small.app.happyplaces.db

import androidx.room.*

@Dao
interface HappyPlaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg happyPlaceEntity: HappyPlace)


    @Query("SELECT * FROM HappyPlace")
    fun findAll(): List<HappyPlace>

    @Delete
    fun delete(vararg happyPlaceEntity: HappyPlace)
}

