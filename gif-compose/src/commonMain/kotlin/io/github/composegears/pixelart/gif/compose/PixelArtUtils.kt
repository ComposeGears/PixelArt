package io.github.composegears.pixelart.gif.compose

import com.shakster.gifkt.ImageFrame

/**
 * Extracts the logical (downscaled) pixel grid from an [ImageFrame] that contains *upscaled* pixel art,
 * where every logical pixel was rendered as a square block of side length [pixelSize].
 *
 * This is a convenience extension that delegates to the array-based [extractLogicalPixels] overload.
 *
 * Preconditions (validated with `require`):
 * - `pixelSize > 0`
 * - `width % pixelSize == 0 && height % pixelSize == 0`
 *
 * Typical scenario: you decoded a GIF whose original low-resolution pixel art was enlarged using
 * nearest-neighbour scaling to avoid blurring. This function collapses it back to its compact
 * logical dimension while choosing a representative color per block using [strategy].
 *
 * Example:
 * ```kotlin
 * val frame: ImageFrame = ... // 320x240 (each logical pixel blown up 4x)
 * val reduced = frame.extractLogicalPixels(pixelSize = 4)
 * println("${'$'}{reduced.width} x ${'$'}{reduced.height}") // 80 x 60
 * ```
 *
 * @param pixelSize Side length (in physical pixels) of each square block representing one logical pixel.
 * @param strategy Strategy used to derive a representative color for a block (default: [BlockColorStrategy.First]).
 * @return A [PixelArtReduced] containing one ARGB value per logical pixel.
 *
 * @throws IllegalArgumentException if preconditions are violated.
 *
 * @see extractLogicalPixels
 * @see BlockColorStrategy
 */
fun ImageFrame.extractLogicalPixels(
    pixelSize: Int,
    strategy: BlockColorStrategy = BlockColorStrategy.First
): PixelArtReduced = extractLogicalPixels(
    argb = argb,
    width = width,
    height = height,
    pixelSize = pixelSize,
    strategy = strategy
)

/**
 * Collapses a high-resolution (visually upscaled) pixel-art surface into its logical pixel grid by visiting
 * each non-overlapping `pixelSize x pixelSize` block and computing a single representative color defined by [strategy].
 *
 * Rationale: Upscaled pixel-art (e.g. to avoid filtering artifacts in GIFs / spritesheets) inflates memory, bandwidth
 * and processing costs. Reducing it back to the logical grid can make subsequent processing (palette analysis,
 * diffing, compression) more efficient.
 *
 * Validation:
 * - Throws [IllegalArgumentException] if `pixelSize <= 0`.
 * - Throws [IllegalArgumentException] if `width` or `height` is not divisible by `pixelSize`.
 *
 * Strategies:
 * - [BlockColorStrategy.First]         – Use the top‑left pixel (fastest; zero extra allocations).
 * - [BlockColorStrategy.Average]       – Arithmetic mean of channels (integer division; rounds toward zero; alpha treated non‑premultiplied).
 * - [BlockColorStrategy.MostFrequent]  – Modal color (palette fidelity; creates & clears a small `HashMap` per block).
 *
 * Complexity (N = width * height input pixels):
 * - All strategies run in O(N); Average & MostFrequent have a larger constant factor proportional to `pixelSize^2` per output pixel.
 *
 * Memory:
 * - Allocates exactly one `IntArray` of size `(width / pixelSize) * (height / pixelSize)` for the result.
 * - `MostFrequent` allocates/uses a small temporary `HashMap` per block (capacity ≈ `pixelSize^2`).
 *
 * Color averaging details:
 * - Simple integer mean; each channel is summed then divided by the number of samples.
 * - No gamma correction or alpha weighting is performed.
 *
 * Modal selection details:
 * - First color reaching the highest frequency wins; ties prefer the earliest encountered color (implicit in iteration order / map traversal).
 *
 * Example:
 * ```kotlin
 * val reduced = extractLogicalPixels(src, width = 320, height = 240, pixelSize = 4)
 * // reduced.width == 80, reduced.height == 60
 * ```
 *
 * @param argb Source ARGB pixels in row-major order; length must equal `width * height`.
 * @param width Source width in *physical* (upscaled) pixels.
 * @param height Source height in *physical* (upscaled) pixels.
 * @param pixelSize Side length of each logical pixel block (must evenly divide width & height).
 * @param strategy Color selection strategy for each block.
 * @return [PixelArtReduced] containing reduced ARGB values and new logical dimensions.
 *
 * @throws IllegalArgumentException if `pixelSize <= 0` or dimensions not divisible by `pixelSize`.
 */
@Suppress("CyclomaticComplexMethod", "CognitiveComplexMethod", "NestedBlockDepth")
fun extractLogicalPixels(
    argb: IntArray,
    width: Int,
    height: Int,
    pixelSize: Int,
    strategy: BlockColorStrategy = BlockColorStrategy.First
): PixelArtReduced {
    require(pixelSize > 0) { "pixelSize must be > 0" }
    require(width % pixelSize == 0 && height % pixelSize == 0) {
        "width ($width) and height ($height) must be divisible by pixelSize ($pixelSize)"
    }

    val outW = width / pixelSize
    val outH = height / pixelSize
    val out = IntArray(outW * outH)

    when (strategy) {
        BlockColorStrategy.First -> { // O(N)
            var outIndex = 0
            var y = 0
            while (y < height) {
                var x = 0
                val rowBase = y * width
                while (x < width) {
                    out[outIndex++] = argb[rowBase + x] // top-left of block
                    x += pixelSize
                }
                y += pixelSize
            }
        }
        BlockColorStrategy.Average -> { // O(N) with larger constant (sums each block)
            var outIndex = 0
            var by = 0
            while (by < height) {
                var bx = 0
                while (bx < width) {
                    var aSum = 0
                    var rSum = 0
                    var gSum = 0
                    var bSum = 0
                    var yy = 0
                    while (yy < pixelSize) {
                        val rowStart = (by + yy) * width + bx
                        var xx = 0
                        while (xx < pixelSize) {
                            val c = argb[rowStart + xx]
                            aSum += c ushr 24 and 0xFF
                            rSum += c ushr 16 and 0xFF
                            gSum += c ushr 8 and 0xFF
                            bSum += c and 0xFF
                            xx++
                        }
                        yy++
                    }
                    val count = pixelSize * pixelSize
                    val a = aSum / count and 0xFF
                    val r = rSum / count and 0xFF
                    val g = gSum / count and 0xFF
                    val b = bSum / count and 0xFF
                    out[outIndex++] = a shl 24 or (r shl 16) or (g shl 8) or b
                    bx += pixelSize
                }
                by += pixelSize
            }
        }
        BlockColorStrategy.MostFrequent -> { // O(N) with hashmap per block
            val freq = HashMap<Int, Int>(pixelSize * pixelSize)
            var outIndex = 0
            var by = 0
            while (by < height) {
                var bx = 0
                while (bx < width) {
                    freq.clear()
                    var yy = 0
                    while (yy < pixelSize) {
                        val rowStart = (by + yy) * width + bx
                        var xx = 0
                        while (xx < pixelSize) {
                            val c = argb[rowStart + xx]
                            freq[c] = (freq[c] ?: 0) + 1
                            xx++
                        }
                        yy++
                    }
                    var bestColor = 0
                    var bestCount = -1
                    for ((color, count) in freq) {
                        if (count > bestCount) {
                            bestCount = count
                            bestColor = color
                        }
                    }
                    out[outIndex++] = bestColor
                    bx += pixelSize
                }
                by += pixelSize
            }
        }
    }

    return PixelArtReduced(
        argb = out,
        width = outW,
        height = outH
    )
}

/**
 * Result of pixel‑art logical reduction.
 *
 * Equality / hashCode are value-based and compare array contents (not reference).
 *
 * @property argb One ARGB int per logical pixel (row-major order).
 * @property width Logical pixel width (originalWidth / pixelSize).
 * @property height Logical pixel height (originalHeight / pixelSize).
 */
data class PixelArtReduced(
    val argb: IntArray,
    val width: Int,
    val height: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PixelArtReduced

        if (width != other.width) return false
        if (height != other.height) return false
        if (!argb.contentEquals(other.argb)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + argb.contentHashCode()
        return result
    }
}

/**
 * Strategy used to derive a representative color for a pixel block.
 */
enum class BlockColorStrategy {
    /** Use the top-left pixel of the block; fastest; zero extra allocations. */
    First,

    /** Arithmetic mean of each 8-bit channel; integer division truncates; alpha treated non-premultiplied. */
    Average,

    /** Modal color of the block; preserves palette edges; uses a small temporary HashMap per block. */
    MostFrequent
}
