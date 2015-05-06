package io.drakon.laundarray.net

import io.drakon.laundarray.lib.Reference

import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.Side
import io.drakon.laundarray.net.msg.MessageParticleEvent

/**
 * Networking bumf.
 *
 * @author Arkan <arkan@drakon.io>
 */
public object PacketHandler {

    public val PIPE: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.IDs.MODID.toLowerCase())

    public fun init() {
        PIPE.registerMessage(javaClass<MessageParticleEvent>(), javaClass<MessageParticleEvent>(), 0, Side.CLIENT)
    }

}