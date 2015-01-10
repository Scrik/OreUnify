package org.prok.oreunify;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class OreUnifyStack {
    private final String name;
    private final int metadata;
    private final int count;
    private ItemStack itemStack;

    public OreUnifyStack(String name, int count, int metadata) {
        this.name = name;
        this.count = count;
        this.metadata = metadata;
    }

    public ItemStack get() {
        return get(count);
    }

    public ItemStack get(int count) {
        if (itemStack != null) {
            return itemStack;
        }
        Item item = GameData.getItemRegistry().getObject(name);
        if (item == null) {
            throw new RuntimeException("Unknown item: " + name);
        }
        return itemStack = new ItemStack(item, count, metadata);
    }
}
