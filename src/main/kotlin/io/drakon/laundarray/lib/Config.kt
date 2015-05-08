package io.drakon.laundarray.lib

import net.minecraftforge.common.config.Configuration
import java.io.File

/**
 * Mod configuration.
 *
 * @author Arkan <arkan@drakon.io>
 */
public object Config {

    public object Arrays {
        public object Laundry {
            public var enabled:Boolean = true
        }
        public object RandomTeleport {
            public var enabled: Boolean = true
            public var range: Int = 2000
        }
    }

    public fun loadConfig(file: File) {
        val conf = Configuration(file)
        conf.load()

        // Laundry
        Arrays.Laundry.enabled = conf.getBoolean("enable", "laundry", Arrays.Laundry.enabled, "Enable Laundry Array (doesn't remove existing arrays)")

        // Random Teleport
        Arrays.RandomTeleport.enabled = conf.getBoolean("enable", "randomtele", Arrays.RandomTeleport.enabled, "Enable Random Teleport Array (doesn't remove existing arrays)")
        Arrays.RandomTeleport.range = conf.getInt("range", "randomtele", Arrays.RandomTeleport.range, 500, 5000, "Max radius teleported per use of array")

        conf.save()
    }

}