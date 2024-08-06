package com.xir.NHUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = NHUtilities.MODID,
    version = Tags.VERSION,
    name = NHUtilities.MOD_Name,
    dependencies = "required-before:IC2; " + "required-before:gregtech; ",
    acceptedMinecraftVersions = "[1.7.10]")
public class NHUtilities {

    public static final String MODID = "NHUtilities";
    public static final String MOD_Name = "NH Utilities";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance
    public static NHUtilities instance;

    @SidedProxy(clientSide = "com.xir.NHUtilities.ClientProxy", serverSide = "com.xir.NHUtilities.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
