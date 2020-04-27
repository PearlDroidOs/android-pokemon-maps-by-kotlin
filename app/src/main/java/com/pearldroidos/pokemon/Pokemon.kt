package com.pearldroidos.pokemon

import android.graphics.Bitmap
import android.location.Location


data class Pokemon(val name: String, val des: String, val bitmap: Bitmap, val power: Double,
    val lat: Double, val log: Double, var isCatch: Boolean = false)


