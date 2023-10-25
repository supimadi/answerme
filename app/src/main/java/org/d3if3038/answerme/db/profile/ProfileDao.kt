package org.d3if3038.answerme.db.profile

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.d3if3038.answerme.model.Profile

@Dao
interface ProfileDao {

    @Insert
    fun insert(profile: Profile)

    @Query("SELECT * FROM profile ORDER BY id ASC")
    fun getProfile() : LiveData<Profile>
}