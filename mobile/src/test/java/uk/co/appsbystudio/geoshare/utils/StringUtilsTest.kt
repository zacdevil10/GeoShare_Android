package uk.co.appsbystudio.geoshare.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {

    @Test
    fun ellipsize() {
        assertEquals("This is...", "This is a test.".ellipsize(10))
        assertEquals("It doesn't...", "It doesn't matter how long the string is. It will always end up the same length.".ellipsize(13))
        assertEquals("It...", "It doesn't matter how long the string is. It will always end up the same length. Unless a word would be cut off in which case the word is completely removed.".ellipsize(10))
    }
}