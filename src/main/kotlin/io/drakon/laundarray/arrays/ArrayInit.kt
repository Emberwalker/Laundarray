package io.drakon.laundarray.arrays

import com.pahimar.ee3.api.array.AlchemyArray
import com.pahimar.ee3.api.array.AlchemyArrayRegistryProxy
import io.drakon.laundarray.Laundarray

/**
 * Array initialiser.
 *
 * @author Arkan <arkan@drakon.io>
 */
public object ArrayInit {

    public val laundryArray:AlchemyArray = LaundryArray()

    public fun init() {
        if (!AlchemyArrayRegistryProxy.registerAlchemyArray(laundryArray)) {
            Laundarray.log.error("Failed to registry array?!")
        }
    }

}