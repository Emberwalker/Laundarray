package io.drakon.laundarray.arrays

import io.drakon.laundarray.lib.Config.Arrays.IslandGen as igConf
import io.drakon.laundarray.lib.Reference

import com.pahimar.ee3.api.array.AlchemyArray
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChatComponentText
import net.minecraft.world.Explosion
import net.minecraft.world.World
import java.util.HashSet
import java.util.Random

/**
 * Arraysing Terrain with alchemy. Who doesn't like floating islands?
 *
 * @author Arkan <arkan@drakon.io>
 */
public class IslandGenArray : AlchemyArray(Reference.Textures.ISLAND_GEN_ARRAY, Reference.Names.ISLAND_GEN_ARRAY) {

    private final val CHALK_COST = 2
    private final val NBT_PREFIX = "islgen"

    private var requiredEMC = 1024 // TODO: Vary depending on circle size
    private var storedEMC = 0.0
    private var isRunning = false

    override fun onEntityCollidedWithArray(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entity: Entity?) {
        super.onEntityCollidedWithArray(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entity)

        if (world.isRemote) return
        if (entity !is EntityItem) return
        val istack = entity.getEntityItem()
        val item = istack.getItem()
        if (item == null || !EnergyValueRegistryProxy.hasEnergyValue(item)) return

        val value = EnergyValueRegistryProxy.getEnergyValue(item).getValue()
        val stackValue = value * istack.stackSize
        val deficit = requiredEMC - storedEMC

        if (deficit > stackValue) {
            // Short-circuit gobble
            istack.stackSize = 0
            storedEMC += stackValue
        } else {
            // Find how many we need to complete the requirement
            var toTake = Math.min(istack.stackSize.toDouble(), deficit/value)
            toTake = Math.ceil(toTake)
            var take = toTake.toInt()
            istack.stackSize -= take
            storedEMC += take * value
        }

        if (istack.stackSize <= 0) entity.setDead()
    }

    override fun onArrayActivated(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entityPlayer: EntityPlayer, sideHit: Int, hitX: Float, hitY: Float, hitZ: Float) {
        super.onArrayActivated(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entityPlayer, sideHit, hitX, hitY, hitZ)

        if (world.isRemote) return
        if (isRunning) entityPlayer.addChatMessage(ChatComponentText("This array is already running."))

        if (storedEMC < requiredEMC) {
            entityPlayer.addChatMessage(ChatComponentText("The array seems to be short on EMC. Maybe I should throw more stuff at it?"))
        } else {
            // TODO: Start island gen
        }
    }

    override fun onUpdate(world: World, arrayX: Int, arrayY: Int, arrayZ: Int, tickCount: Int) {
        // TODO: Island building
        super.onUpdate(world, arrayX, arrayY, arrayZ, tickCount)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        if (nbt.hasKey("$NBT_PREFIX.storedEMC")) {
            storedEMC = nbt.getDouble("$NBT_PREFIX.storedEMC")
        }
    }

    override fun writeToNBT(nbtTagCompound: NBTTagCompound) {
        super.writeToNBT(nbtTagCompound)
        // TODO: Write stored EMC
    }

    override fun getChalkCostPerBlock(): Int {
        return CHALK_COST
    }

    override fun onArrayDestroyedByExplosion(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, explosion: Explosion?) {
        onArrayDestroyed(world, arrayX, arrayY, arrayZ)
        super.onArrayDestroyedByExplosion(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, explosion)
    }

    override fun onArrayDestroyedByPlayer(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, metaData: Int) {
        onArrayDestroyed(world, arrayX, arrayY, arrayZ)
        super.onArrayDestroyedByPlayer(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, metaData)
    }

    private fun onArrayDestroyed(world: World, x: Int, y: Int, z: Int) {
        if (!world.isRemote && igConf.ejectDirt) {
            var amt = Math.floor(storedEMC.toDouble()).toInt()
            val stacks = HashSet<ItemStack>()

            while (amt > 0) {
                var stackSize = if (amt > 64) 64 else amt
                amt -= stackSize
                stacks.add(ItemStack(Blocks.dirt, stackSize))
            }

            val rand = Random()
            val factor = 0.05
            for (stack in stacks) {
                // Shamelessly borrowed from EE3
                val dx = rand.nextFloat() * 0.8 + 0.1
                val dy = rand.nextFloat() * 0.8 + 0.1
                val dz = rand.nextFloat() * 0.8 + 0.1

                val ent = EntityItem(world, x+dx, y+dy, z+dz, stack)

                ent.motionX = rand.nextGaussian() * factor
                ent.motionY = rand.nextGaussian() * factor + 0.2
                ent.motionZ = rand.nextGaussian() * factor

                world.spawnEntityInWorld(ent)
            }
        }
    }
}