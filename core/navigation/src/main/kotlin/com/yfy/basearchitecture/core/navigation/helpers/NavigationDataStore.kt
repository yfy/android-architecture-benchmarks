package com.yfy.basearchitecture.core.navigation.helpers

object NavigationDataStore {

    private val dataMap = mutableMapOf<String, Any?>()

    fun <T> putData(key: String, data: T) {
        dataMap[key] = data as Any
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(key: String): T? {
        return dataMap[key] as? T
    }

    fun removeData(key: String) {
        dataMap.remove(key)
    }

    fun clearData() {
        dataMap.clear()
    }

    fun generateKey(): String {
        return "nav_data_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}