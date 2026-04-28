package com.simpe.bridge.appmovil.domain.utils

import android.content.Context
import android.content.SharedPreferences

//Acá se configuran los remitentes
class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "simpe_bridge_settings"
        private const val KEY_SINPE_SENDERS = "sinpe_senders"
        
        // Estos son los remitentes por defecto
        val DEFAULT_SENDERS = setOf(
            "SINPE",
            "BNCR",
            "BCR", 
            "BAC",
            "POPULAR",
            "CREDITOS",
            "COOPECAJA",
            "MULTIMONEY",
            "+506",
            "506"
        )
    }

    
     //Obtiene la lista de remitentes esperados.
    fun getSinpeSenders(): Set<String> {
        val saved = prefs.getStringSet(KEY_SINPE_SENDERS, null)
        return saved ?: DEFAULT_SENDERS
    }

    
     //Guarda la lista de remitentes esperados.
    fun setSinpeSenders(senders: Set<String>) {
        prefs.edit().putStringSet(KEY_SINPE_SENDERS, senders).apply()
    }

    //Agrega un remitente a la lista.
    fun addSinpeSender(sender: String) {
        val current = getSinpeSenders().toMutableSet()
        current.add(sender.uppercase().trim())
        setSinpeSenders(current)
    }

    // Remueve un remitente de la lista.
    fun removeSinpeSender(sender: String) {
        val current = getSinpeSenders().toMutableSet()
        current.remove(sender.uppercase().trim())
        setSinpeSenders(current)
    }

    //Resetea a remitentes por defecto.
    fun resetToDefaults() {
        setSinpeSenders(DEFAULT_SENDERS)
    }

    //Verifica si un remitente está en la lista configurada.
    fun isKnownSender(sender: String): Boolean {
        val senders = getSinpeSenders()
        return senders.any { sender.contains(it, ignoreCase = true) }
    }
}