package io.drakon.laundarray.arrays

import com.pahimar.ee3.api.array.AlchemyArray
import cpw.mods.fml.common.network.NetworkRegistry
import io.drakon.laundarray.Laundarray
import io.drakon.laundarray.lib.Reference
import io.drakon.laundarray.net.PacketHandler
import io.drakon.laundarray.net.msg.MessageParticleEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemCloth
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

/**
 * The mods namesake.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class LaundryArray : AlchemyArray(Reference.Textures.LAUNDRY_ARRAY, Reference.Names.LAUNDRY_ARRAY) {

    private val PARTICLE_RENDER_DIST = 64.0

    override fun onEntityCollidedWithArray(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entity: Entity) {
        if (entity is EntityItem) {
            val istack = entity.getEntityItem()
            val item = istack.getItem()
            if (item is ItemCloth && istack.getItemDamage() != 0) {
                //Laundarray.log.info("Hit by wool? $eventX/$eventY/$eventZ")
                istack.setItemDamage(0) // 0 = white
                shinyParticlesOooh("largesmoke", world, eventX.toDouble(), eventY.toDouble(), eventZ.toDouble())
            } else if (item is ItemArmor && item.getArmorMaterial() == ItemArmor.ArmorMaterial.CLOTH && item.hasColor(istack)) {
                //Laundarray.log.info("Hit by armor? $eventX/$eventY/$eventZ")
                item.removeColor(istack)
                shinyParticlesOooh("largesmoke", world, eventX.toDouble(), eventY.toDouble(), eventZ.toDouble())
            }
        }

        super.onEntityCollidedWithArray(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entity)
    }

    override fun onArrayPlacedBy(world: World?, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entityLiving: EntityLivingBase?, itemStack: ItemStack?) {
        super.onArrayPlacedBy(world, eventX, eventY, eventZ, arrayX, arrayY, arrayZ, entityLiving, itemStack)
    }

    private fun shinyParticlesOooh(particle: String, world: World, x: Double, y: Double, z: Double) {
        //Laundarray.log.info("World Remote: {}", world.isRemote)
        if (world.isRemote) {
            PacketHandler.PIPE.sendToAllAround(MessageParticleEvent(particle, x, y, z), NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, PARTICLE_RENDER_DIST))
        }
    }
}