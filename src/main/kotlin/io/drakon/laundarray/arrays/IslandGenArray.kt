package io.drakon.laundarray.arrays

import io.drakon.laundarray.lib.Config.Arrays.IslandGen as igConf
import io.drakon.laundarray.lib.Reference

import com.pahimar.ee3.api.array.AlchemyArray
import com.pahimar.ee3.api.exchange.EnergyValueRegistryProxy
import com.pahimar.ee3.tileentity.TileEntityAlchemyArray
import io.drakon.laundarray.algo.IslandGenerator

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ChatComponentText
import net.minecraft.world.Explosion
import net.minecraft.world.World
import java.util.HashMap
import java.util.HashSet
import java.util.Random
import java.util.concurrent.Future

/**
 * Arraysing Terrain with alchemy. Who doesn't like floating islands?
 *
 * @author Arkan <arkan@drakon.io>
 */
public class IslandGenArray : AlchemyArray(Reference.Textures.ISLAND_GEN_ARRAY, Reference.Names.ISLAND_GEN_ARRAY) {

    private final val CHALK_COST = 2
    private final val NBT_PREFIX = "islgen"
    private final val SCALING_FACTOR = 15

    private var requiredEMC = 1024 // TODO: Vary depending on circle size
    private var storedEMC = 0.0

    private class IslGenState {
        public var isRunning:Boolean = false
        public var genFuture:Future<Pair<MutableList<IslandGenerator.Companion.BlockCoord>, MutableList<IslandGenerator.Companion.BlockCoord>>>? = null
        public var result:Pair<MutableList<IslandGenerator.Companion.BlockCoord>, MutableList<IslandGenerator.Companion.BlockCoord>>? = null
        public val rand:Random = Random()
        public var size:Int = 0
    }

    private data class ArrayCoord(val x:Int, val y:Int, val z:Int)

    private var states = HashMap<ArrayCoord, IslGenState>()

    override fun onEntityCollidedWithArray(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entity: Entity?) {
        super.onEntityCollidedWithArray(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entity)

        if (world.isRemote) return
        if (entity !is EntityItem) return
        /*val istack = entity.getEntityItem()
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

        if (istack.stackSize <= 0) entity.setDead()*/
    }

    override fun onArrayActivated(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entityPlayer: EntityPlayer, sideHit: Int, hitX: Float, hitY: Float, hitZ: Float) {
        super.onArrayActivated(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entityPlayer, sideHit, hitX, hitY, hitZ)

        if (world.isRemote) return

        var state = states.get(ArrayCoord(arrayX, arrayY, arrayZ))
        if (state == null) {
            state = IslGenState()
            states.put(ArrayCoord(arrayX, arrayY, arrayZ), state)
        }

        if (state.isRunning) {
            entityPlayer.addChatMessage(ChatComponentText("This array is already running."))
            return
        }

        /*if (storedEMC < requiredEMC) {
            entityPlayer.addChatMessage(ChatComponentText("The array seems to be short on EMC. Maybe I should throw more stuff at it?"))
        } else {
            // TODO: Start island gen
        }*/

        if (state.genFuture == null) {
            val tile = world.getTileEntity(arrayX, arrayY, arrayZ)
            if (tile !is TileEntityAlchemyArray) return

            entityPlayer.addChatMessage(ChatComponentText("The array churns into action..."))
            state.size = tile.getSize()
            val height = state.size * SCALING_FACTOR
            val radius = state.size * SCALING_FACTOR
            state.genFuture = IslandGenerator.generateIslandData(height, radius)
            state.isRunning = true
        }
    }

    override fun onUpdate(world: World, arrayX: Int, arrayY: Int, arrayZ: Int, tickCount: Int) {
        super.onUpdate(world, arrayX, arrayY, arrayZ, tickCount)

        if (world.isRemote) return

        val coord = ArrayCoord(arrayX, arrayY, arrayZ)
        var state = states.get(coord)
        if (state == null) {
            state = IslGenState()
            states.put(ArrayCoord(arrayX, arrayY, arrayZ), state)
        }

        if (state.isRunning && state.genFuture != null) {
            if (state.result != null) {
                // Continue build
                for (_ in 1..5)
                    continueBuild(state, world, coord, state.rand.nextInt(5) == 0)
            } else {
                if (state.genFuture!!.isDone()) {
                    state.result = state.genFuture!!.get()
                    continueBuild(state, world, coord)
                }
            }
        }
    }

    private fun continueBuild(state: IslGenState, world: World, coord: ArrayCoord, dirt:Boolean = false) {
        val stones = state.result!!.first
        val dirts = state.result!!.second

        val offset = Math.round((state.size.toDouble() * SCALING_FACTOR) / 2.0).toInt()
        val baseX = coord.x - offset
        val baseY = coord.y + 3
        val baseZ = coord.z - offset

        if (stones.isNotEmpty()) {
            val coordS = stones.first()
            stones.remove(coordS)

            if (world.getBlock(baseX + coordS.x, baseY + coordS.y, baseZ + coordS.z).isAir(world, baseX + coordS.x, baseY + coordS.y, baseZ + coordS.z)) {
                world.setBlock(baseX + coordS.x, baseY + coordS.y, baseZ + coordS.z, Blocks.stone)
            }
        }

        if (dirt && dirts.isNotEmpty()) {
            val coordD = dirts.first()
            dirts.remove(coordD)

            if (world.getBlock(baseX + coordD.x, baseY + coordD.y, baseZ + coordD.z).isAir(world, baseX + coordD.x, baseY + coordD.y, baseZ + coordD.z)) {
                world.setBlock(baseX + coordD.x, baseY + coordD.y, baseZ + coordD.z, Blocks.grass)
            }
        }

        if (stones.isEmpty() && dirts.isEmpty()) {
            states.remove(coord) // Get rid of state for this array
            world.setBlockToAir(coord.x, coord.y, coord.z)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        //if (nbt.hasKey("$NBT_PREFIX.storedEMC")) {
        //    storedEMC = nbt.getDouble("$NBT_PREFIX.storedEMC")
        //}
    }

    override fun writeToNBT(nbtTagCompound: NBTTagCompound) {
        super.writeToNBT(nbtTagCompound)
        // TODO: Write stored EMC
    }

    override fun getChalkCostPerBlock(): Int {
        return CHALK_COST
    }

    override fun onArrayDestroyedByExplosion(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, explosion: Explosion?) {
        //onArrayDestroyed(world, arrayX, arrayY, arrayZ)
        super.onArrayDestroyedByExplosion(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, explosion)
    }

    override fun onArrayDestroyedByPlayer(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, metaData: Int) {
        //onArrayDestroyed(world, arrayX, arrayY, arrayZ)
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