package com.delhoume.flashbattle.util

import android.app.Activity
import com.delhoume.flashbattle.dataclass.City
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.util.Scanner
import kotlin.collections.get

class CityInfo private constructor() {
    companion object {
        @Volatile
        private var instance: CityInfo? = null

        fun getInstance(): CityInfo {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = CityInfo()
                    }
                }
            }
            return instance!!
        }
    }

    var cities_by_code = HashMap<String, City>()
    var cities_by_name = HashMap<String, String>()
    var unknown_city = City("XX", "unknown", "", 1, 0, 0)
    var ordered_cities: MutableList<String> = ArrayList()


    fun init(input: InputStream) {
        val strInput = input.bufferedReader().use { it.readText() }
        val json = JSONObject(strInput)
        val keys = json.keys().asSequence().toList()
        keys.forEach { key ->
            val citynode = json.getJSONObject(key)
            val code = citynode.getString("code")
            val name = citynode.getString("name")
            val country = citynode.getString("country")
            val invaders = citynode.getInt("invaders")
            var start: Int = 1;
            if (code == "LIL") start = 0
            var city = City(code, name, country, start, invaders, 0)
            cities_by_code.put(code, city)
            cities_by_name.put(name, code)
        }

    }

    fun getKnownCities(): List<String> {
        return cities_by_name.keys.sorted()
    }


    fun getCityByCode(code: String): City {
        var ret = cities_by_code.get(code)
        if (ret == null) ret = unknown_city
        return ret
    }

    fun getCityByName(name: String): City {
        var code = cities_by_name.get(name)
        if (code == null) code = ""
        return getCityByCode(code)
    }

    fun getCityCodeFromSICode(sicode: String): String {
        if (sicode.contains("_")) {
            val parts =
                sicode.split("_")
            return parts[0]
        }
        return ""
    }

    fun getRighPartFromSICode(sicode: String): String {
        if (sicode.contains("_")) {
            val parts = sicode.split("_")
            return parts[1]
        }
        return ""
    }

    fun getSINumberFromSICode(sicode: String): Int {
        if (sicode.contains("_")) {
            val parts =
                sicode.split("_")
            var city = getCityByCode(parts[0])
            return parts[1].toInt() - city.start
        }
        return 0
    }
}