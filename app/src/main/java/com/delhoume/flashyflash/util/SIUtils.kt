package com.delhoume.flashyflash.util

import androidx.compose.runtime.mutableStateOf
import com.delhoume.flashyflash.dataclass.City
import com.delhoume.flashyflash.dataclass.SpaceInvader
import com.delhoume.flashyflash.flashfile.FlashFile
import com.delhoume.flashyflash.flashfile.printInvaderNumber

fun copyInvaders(invaders: List<SpaceInvader>): MutableList<SpaceInvader> {
    var newlist: MutableList<SpaceInvader> = ArrayList()
    for (si in invaders) {
        newlist.add(SpaceInvader(si.code, si.flashed, si.selected, si.startIndex))
    }
    return newlist
}

fun copyFlashedInvadersAndCities(invaders: List<SpaceInvader>): MutableList<SpaceInvader> {
    var flashed = invaders.filter { it.flashed.value || it.isFlashedCity() }
    return copyInvaders(flashed)
}


fun getKnownCityIndex(cityname: String): Int {
    return CityInfo.getInstance().getKnownCities().indexOf(cityname)
}

// not optimized (possible number too small to bother
fun getSpaceInvaderFromCode(invaders: List<SpaceInvader>, code: String): SpaceInvader? {
    return invaders.find { it.code == code }
}

fun ListFromFlashfile(flashfile: String): MutableList<SpaceInvader> {
    var citiesStart: MutableList<Int> = ArrayList()
    var invaders: MutableList<SpaceInvader> = ArrayList()
    var allcities = CityInfo.getInstance()
    var knowncities = allcities.getKnownCities()
    var ff = FlashFile()
    var flashedtokens = ff.decodeString(flashfile)
    // first pass to build maps
    for (t in 0..flashedtokens.size - 1) {
        var si_code = flashedtokens[t]
        var citycode = allcities.getCityCodeFromSICode(si_code)
        var city = allcities.getCityByCode(citycode)
        if (city.code != citycode) {
            var newcity = City(citycode, citycode, "", 1, 1, 0)
            allcities.cities_by_code.put(citycode, newcity)
            allcities.cities_by_name.put(citycode, citycode)
            city = allcities.getCityByCode(citycode)
        }
        var number = allcities.getSINumberFromSICode(si_code)
        if (number > city.max) { // add at least num-max si
            city.max = number
        }
        city.flashed++
    }
    // second pass create the SIs
    var start = 0
    for (c in 0..knowncities.size - 1) {
        var city = allcities.getCityByName(knowncities[c])
        var cityflashed = city.flashed > 0
        invaders.add(
            SpaceInvader(
                "${city.code}_${city.code}",
                mutableStateOf(cityflashed), mutableStateOf(false)
            )
        )
        start += 1
        citiesStart.add(start)
        if (city.invaders > 0) {
            for (si in 0..city.max - 1) {
                invaders.add(
                    SpaceInvader(
                        "${city.code}_${printInvaderNumber(si + city.start, true)}",
                        mutableStateOf(false), mutableStateOf(false)
                    )
                )
            }
        }
        start += city.max
    }

    //
    for (t in 0..flashedtokens.size - 1) {
        var si_code = flashedtokens[t]
        // third to set flash status
        var citycode = allcities.getCityCodeFromSICode(si_code)
        var si = getSpaceInvaderFromCode(invaders, citycode + "_" + citycode)
        if (si != null) {
            si.flashed.value = true
            si = getSpaceInvaderFromCode(invaders, flashedtokens[t])
            if (si != null)
                si.flashed.value = true
        }
    }
    return invaders
}


fun computeCitiesFlashed(invaders: List<SpaceInvader>): Int {
    var totalflashedcities = 0
    var allcities = CityInfo.getInstance()
    var sortedcities = allcities.getKnownCities()
    for (cc in 0..sortedcities.size - 1) {
        var city = allcities.getCityByName(sortedcities[cc])
        if (city.flashed > 0) {
            totalflashedcities++
            var si = getSpaceInvaderFromCode(invaders, city.code + "_" + city.code)
            if (si != null)
                si.flashed.value = true
        }
    }
    return totalflashedcities
}

fun computeStats(invaders: List<SpaceInvader>): Int {
    var allcities = CityInfo.getInstance()
    var sortedcities = allcities.getKnownCities()

    for (c in 0..sortedcities.size - 1) {
        allcities.getCityByName(sortedcities[c]).flashed = 0
    }
    var allflashedinvaders = 0
    for (si in 0..invaders.size - 1) {
        var invader = invaders[si]
        if (!invader.isCity() && invader.flashed.value) {
            allflashedinvaders++
            var citycode = allcities.getCityCodeFromSICode(invaders[si].code)
            var city = allcities.getCityByCode(citycode)
            city.flashed++
        }
    }
    return allflashedinvaders
}
