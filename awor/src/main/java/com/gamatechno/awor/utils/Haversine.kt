package com.gamatechno.awor.utils

import kotlin.math.*

object Haversine {
    private const val EARTH_RADIUS = 6371 // Approx Earth radius in KM

    fun distance(
        startLat: Double, startLong: Double,
        endLat: Double, endLong: Double
    ): Double {
        var sLat = startLat
        var eLat = endLat
        val dLat = Math.toRadians(eLat - sLat)
        val dLong = Math.toRadians(endLong - startLong)
        sLat = Math.toRadians(sLat)
        eLat = Math.toRadians(eLat)
        val a =
            haversin(dLat) + cos(sLat) * cos(eLat) * haversin(
                dLong
            )
        val c =
            2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c // <-- d
    } //jarak dalam kilometer

    private fun haversin(`val`: Double): Double {
        return sin(`val` / 2).pow(2.0)
    }
}