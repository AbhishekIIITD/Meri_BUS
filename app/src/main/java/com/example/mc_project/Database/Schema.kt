package com.example.mc_project.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class StopInfo(
    @PrimaryKey(autoGenerate = true)
    val stopId:Int = -1,
    val latitude: Double ,
    val name: String,
    val longitude: Double,
    val distance: String=""
    )


@Dao
interface StopDao {
    @Query("SELECT * FROM StopInfo")
    fun getAllFavouriteStops(): LiveData<List<StopInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg stops: StopInfo)

    @Query("DELETE FROM StopInfo")
    fun deleteAll()

    @Query("DELETE FROM StopInfo WHERE stopId==:stopId")
    fun deleteByStopId(stopId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM StopInfo WHERE stopId=:stopId)")
    fun contains(stopId: Int): LiveData<Boolean?>?
}
