package io.drakon.laundarray.proxy

import cpw.mods.fml.client.FMLClientHandler

public class ClientProxy : CommonProxy() {

    override fun spawnParticle(name: String, x: Double, y: Double, z: Double) {
        FMLClientHandler.instance().getWorldClient().spawnParticle(name, x, y, z, 0.0, 0.5, 0.0)
    }

}