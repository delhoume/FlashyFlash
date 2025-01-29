package com.delhoume.flashyflash.dataclass

import androidx.compose.runtime.MutableState
import com.delhoume.flashyflash.util.CityInfo

data class SpaceInvader(
    var code: String, var flashed: MutableState<Boolean>,
    var selected: MutableState<Boolean>, var startIndex: Int = 0
) {
    fun swapFlash() {
        flashed.value = !flashed.value
    }
    fun swapSelection() {
        selected.value = !selected.value
    }

    fun isCity() : Boolean {
        var parts = code.split("_")
        return parts[0] == parts[1]
    }
    fun isFlashedCity() : Boolean {
        var parts = code.split("_")
        if (parts[0] != parts[1])
            return false
        var all_cities = CityInfo.getInstance()
        var city = all_cities.getCityByCode(parts[0])
        return city.flashed > 0
    }
}



