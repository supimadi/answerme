package org.d3if3038.answerme.db.profile


import org.d3if3038.answerme.model.Profile
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Profile::class], version = 1, exportSchema = false)
abstract class ProfileDb : RoomDatabase() {

    abstract val dao: ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ProfileDb? = null

        fun getInstance(context: Context) : ProfileDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProfileDb::class.java,
                        "profile.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()

                }
                return instance

            }
        }
    }


}