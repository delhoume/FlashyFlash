package com.delhoume.flashbattle

import android.app.Activity
import android.content.Intent
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

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delhoume.flashbattle.dataclass.InvadersList
import com.delhoume.flashbattle.util.copyAssetFile
import com.delhoume.flashbattle.util.getListsFolder
import com.delhoume.flashbattle.flashfile.FlashFile
import java.io.File
import kotlin.random.Random


class ListsActivity : ComponentActivity() {
    var flashlists: MutableList<InvadersList> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t = this;
        copyAssetFile(assets, "lists/flashed.txt")
        File(getListsFolder(), "myflashlist.txt").createNewFile()
        val files = getListsFolder().listFiles()
        files?.forEach {
            flashlists.add(
                InvadersList(
                    it.nameWithoutExtension,
                    it.readText()
                )
            )
        }

        setContent {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Black)
            )
            {
                TopActions()
                RecyclerView(flashlists = flashlists, activity = t)
            }
        }
    }

    @Composable
    fun ListItem(flashlist: InvadersList, onItemClick: (InvadersList) -> Unit) {
        var ff = FlashFile()
        var ntokens = ff.decodeString(flashlist.flashlist).size
        Surface(
            color = Color.Black,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        {
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick(flashlist) }

            )
            {
                Row(modifier = Modifier.padding(10.dp))
                {
                    Text(
                        text = flashlist.name,
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 30.sp
                    )
                    Text(
                        text = ("($ntokens A)"),
                        fontFamily = FontFamily(pixelFamily),
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    Text(
                        text = "edit", //  pen
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "delete", // trashcan
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "fight",  // epée
                        fontFamily = FontFamily(spaceFamily),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    @Composable
    fun TopActions() {
        Surface(
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        {
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "New",
                    fontFamily = FontFamily(spaceFamily),
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).wrapContentHeight()
                )
                Text(
                    text = "Import",
                    fontFamily = FontFamily(spaceFamily),
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).wrapContentHeight()
                )
            }
        }
    }

    @Composable
    fun RecyclerView(flashlists: List<InvadersList>, activity: Activity) {
        LazyColumn() {
            items(flashlists)
            { flashlist ->
                ListItem(flashlist = flashlist,
                    onItemClick = { selectedList ->
                        var intent = Intent(activity,InvadersActivity::class.java)
                        intent.putExtra("flashfile", selectedList.flashlist)
                        activity.startActivity(intent)
                    })
            }
        }
    }

    val invaders = "abcdefghjklmnlopqrstuvwxyzABCDEFGHJKLMNLOPQRSTUVWXYZ"
    fun RandomInvader(): String {
        val index = Random.nextInt(invaders.length)
        return invaders.substring(index, index + 1)
    }

    @Composable
    fun RandomInvaderText(text: String) {
        Text(text = text)
    }
}
