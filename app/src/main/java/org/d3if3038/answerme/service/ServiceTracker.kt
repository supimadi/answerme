package org.d3if3038.answerme.service

import android.content.Context
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore


enum class ServiceState {
    STARTED,
    STOPPED
}

fun setServiceState(context: Context, state: ServiceState) {
    val settingDataStore = SettingDataStore(context.dataStore)
    settingDataStore.putString("SPYSERVICE_STATE", state.name)
}

fun getServiceState(context: Context): ServiceState {
    val settingDataStore = SettingDataStore(context.dataStore)
    val value =  settingDataStore.getString("SPYSERVICE_STATE", ServiceState.STOPPED.name)
    return ServiceState.valueOf(value)
}