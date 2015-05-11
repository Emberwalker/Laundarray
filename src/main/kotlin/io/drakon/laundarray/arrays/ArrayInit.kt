package io.drakon.laundarray.arrays

import com.pahimar.ee3.api.array.AlchemyArray
import com.pahimar.ee3.api.array.AlchemyArrayRegistryProxy
import io.drakon.laundarray.Laundarray
import io.drakon.laundarray.lib.Config

/**
 * Array initialiser.
 *
 * @author Arkan <arkan@drakon.io>
 */
public object ArrayInit {

    public val laundryArray:AlchemyArray = LaundryArray()
    public val randomTeleArray:AlchemyArray = RandomTeleArray()
    public val islandGenArray:AlchemyArray = IslandGenArray()
    public val eraserArray:AlchemyArray = EraserArray()

    public fun init() {
        if (Config.Arrays.Laundry.enabled) AlchemyArrayRegistryProxy.registerAlchemyArray(laundryArray)
        if (Config.Arrays.RandomTeleport.enabled) AlchemyArrayRegistryProxy.registerAlchemyArray(randomTeleArray)
        if (Config.Arrays.IslandGen.enabled) AlchemyArrayRegistryProxy.registerAlchemyArray(islandGenArray)
        if (Config.Arrays.Eraser.enabled) AlchemyArrayRegistryProxy.registerAlchemyArray(eraserArray)
    }

}