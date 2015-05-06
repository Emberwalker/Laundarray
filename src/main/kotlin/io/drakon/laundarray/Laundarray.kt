package io.drakon.laundarray

import io.drakon.laundarray.shims.LaundarrayMod

import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import io.drakon.laundarray.arrays.ArrayInit
import io.drakon.laundarray.lib.Reference
import io.drakon.laundarray.net.PacketHandler
import io.drakon.laundarray.proxy.CommonProxy

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Laundarray - Laundry... Alchemy style!
 *
 * @author Arkan <arkan@drakon.io>
 */
[suppress("UNUSED_PARAMETER")]
public object Laundarray {

    public val log:Logger = LogManager.getLogger(Reference.IDs.MODID)

    public fun getProxy(): CommonProxy {
        return LaundarrayMod.proxy
    }

    public fun preinit(evt:FMLPreInitializationEvent) {
        log.info("Preparing to do the laundry.")
        PacketHandler.init()
    }

    public fun init(evt:FMLInitializationEvent) {
        ArrayInit.init()
    }

    public fun postinit(evt:FMLPostInitializationEvent) {

    }

}