package io.drakon.laundarray.shims;

import cpw.mods.fml.common.SidedProxy;
import io.drakon.laundarray.Laundarray;
import io.drakon.laundarray.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Because 1.7.10 is pre-Forgelin. *grumble*
 *
 * @author Arkan <arkan@drakon.io>
 */
@Mod(modid = "Laundarray", name = "Laundarray", version = "@VERSION@", dependencies = "required-after:EE3-API|array@[1.2,)")
public class LaundarrayMod {

    @SidedProxy(serverSide = "io.drakon.laundarray.proxy.CommonProxy", clientSide = "io.drakon.laundarray.proxy.ClientProxy")
    public static CommonProxy proxy = null;

    @EventHandler
    public void preinit(FMLPreInitializationEvent evt) {
        Laundarray.INSTANCE$.preinit(evt);
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {
        Laundarray.INSTANCE$.init(evt);
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent evt) {
        Laundarray.INSTANCE$.postinit(evt);
    }

}
