package com.delhoume.flashbattle

import AutoSizeText
import FontSizeRange
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.delhoume.flashbattle.dataclass.SpaceInvader
import com.delhoume.flashbattle.flashfile.FlashFile
import com.delhoume.flashbattle.flashfile.printInvaderNumber
import com.delhoume.flashbattle.util.CityInfo
import com.delhoume.flashbattle.util.ListFromFlashfile
import com.delhoume.flashbattle.util.ResourceManager
import com.delhoume.flashbattle.util.computeCitiesFlashed
import com.delhoume.flashbattle.util.computeStats
import com.delhoume.flashbattle.util.copyInvaders
import com.delhoume.flashbattle.util.getFinalThumbnail
import com.delhoume.flashbattle.util.getSpaceInvaderFromCode
import com.delhoume.flashbattle.util.readUserFlash
import com.delhoume.flashbattle.util.writeUserflashContents
import com.lightspark.composeqr.QrCodeView
import greyScale
import kotlinx.coroutines.launch
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.floor


private val TAG: String = "InvadersActivity"

var currentlistfile: String = "myflashes.txt"

var all_invaders: MutableList<SpaceInvader> = ArrayList()
var flashed_invaders: MutableList<SpaceInvader> = ArrayList()

var flashed_cities_count: MutableState<Int> = mutableIntStateOf(0)
var flashed_invaders_count: MutableState<Int> = mutableIntStateOf(0)


@OptIn(ExperimentalFoundationApi::class)
class InvadersActivity : ComponentActivity() {
    lateinit var t: InvadersActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        t = this
        ResourceManager.getInstance().init(activity = this)
        CityInfo.getInstance()

        //strInput = readFromDownloads(this, currentlistfile)
        var strInput: String = readUserFlash(this, currentlistfile)
        if (strInput.isEmpty())
            strInput = "PA_1500 POTI_01 BAB_01+9 VRS_12"

        setContent {
            //AppTheme {
            val srcfilenamr = "myflashes.txt"
            var contents: String = readUserFlash(this, "myflashes.txt")

            var inputState = remember { mutableStateOf(contents) }
            var pgstate = rememberPagerState { 2 }
            var invaders = ListFromFlashfile(inputState.value)
            HorizontalPager(state = pgstate) { page ->
                when (page) {
                    0 -> {
                        OpenFlashFile(invaders)
                    }

                    1 -> {
                        ExportPage(invaders = invaders, state = inputState)
                    }
                }
            }
        }
    }
}

@Composable
fun OpenFlashFile(invaders: List<SpaceInvader>) {
    var flashed_invaders = invaders.filter { invader ->
        invader.isFlashedCity() || invader.flashed.value
    }
    InvadersEntryPoint(invaders, flashed_invaders)
}

@Composable
fun ExportPage(invaders: List<SpaceInvader>, state: MutableState<String>) {
    var flashfile = getFlashFileContents(invaders)
    var flatlist = getFlatFileContents(invaders)
    var context: Context = LocalContext.current
    var contentResolver = context.contentResolver
    var showSingleFilePicker by remember { mutableStateOf(false) }
    var pathSingleChosen by remember { mutableStateOf("") }
    // write ToDownloads(this, "myflashes.txt", flashfile)
    // writeToDownloads(this, "myflashes_flat.txt", flatfile)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.White)
                .size(350.dp),
            contentAlignment = Alignment.Center
        ) {
            var encodedstr = encode(flashfile)
            Spacer(modifier = Modifier.height(30.dp))
            QrCodeView(
                data = "http://vliv.freeboxos.fr:52000/flashfile.html?content=$encodedstr",
                modifier = Modifier.size(330.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
        ) {
            StandardText(
                text = "Copy optimized FlashFile list to clipboard",
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .clickable {
                        CopyToClipboard(flashfile, context)
                    }

            )
            StandardText(
                text = "Copy full list to clipboard",
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize()
                    .clickable {
                        CopyToClipboard(flatlist, context)
                    }
            )
            StandardText(
                text = "Import QRCode",
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth()
                    .clickable {
                        var intent = Intent(context, QRCodeActivity::class.java)
//                        startActivity
                        state.value = "PA_1500"
                    }
            )

            if (showSingleFilePicker) {
                val fileType = listOf("txt")
                FilePicker(showSingleFilePicker, fileExtensions = fileType) { pfile ->
                    if (pfile != null) {
//                        Uri.parse(pfile.platformFile as String?)
                        state.value = "PA_1000"
                    }
                    showSingleFilePicker = false
                }

            }
            Text("File Chosen: $pathSingleChosen")

            StandardText(
                text = "Import FlashFile from Storage",
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxSize()
                    .clickable {
                        showSingleFilePicker = true
                    }
            )
        }
    }
}

fun CopyToClipboard(text: String, context: Context) {
    val clipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Space Invaders", text)
    clipboardManager.setPrimaryClip(clip)
    Toast
        .makeText(context, "Done", Toast.LENGTH_SHORT)
        .show()
}

fun getFlashFileContents(invaders: List<SpaceInvader>): String {
    var flashed = invaders.filter { !it.isCity() && it.flashed.value }
    var ff = FlashFile()
    var tokens: MutableList<String> = ArrayList()
    flashed.forEach { tokens.add(it.code) }
    var tokensout = ff.encode(tokens, false, true)
    return tokensout.fastJoinToString(" ")
}

fun getFlatFileContents(invaders: List<SpaceInvader>): String {
    var flashed = invaders.filter { !it.isCity() && it.flashed.value }
    var tokens: MutableList<String> = ArrayList()
    flashed.forEach { tokens.add(it.code) }
    return tokens.fastJoinToString(" ")
}

@Composable
fun InvadersEntryPoint(invaders: List<SpaceInvader>, flashed: List<SpaceInvader>) {
    var editMode = rememberSaveable { mutableStateOf(false) }
    var displayMode = rememberSaveable { mutableIntStateOf(0) }
    var trigger = rememberSaveable { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .offset(0.dp, 0.dp)
            .fillMaxSize()
            .background(Color.Black)
    ) {
    //    trigger.value = !trigger.value
        var flashed = copyInvaders(invaders).filter { invader ->
            invader.flashed.value
        }
        var cities = copyInvaders(flashed).filter { invader -> invader.isCity() }

            NormalHeader(editMode, invaders, displayMode)
        if (editMode.value) {
            InvaderGrid(
                invaders = invaders,
                editMode = editMode,
                displayMode = displayMode,
                modifier = Modifier
            )
        } else {
            InvaderGrid(
                invaders = if (displayMode.intValue == 0) flashed else cities,
                editMode = editMode,
                displayMode = displayMode,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun NormalHeader(
    editMode: MutableState<Boolean>,
    invaders: List<SpaceInvader>,
    displayMode: MutableState<Int>
) {
    var context: Context = LocalContext.current
    flashed_invaders_count.value = computeStats(invaders)
    flashed_cities_count.value = computeCitiesFlashed(invaders)
    Row(
        Modifier
            .height(80.dp)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        StandardText(
            text = "${flashed_invaders_count.value} Flashes",
            modifier = Modifier.weight((2.0f))
        )
        FancyToggle(
                text = "${flashed_cities_count.value} Cities",
            state = displayMode.value == 1,
        modifier = Modifier.weight(2.0f)
            .clickable {
                if (displayMode.value == 1)
                    displayMode.value = 0
                else
                    displayMode.value += 1
            }
        )
        Spacer(
            modifier = Modifier
                .size(5.dp)
                .weight(1.0f)
        )
        FancyToggle(
            text = "edit",
            state = editMode.value,
            modifier = Modifier
                .weight(2.0f)
                .clickable {
                    editMode.value = !editMode.value
                    if (editMode.value == false) {
                        writeUserflashContents(
                            context,
                            currentlistfile,
                            getFlashFileContents(invaders)
                        )
                        flashed_invaders_count.value = computeStats(invaders)
                        flashed_cities_count.value = computeCitiesFlashed(invaders)
                    }
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InvaderGrid(
    invaders: List<SpaceInvader>,
    editMode: MutableState<Boolean>,
    displayMode: MutableState<Int>,
    modifier: Modifier
) {
    var state = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    LazyVerticalGridScrollbar(
        state = state,
        settings = ScrollbarSettings(
            selectionMode = ScrollbarSelectionMode.Thumb,
            enabled = true,
            alwaysShowScrollbar = false,
            hideDelayMillis = 2000
        ),

        indicatorContent = { index, isThumbSelected ->
            var si = invaders[index]
            val cities = CityInfo.getInstance()
            var city = cities.getCityByCode(cities.getCityCodeFromSICode(si.code))
            Indicator(
                text = city.name.uppercase(),
                isThumbSelected = isThumbSelected
            )
        }
    )
    {
        LazyVerticalGrid(
            state = state,
            columns = GridCells.Adaptive(minSize = 80.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            var all_cities = CityInfo.getInstance()

            items(invaders, key = { it.code }) { invader ->
                Box(
                    Modifier
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = {
                                if (editMode.value == true) {
                                    if (invader.isCity()) {
                                        var city = all_cities.getCityByCode(
                                            all_cities.getCityCodeFromSICode(
                                                invader.code
                                            )
                                        )
                                        var flashed = city.flashed > 0
                                        for (si in 0 until city.max) {
                                            var sicode = "${city.code}_${
                                                printInvaderNumber(
                                                    si + city.start,
                                                    true
                                                )
                                            }"
                                            var si = getSpaceInvaderFromCode(invaders, sicode)
                                            if (si != null)
                                                si.flashed.value = !flashed
                                        }
                                        // itself
                                        invader.flashed.value = !flashed
                                    } else {
                                        invader.swapFlash()
                                    }
                                    flashed_invaders_count.value = computeStats(invaders)
                                    flashed_cities_count.value = computeCitiesFlashed(invaders)
                                }
                            },
                            onLongClick = {
                                // TODO
                                Log.i(TAG, "finding next city")
                                // next flashed city
                                var sicode = invader.code
                                all_cities.getCityByCode(
                                    (all_cities.getCityCodeFromSICode(sicode))
                                )

                                var idx: Int = 0
                                while (invaders[idx].code != invader.code) idx++
                                Log.i(TAG, "found current at  $idx")
                                ++idx
                                while (idx < invaders.size) {
                                    if (invaders[idx].isFlashedCity())
                                            break
                                    idx++
                                }
                                if (idx < invaders.size)
                                    Log.i(
                                        TAG,
                                        "current ${invader.code}, next ${invaders[idx].code}"
                                    )
                                else
                                    Log.i(TAG, "not found")
                                coroutineScope.launch {
                                    state.animateScrollToItem(index = idx)
                                }
                            }

                        )
                ) {
                    val cityInfo = CityInfo.getInstance()
                    val city_code = cityInfo.getCityCodeFromSICode(invader.code)
                    var rightPart = cityInfo.getRighPartFromSICode((invader.code))
                    if (rightPart != city_code) {
                        InvaderThumbnail(invader = invader)
                    } else {
                        CityThumbnail(invader = invader)
                    }
                    //  InvaderItem(invader = invader, selected = true, inSelectionMode = false, Modifier)
                }
            }
        }
    }
}


@Composable
fun InvaderThumbnail(invader: SpaceInvader) {
    Image(
        bitmap = getFinalThumbnail(invader).asImageBitmap(),
        contentDescription = "",
        modifier = if (invader.flashed.value)
            Modifier.fillMaxSize() else Modifier
            .fillMaxSize()
            .greyScale()
    )
    Text(
        text = invader.code,
        color = if (invader.flashed.value) Color.White else Color.Gray,
        fontFamily = FontFamily(spaceFamily),
        fontSize = 12.sp,
        modifier = Modifier
            .drawBehind {
                drawRect(
                    Color(0xaa222222)
                )
            }
    )
}

@Composable
fun CityThumbnail(invader: SpaceInvader) {
    var citycode = invader.code.split("_")[0]
    var city = CityInfo.getInstance().getCityByCode(citycode)
    var bgcolor = Color(0xff222222)
    if (invader.flashed.value) bgcolor = Color(0xff8899ff)
    var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    if (city.country != "SPACE")
        bitmap =
            ResourceManager.getInstance().loadAsset("flags", "${city.country}.png")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgcolor),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(20.dp, 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "",
                    modifier = if (invader.flashed.value)
                        Modifier else Modifier.greyScale()
                )
            }
            Row {
                Column {
                    var flashedcounttext = "${city.flashed} / ${city.max}"
                    AutoSizeText(
                        text = city.name.uppercase(),
                        color = Color.White,
                        fontFamily = FontFamily(spaceFamily),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        fontSizeRange = FontSizeRange(
                            min = 8.sp,
                            max = 12.sp,
                        ),
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = flashedcounttext,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}


@Composable
fun BaseText(text: String, color: Color, modifier: Modifier) {
    Text(
        text = text,
        fontFamily = FontFamily(spaceFamily),
        color = color,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        modifier = modifier
    )
}

@Composable
fun StandardText(text: String, modifier: Modifier) {
    BaseText(
        text = text,
        color = Color.White,
        modifier = modifier.drawBehind { drawRect(Color.Black) })
}

@Composable
fun HighlightedText(text: String, modifier: Modifier) {
    BaseText(
        text = text,
        color = Color.Black,
        modifier = modifier.drawBehind { drawRect(Color.White) })
}

@Composable
fun FancyToggle(text: String, modifier: Modifier, state: Boolean) {
    if (state == true) HighlightedText(text = text, modifier = modifier)
    else StandardText(text = text, modifier = modifier)
}

@Composable
fun Indicator(text: String, isThumbSelected: Boolean) {
    Surface {
        Text(
            text = text,
            color = Color.White,
            fontFamily = FontFamily(spaceFamily),
            fontSize = 16.sp,
            modifier = Modifier
                .background(Color.Black)
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 10.dp,
//                        bottomStart = 10.dp,
//                        bottomEnd = 10.dp,
//                        topEnd = 10.dp
//                    )
//                )
        )
    }
}


fun encode(url: String) = URLEncoder.encode(url, "UTF-8")


