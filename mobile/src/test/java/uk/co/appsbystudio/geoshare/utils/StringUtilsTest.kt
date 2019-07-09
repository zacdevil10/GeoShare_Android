package uk.co.appsbystudio.geoshare.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {

    @Test
    fun ellipsize() {
        assertEquals("This is...", "This is a test".ellipsize(10))
    }
}