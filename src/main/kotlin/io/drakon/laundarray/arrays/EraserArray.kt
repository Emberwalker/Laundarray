package io.drakon.laundarray.arrays

import io.drakon.laundarray.lib.Reference

import com.pahimar.ee3.api.array.AlchemyArray

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.World

/**
 * Errayser. Destroys items on contact.
 *
 * @author Arkan <arkan@drakon.io>
 */
public class EraserArray : AlchemyArray(Reference.Textures.ERASER_ARRAY, Reference.Names.ERASER_ARRAY) {

    override fun onEntityCollidedWithArray(world: World, eventX: Int, eventY: Int, eventZ: Int, arrayX: Int, arrayY: Int, arrayZ: Int, entity: Entity?) {
        if (world.isRemote || entity !is EntityItem) return
        entity.getEntityItem().stackSize = 0
        entity.setDead()
    }

}