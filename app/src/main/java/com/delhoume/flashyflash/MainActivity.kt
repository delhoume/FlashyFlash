package com.delhoume.flashyflash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.delhoume.flashyflash.ui.theme.spaceFamily
import com.delhoume.flashyflash.util.CityInfo


//  this is the splashscreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CityInfo.getInstance().init(assets.open("cities.json"))
        enableEdgeToEdge()
        setContent {
            Column(
                Modifier
                    .clickable(onClick = {
                        var intent = Intent(this, InvadersActivity::class.java)
                        startActivity(intent)
                    })
                    .fillMaxSize()
                    .background(Color.Black)
                //               contentAlignment = Alignment.Center,  contentAlignment = Alignment.Center
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .wrapContentSize(align = Alignment.Center)
                ) {

                    Text(
                        text = getString(R.string.app_name),
                        fontFamily = FontFamily(spaceFamily),
                        fontSize = 36.sp,
                        color = Color.White,
                    )
                    Text(
                        text = "delhoume@gmail.com",
                        fontFamily = FontFamily(spaceFamily),
                        fontSize = 12.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
