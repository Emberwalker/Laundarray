package io.drakon.laundarray.lib

import net.minecraft.util.ResourceLocation

/**
 * Shared content.
 */
public object Reference {

    object IDs {
        public val MODID:String = "Laundarray"
    }
    object Names {
        public val LAUNDRY_ARRAY:String = "arrays.laundry:laundarray"
    }

    object Textures {
        public val LAUNDRY_ARRAY:ResourceLocation = ResourceLocation(IDs.MODID.toLowerCase(), "textures/arrays/laundarray.png")
    }

}