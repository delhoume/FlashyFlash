package com.delhoume.flashyflash.util


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.delhoume.flashyflash.dataclass.SpaceInvader
import java.io.InputStream
import java.util.Locale
import kotlin.math.floor

private val TAG: String = "ResourceManager"

class ResourceManager private constructor() {
    companion object {
        @Volatile
        private var instance: ResourceManager? = null

        fun getInstance(): ResourceManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ResourceManager()
                    }
                }
            }
            return instance!!
        }
    }

    var cache = HashMap<String, Any>()
    var default_bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    lateinit var the_activity: Activity

    fun init(activity: Activity) {
        the_activity = activity
    }

    fun get(name: String): Any? {
        var res: Any? = cache.get(name)
        if (res != null) return res

        var parts = name.split(":")
        var type = parts[1]
        var ins: InputStream? = openResource(name)
        if (ins != null)
            when (type) {
                "bitmap" -> {
                    var result = createBitmap(ins)
                    if (result != null) {
                        cache.put(name, result)
                        return result
                    }
                }
            }
        return null
    }

    fun createBitmap(ins: InputStream): Bitmap? {
        return BitmapFactory.decodeStream(ins)
    }

    // "asset:bitmap:atlas:PA_00.jpg"
    fun openResource(name: String): InputStream? {
        var parts = name.split(":")
        var src = parts[0]
        when (src) {
            "asset" -> {
                return openAsset(parts[2], parts[3])
            }

            "user" -> {
                return openUser(parts[2], parts[3])
            }
        }
        return null
    }

    fun openUser(folder: String, name: String): InputStream? {
        return null
    }

    fun openAsset(folder: String, name: String): InputStream? {
        val list: Array<String>? = the_activity.assets.list(folder)
        if ((list != null) && list.contains(name)) {
            var inputstream = the_activity.assets.open(folder + "/" + name)
            return inputstream
        }
        return null
    }

    fun loadAsset(folder: String, name: String): Bitmap {
        val list: Array<String>? = the_activity.assets.list(folder)
        if ((list != null) && list.contains(name)) {
            var inputstream = the_activity.assets.open(folder + "/" + name)
            return BitmapFactory.decodeStream(inputstream)
        }
        return default_bitmap
    }
}

// thumbnails from flashinvaders are not included, you have to build thrm
// it is easy so ask me how
var use_generic_thumbnail= true

// atlas retrieval
fun getFinalThumbnail(invader: SpaceInvader): Bitmap {
    val cityInfo = CityInfo.getInstance()
    var number = cityInfo.getSINumberFromSICode(invader.code)
    val city_code = cityInfo.getCityCodeFromSICode(invader.code)
    var city = cityInfo.getCityByCode(city_code)
    var atlasnum = floor(number / 400.0).toInt()
    if (atlasnum < 0) atlasnum = 0
    var atlasresource =
        "asset:bitmap:atlas:${city_code.uppercase(Locale.getDefault())}_0${atlasnum.toString()}.jpg"
    if (use_generic_thumbnail) {
        atlasresource =
            "asset:bitmap:atlas:GENERIC_00.jpg"
    }
    var mgr = ResourceManager.getInstance()
    var atlasbitmap = mgr.get(atlasresource) as Bitmap

    if (use_generic_thumbnail) {
        number = number % 23
    } else {
        if (number >= city.invaders) { // we have unknown  thumbnails, use default
            return mgr.get("asset:bitmap:atlas:missing_invader.jpg") as Bitmap
        }
    }
    val x = number % 20
    val y = ((number / 20).toInt()) % 20
    var bitmap = Bitmap.createBitmap(atlasbitmap, x * 100, y * 100, 100, 100)
    return bitmap
}
