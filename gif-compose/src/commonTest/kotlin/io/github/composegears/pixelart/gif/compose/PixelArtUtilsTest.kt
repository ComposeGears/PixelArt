package io.github.composegears.pixelart.gif.compose

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PixelArtUtilsTest {

    @Test
    fun `reduces with first strategy picking top-left pixel`() {
        // 4x4 source, pixelSize=2 -> 2x2 logical
        val c1 = argb(r = 10, g = 20, b = 30)
        val c2 = argb(r = 40, g = 50, b = 60)
        val c3 = argb(r = 70, g = 80, b = 90)
        val c4 = argb(r = 100, g = 110, b = 120)
        val filler = argb(r = 0, g = 0, b = 0)

        val src = intArrayOf(
            // y = 0
            c1, filler, c2, filler,
            // y = 1
            filler, filler, filler, filler,
            // y = 2
            c3, filler, c4, filler,
            // y = 3
            filler, filler, filler, filler
        )

        val reduced = extractLogicalPixels(
            src,
            width = 4,
            height = 4,
            pixelSize = 2,
            strategy = BlockColorStrategy.First
        )

        assertEquals(2, reduced.width)
        assertEquals(2, reduced.height)
        reduced.argb.assertContentEquals(intArrayOf(c1, c2, c3, c4))
    }

    @Test
    fun `reduces with average strategy computes channel means`() {
        // Single 2x2 block -> 1x1 output
        val c1 = argb(r = 10, g = 20, b = 30)
        val c2 = argb(r = 30, g = 20, b = 10)
        val c3 = argb(r = 50, g = 60, b = 70)
        val c4 = argb(r = 90, g = 110, b = 130)
        val src = intArrayOf(
            c1,
            c2,
            c3,
            c4
        )
        // Averages:
        // R: (10+30+50+90)/4 = 45
        // G: (20+20+60+110)/4 = 52
        // B: (30+10+70+130)/4 = 60
        val expected = argb(r = 45, g = 52, b = 60)

        val reduced = extractLogicalPixels(
            src,
            width = 2,
            height = 2,
            pixelSize = 2,
            strategy = BlockColorStrategy.Average
        )

        assertEquals(1, reduced.width)
        assertEquals(1, reduced.height)
        reduced.argb.assertContentEquals(intArrayOf(expected))
    }

    @Test
    fun `reduces with most frequent strategy picks modal color`() {
        // 2x2 block where red appears twice (mode)
        val red = argb(r = 255, g = 0, b = 0)
        val blue = argb(r = 0, g = 0, b = 255)
        val green = argb(r = 0, g = 255, b = 0)
        val src = intArrayOf(
            red,
            red,
            blue,
            green
        )
        val reduced = extractLogicalPixels(
            src,
            width = 2,
            height = 2,
            pixelSize = 2,
            strategy = BlockColorStrategy.MostFrequent
        )
        assertEquals(1, reduced.width)
        assertEquals(1, reduced.height)
        reduced.argb.assertContentEquals(intArrayOf(red))
    }

    @Test
    fun `pixel size one returns identity array`() {
        val src = intArrayOf(
            argb(r = 1, g = 2, b = 3),
            argb(r = 4, g = 5, b = 6),
            argb(r = 7, g = 8, b = 9),
            argb(r = 10, g = 11, b = 12)
        )
        val reduced = extractLogicalPixels(
            src,
            width = 2,
            height = 2,
            pixelSize = 1
        )
        assertEquals(2, reduced.width)
        assertEquals(2, reduced.height)
        reduced.argb.assertContentEquals(src)
    }

    @Test
    fun `invalid dimensions not divisible throw illegal argument`() {
        val src = IntArray(6) // width=3, height=2
        assertFailsWith<IllegalArgumentException> {
            extractLogicalPixels(
                src,
                width = 3,
                height = 2,
                pixelSize = 2
            )
        }
    }

    @Test
    fun `pixel size zero throw illegal argument`() {
        val src = IntArray(4)
        assertFailsWith<IllegalArgumentException> {
            extractLogicalPixels(
                src,
                width = 2,
                height = 2,
                pixelSize = 0
            )
        }
    }
}

private fun argb(a: Int = 255, r: Int, g: Int, b: Int): Int =
    a shl 24 or (r shl 16) or (g shl 8) or b

private fun IntArray.assertContentEquals(other: IntArray) {
    assertTrue(
        actual = contentEquals(other),
        message = "Expected ${other.joinToString()} but was ${joinToString()}"
    )
}
