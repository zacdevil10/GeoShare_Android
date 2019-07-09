package uk.co.appsbystudio.geoshare.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.math.round

class LocationUtilsTest {

    @Test
    fun convertDistanceToMeters_whenLessThan1000() {
        assertEquals("573 M", 573f.distanceConverter())
        assertEquals("999 M", 999.99f.distanceConverter())
    }

    @Test
    fun convertDistanceToKM_whenGreaterThan1000() {
        assertEquals("5.73 KM", 5730f.distanceConverter())
        assertEquals("1.0 KM", round(999.99f).distanceConverter())
        assertEquals("5.73 KM", round(5730f).distanceConverter())
        assertNotEquals("6 KM", round(5730f).distanceConverter())
    }
}