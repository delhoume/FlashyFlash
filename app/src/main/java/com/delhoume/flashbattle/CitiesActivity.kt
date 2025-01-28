package com.delhoume.flashbattle

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delhoume.flashbattle.dataclass.City
import com.delhoume.flashbattle.dataclass.InvadersList
import com.delhoume.flashbattle.flashfile.FlashFile
import com.delhoume.flashbattle.util.CityInfo

val city_all = City("ALL", "All cities", "", 0, 0, 0)


class CitiesActivity : ComponentActivity() {
    var cities: HashMap<String, City> = HashMap<String, City>()
    var flashfile: String? = ""
    var t = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var tokens: MutableList<String> = ArrayList()
        var incities: MutableList<City> = ArrayList()

        val bundle = intent.extras
        if (bundle != null) {
            var flashlist = bundle.getString("flashfile")
            if (flashlist == null) flashlist = ""
            var ff = FlashFile()
            tokens = ff.decodeString(flashlist)
        }
        val knownCitiesCodes = CityInfo.getInstance().getKnownCities()
        knownCitiesCodes.forEach {
            var city = CityInfo.getInstance().getCityByCode(it).copy()
            cities.put(city.code, city)
        }
        city_all.invaders = tokens.size

        tokens.forEach {
            var citycode = CityInfo.getInstance().getCityCodeFromSICode(it)
            var city = cities.get(citycode)
            if (city != null) {
                city.
                flashed++
            }
        }
// make list of flashed cities
        //      enableEdgeToEdge()
        incities.add(city_all)
        cities.values.toList().forEach {
            if (it.flashed > 0) {
                incities.add(it.copy())
            }
        }
        setContent {
            EntryPoint(cities = incities, activity = this)
        }
    }


    @Composable
    fun CityItem(city: City, onItemClick: (String) -> Unit) {
        Surface(
            color = Color.Black,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        {
            Column(modifier = Modifier.fillMaxWidth())
            {
                Row(modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        onItemClick(city.code)
                    })
                {
                    var txt = if (city.code == "ALL") (" / " + CityInfo.getInstance()
                        .getKnownCities().size.toString())
                    else city.country + " " + city.flashed.toString() + " / " + city.invaders.toString()
                    Text(
                        text = txt,
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = city.name,
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "delete", // trashcan
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    @Composable
    fun EntryPoint(cities: List<City>, activity: Activity) {
        CitiesFullPanel(cities, activity)
    }

    @Composable
    fun CitiesFullPanel(cities: List<City>, activity: Activity) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        {
            TopActions()
            RecyclerView(cities = cities, activity = activity)
        }
    }


    @Composable
    fun TopActions() {
        Surface(
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        {
            var showFlashed by remember { mutableStateOf(true) }
            var showNotFlashed by remember { mutableStateOf(false) }
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Flashed",
                    fontFamily = FontFamily(spaceFamily),
                    color = Color.White,
                    fontSize = if (showFlashed) 24.sp else 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .clickable { showFlashed = !showFlashed }
                )
                Text(
                    text = "Back",
                    fontFamily = FontFamily(spaceFamily),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .clickable {
    var intent = intent
                        }
                )
                Text(
                    text = "Not flashed",
                    fontFamily = FontFamily(spaceFamily),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = if (showNotFlashed) 24.sp else 20.sp,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .clickable { showNotFlashed = !showNotFlashed }
                )
            }
        }
    }

    @Composable
    fun RecyclerView(cities: List<City>, activity: Activity) {
        LazyColumn() {
            items(cities)
            { city ->
                CityItem(city = city, onItemClick = { _ ->
                    var intent = Intent(activity, InvadersActivity::class.java)
                    intent.putExtra("city", city.code)
                    if (city.code == "ALL") {
                        var flashlist = activity.intent.getStringExtra("flashfile")
                        intent.putExtra("flashlist", flashlist)
                    }
                    activity.startActivity(intent)
                })
            }
        }
    }
}
