package org.prok.oreunify;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(modid = "oreunify", useMetadata = true)
public class OreUnify {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OreUnifyConfig.load(new Configuration(event.getSuggestedConfigurationFile()));

        MinecraftForge.EVENT_BUS.register(new OreUnifyEventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (OreUnifyConfig.INSTANCE.mapSuggestions) {
            OreUnifyConfig.INSTANCE.setMapping(computeSuggestions());
        }
        if (OreUnifyConfig.INSTANCE.dumpDictionary) {
            try {
                File file = new File(Minecraft.getMinecraft().mcDataDir, "oreunify.txt");
                OutputStream os = new FileOutputStream(file);
                Writer writer = new OutputStreamWriter(os);
                for (String name : OreDictionary.getOreNames()) {
                    OreUnifyRow row = OreUnifyRow.create(name);
                    if (row != null) {
                        writer.write(row.toString());
                        writer.write('\n');
                    }
                }
                writer.close();
                os.close();
            } catch (Exception e) {
                new Exception("Error occurred during dictionary dump", e).printStackTrace();
            }
        }
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        if (sDictionaryInit) {
            sDictionaryInit = false;
            for (OreUnifyCustomOre ore : OreUnifyConfig.INSTANCE.customOres) {
                for (OreUnifyStack stack : ore.stacks) {
                    final ItemStack itemStack = stack.get();
                    System.out.println("Register " + itemStack + " as " + ore.name);
                    OreDictionary.registerOre(ore.name, itemStack);
                }
            }
            OreUnifyDictionary.initDictionary(OreUnifyConfig.INSTANCE.itemMapping);
        }
        if (OreUnifyConfig.INSTANCE.itemStackHook) {
            System.out.println("Enabling ItemStack hook...");
            OreUnifyDictionary.enableItemStackHook();
        }
    }

    @NetworkCheckHandler
    public boolean netCheckHandler(Map<String, String> mods, Side side) {
        return true;
    }

    private static boolean sDictionaryInit = true;

    public static OreUnifyRow[] computeSuggestions() {
        List<OreUnifyRow> list = new ArrayList<OreUnifyRow>();
        for (String name : OreDictionary.getOreNames()) {
            if (name.startsWith("ore") || name.startsWith("ingot") || name.startsWith("block") || name.startsWith("nugget")) {
                OreUnifyRow row = OreUnifyRow.create(name);
                if (row == null) {
                    System.out.println("Empty ore dictionary name: " + name + ", skip...");
                    continue;
                }
                list.add(row);
            }
        }
        return list.toArray(new OreUnifyRow[list.size()]);
    }
}
