package org.d3if3038.answerme.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


private const val PREFRENCES_NAME = "preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFRENCES_NAME,
    produceMigrations = {context ->
        listOf(SharedPreferencesMigration(context, PREFRENCES_NAME))
    }
)

class SettingDataStore(private val prefDataStore: DataStore<Preferences>) : PreferenceDataStore() {
    override fun putBoolean(key: String, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            prefDataStore.edit { it[booleanPreferencesKey(key)] = value }
        }
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return runBlocking {
            prefDataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[booleanPreferencesKey((key))] ?: defValue }
                .first()
        }
    }

    override fun putString(key: String?, value: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            prefDataStore.edit { it[stringPreferencesKey(key!!)] = value!! }
        }
    }

    override fun getString(key: String?, defValue: String?): String {
        return runBlocking {
            prefDataStore.data
                .catch { emit(emptyPreferences()) }
                .map { it[stringPreferencesKey((key!!))] ?: defValue }
                .first()!!
        }
    }

}