package uk.co.appsbystudio.geoshare.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class TimeUtilsTest {

    private val lessThanSevenDays = SimpleDateFormat("EEEE", Locale.getDefault())
    private val moreThanSevenDays = SimpleDateFormat("MMM dd", Locale.getDefault())

    @Test
    fun showStringForTime_whenTimeIsLessThan48Hours() {
        assertEquals("Just now", getString(200, MINUTE_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
        assertEquals("a minute ago", getString(200, 2 * MINUTE_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
        assertEquals("49 mins ago", getString(200, 50 * MINUTE_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
        assertEquals("an hour ago", getString(200, 90 * MINUTE_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
        assertEquals("23 hours ago", getString(200, 24 * HOUR_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
        assertEquals("Yesterday", getString(200, 48 * HOUR_MILLIS, Date(0), Date(0), lessThanSevenDays, moreThanSevenDays))
    }

    @Test
    fun showDate_whenTimeIsMoreThan48Hours() {
        assertEquals("Jan 20", getString(200, 200 * HOUR_MILLIS, Date(1548027798000), Date(1562676092358), lessThanSevenDays, moreThanSevenDays))
        assertEquals("Saturday", getString(200, 200 * HOUR_MILLIS, Date(1562452998000), Date(1562676092358), lessThanSevenDays, moreThanSevenDays))
    }

    companion object {
        private const val SECOND_MILLIS: Long = 1000
        private const val MINUTE_MILLIS: Long = 60 * Companion.SECOND_MILLIS
        private const val HOUR_MILLIS: Long = 60 * Companion.MINUTE_MILLIS
    }
}