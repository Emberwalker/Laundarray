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
        private val PREFIX = "arrays.laundry"
        public val LAUNDRY_ARRAY:String = "$PREFIX:laundarray"
        public val RANDOM_TELE_ARRAY:String = "$PREFIX:random_teleport"
    }

    object Textures {
        private val PREFIX = "textures/arrays"
        public val LAUNDRY_ARRAY:ResourceLocation = ResourceLocation(IDs.MODID.toLowerCase(), "$PREFIX/laundarray.png")
        public val RANDOM_TELE_ARRAY:ResourceLocation = ResourceLocation(IDs.MODID.toLowerCase(), "$PREFIX/random_tele.png")
    }

}