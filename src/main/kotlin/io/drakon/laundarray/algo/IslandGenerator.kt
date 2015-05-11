package io.drakon.laundarray.algo

import io.drakon.laundarray.util.FutureHelper

import org.apache.logging.log4j.LogManager
import java.util.Collections
import java.util.HashSet
import java.util.LinkedList

import java.util.Random
import java.util.concurrent.Future

/**
 * Floating island algorithm imported from Aide.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class IslandGenerator private (var initSeed:Any? = null) {

    companion object {

        private val logger = LogManager.getLogger()

        /**
         * Generates a plain island grid. No dirt etc.
         */
        public fun dumbGenerateIsland(maxHeight:Int, maxTopRadius:Int, seed:Any? = null): Future<Array<Array<Array<Boolean>>>> {
            return FutureHelper.getFuture({
                val gen = IslandGenerator(seed)
                gen.generate(maxHeight, maxTopRadius)
            }, startNow = true)
        }

        /**
         * Generates a Pair<stone,dirt>, where stone and dirt are Sets of BlockCoords.
         */
        public fun generateIslandData(maxHeight:Int, maxTopRadius:Int, seed:Any? = null): Future<Pair<MutableList<BlockCoord>, MutableList<BlockCoord>>> {
            return FutureHelper.getFuture({
                val gen = IslandGenerator(seed)
                val grid = gen.generate(maxHeight, maxTopRadius)
                convertGridToPairs(grid)
            }, startNow = true)
        }

        [deprecated("Just don't use it okay.")]
        public fun getRawGenerator(seed:Any? = null): IslandGenerator {
            logger.warn("Someone is getting a raw generator instance. This isn't recommended!")
            return IslandGenerator(seed)
        }

        private fun convertGridToPairs(grid:Array<Array<Array<Boolean>>>): Pair<MutableList<BlockCoord>, MutableList<BlockCoord>> {
            val stone = LinkedList<BlockCoord>()
            val dirt = LinkedList<BlockCoord>()
            for (n in 0..grid.lastIndex) {
                for (p in 0..grid[n].lastIndex) {
                    val col = grid[n][p]
                    for (x in 0..col.lastIndex-2) if (col[x]) stone.add(BlockCoord(n,x,p))
                    for (x in col.lastIndex-2..col.lastIndex) if (col[x]) dirt.add(BlockCoord(n,x,p))
                }
            }

            Collections.shuffle(stone)
            Collections.shuffle(dirt)

            return Pair(stone, dirt)
        }

        public data class BlockCoord(val x:Int, val y:Int, val z:Int)

    }

    init {
        setSeed(initSeed)
    }
    private var seed = initSeed
    private var rand:Random = Random() // Overriden anyway during init
    private var grid:Array<Array<Array<Boolean>>> = Array(0, {Array(0, {Array(0, {false})})}) // Goddamn null checks

    /**
     * Generate one island.
     *
     * @note Verify that radius >= height, or poor results may be produced.
     *
     * @param maxHeight The islands max height.
     * @param maxTopRadius The islands max radius (but the final radius is usually slightly smaller)
     * @return The 3D grid in form x/y/z, using mathematical axes (x/y is the horizontal plane, z is height) NOT Minecraft axes.
     */
    public fun generate(maxHeight:Int, maxTopRadius:Int): Array<Array<Array<Boolean>>> {
        restartRandom()
        grid = Array(maxTopRadius, {
            Array(maxTopRadius, {
                Array(maxHeight, {false})
            })
        })

        // Find and spawn centre seed block
        val centre:Int = Math.round(maxTopRadius.toDouble()/2).toInt()
        grid[centre][centre][0] = true

        // Calculate max splat size
        val maxSplat:Int = Math.floor(maxTopRadius/(maxHeight*2.0)).toInt()

        // Main loop
        for ( z in 1..maxHeight - 1 ) {
            for ( y in 0..maxTopRadius - 1 ) {
                for ( x in 0..maxTopRadius - 1 ) {
                    generateSplat(x, y, z, maxSplat)
                }
            }
        }

        return grid
    }

    /**
     * Sets the generators seed.
     *
     * @note This will RESET the current Random instance.
     * @note Use a String, Number or no parameter. Using another type will be treated the same as null - A new randomised seed based on systime.
     * @param newSeed The new seed to use.
     */
    public fun setSeed(newSeed:Any? = null) {
        seed = newSeed
        if (seed is String) rand = Random((seed as String).toLong()) // Smart Cast bug :#
        else if (seed is Number) rand = Random((seed as Number).toLong())
        else rand = Random()
    }

    private fun restartRandom() {
        setSeed(seed)
    }

    private fun generateSplat(x:Int, y:Int, z:Int, maxSize:Int) {
        // Check if block underneath
        if (!grid[x][y][z-1]) return

        // Splat core
        grid[x][y][z] = true

        var delta_x_pos = 0
        var delta_x_neg = 0
        var delta_y_pos = 0
        var delta_y_neg = 0

        for (_ in 0..maxSize*2) {
            val r = rand.nextInt(4)
            when (r) {
                0 -> {
                    // +x
                    delta_x_pos += 1
                    if (x+delta_x_pos < grid.lastIndex)
                        grid[x+delta_x_pos][y][z] = true}
                1 -> {
                    // +y
                    delta_y_pos += 1
                    if (y+delta_y_pos < grid[x].lastIndex)
                        grid[x][y+delta_y_pos][z] = true}
                2 -> {
                    // -x
                    delta_x_neg += 1
                    if (x-delta_x_neg > 0)
                        grid[x-delta_x_neg][y][z] = true}
                3 -> {
                    // -y
                    delta_y_neg += 1
                    if (y-delta_y_neg > 0)
                        grid[x][y-delta_y_neg][z] = true}
                else -> {
                    // Whut
                    val e = IllegalStateException("Impossible value of r: " + r)
                    logger.warn(e.getMessage() + " at: \n" + e.getStackTrace())}
            }
        }
    }

}