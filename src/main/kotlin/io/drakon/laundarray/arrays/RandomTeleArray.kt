package io.drakon.laundarray.arrays

import com.pahimar.ee3.api.array.AlchemyArray
import io.drakon.laundarray.Laundarray
import io.drakon.laundarray.lib.Config.Arrays.RandomTeleport as rtConf
import io.drakon.laundarray.lib.Reference
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ChatComponentText
import net.minecraft.world.World
import java.util.Random

/**
 * The 'Foarray' - ADVENTURE!
 *
 * @author Arkan <arkan@drakon.io>
 */
public class RandomTeleArray : AlchemyArray(Reference.Textures.RANDOM_TELE_ARRAY, Reference.Names.RANDOM_TELE_ARRAY) {

    private val rand = Random()

    override fun onArrayActivated(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entityPlayer: EntityPlayer, sideHit: Int, hitX: Float, hitY: Float, hitZ: Float) {
        if (!world.isRemote) {
            // TODO: Ensure size >= 2
            val range = rtConf.range
            val dx = rand.nextInt(range*2) - range
            val dz = rand.nextInt(range*2) - range
            val x = eventX + dx
            val z = eventZ + dz

            // Check out of map bounds
            if (x >= 30000000 || x <= -30000000 || z <= -30000000 || z >= 30000000) {
                Laundarray.log.warn("RandomTele: Failed (out of bounds) - x=$x, z=$z")
                entityPlayer.addChatMessage(ChatComponentText("The Farlands seem to be blocking your travel..."))
                return
            }

            world.getChunkFromBlockCoords(x, z) // Force the target to be generated
            var y = world.getHeightValue(x, z)
            if (y <= 0) y = 64 // Catch invalid/unloaded chunks
            y += entityPlayer.height.toInt()
            entityPlayer.setPosition(x.toDouble(), y.toDouble(), z.toDouble())
        }
    }

    override fun getChalkCostPerBlock(): Int {
        return 2 // Expensive!
    }
}