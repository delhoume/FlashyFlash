package com.delhoume.flashyflash.dataclass

data class City(var code: String, var name: String, var country: String,
                var start : Int, var invaders: Int, var flashed : Int,
               var max : Int = invaders)