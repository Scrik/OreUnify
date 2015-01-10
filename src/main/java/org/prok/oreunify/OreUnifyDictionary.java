package org.prok.oreunify;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.prok.oreunify.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

public class OreUnifyDictionary {
    private static final SparseArray<ItemStack> sDictionary = new SparseArray<ItemStack>();
    private static boolean sItemStackHook = false, sItemStackHookDisable = false;

    public static ItemStack getUnified(ItemStack item) {
        sItemStackHookDisable = true;
        try {
            for (int id : OreDictionary.getOreIDs(item)) {
                ItemStack stack = sDictionary.get(id);
                if (stack == null) {
                    continue;
                }
                if (stack.isItemEqual(item)) {
                    return item;
                }
                ItemStack newStack = stack.copy();
                newStack.stackSize = item.stackSize;
                return newStack;
            }
            return item;
        } finally {
            sItemStackHookDisable = false;
        }
    }

    public static ItemStack getUnified(Block block, int blockMetadata) {
        sItemStackHookDisable = true;
        ItemStack stack = getUnified(new ItemStack(block, 1, blockMetadata));
        return stack.getItem() != null ? stack : null;
    }

    public static void hookItemStack(ItemStack itemStack) {
        if (!sItemStackHook || sItemStackHookDisable) {
            return;
        }
        for (int id : OreDictionary.getOreIDs(itemStack)) {
            ItemStack stack = sDictionary.get(id);
            if (stack == null) {
                continue;
            }
            if (!stack.isItemEqual(itemStack)) {
                itemStack.func_150996_a(stack.getItem());
                itemStack.setItemDamage(stack.getItemDamage());
                itemStack.setTagCompound(stack.getTagCompound());
            }
            return;
        }
    }


    public static void initDictionary(OreUnifyRow[] mapping) {
        sDictionary.clear();
        final String[] oreDictionary = OreDictionary.getOreNames();
        for (OreUnifyRow row : mapping) {
            if (!contains(row.name, oreDictionary)) {
                System.out.println("Unknown ore dictionary name " + row.name + ", skip...");
                continue;
            }
            final int id = OreDictionary.getOreID(row.name);
            ItemStack replacement = findValidReplacement(row.replacement, row.candidates);
            if (replacement == null) {
                System.out.println("No valid matches for  " + row.name + ", skip...");
                continue;
            }
            sDictionary.append(id, replacement);
        }
    }

    private static boolean contains(String s, String[] array) {
        for (String a : array) {
            if (s.equals(a)) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack findValidReplacement(OreUnifyStack replacement, List<OreUnifyStack> candidates) {
        ItemStack itemStack;
        if (isValid(itemStack = replacement.get())) {
            return itemStack;
        }
        for (OreUnifyStack stack : candidates) {
            if (isValid(itemStack = stack.get())) {
                return itemStack;
            }
        }
        return null;
    }

    private static boolean isValid(ItemStack stack) {
        return stack != null && stack.getItem() != null;
    }

    public static void enableItemStackHook() {
        sItemStackHook = true;
    }

    public static Block getBlock(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        final Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            return ((ItemBlock) item).field_150939_a;
        }
        return null;
    }

    public static void unifyStack(ItemStack stack) {
        ItemStack newStack = getUnified(stack);
        if (newStack != null && newStack != stack) {
            stack.func_150996_a(newStack.getItem());
            stack.setItemDamage(newStack.getItemDamage());
        }
    }

    public static void unifyStack(ArrayList<ItemStack> drops) {
        for (ItemStack stack : drops) {
            unifyStack(stack);
        }
    }

    public static void unifyEntity(ArrayList<EntityItem> drops) {
        for (EntityItem item : drops) {
            unifyEntity(item);
        }
    }

    public static void unifyEntity(EntityItem item) {
        ItemStack stack = item.getEntityItem();
        ItemStack newStack = getUnified(stack);
        if (newStack != null && newStack != stack) {
            item.setEntityItemStack(newStack);
        }
    }

    public static void unifyStack(ItemStack[] stacks) {
        for (ItemStack stack : stacks) {
            unifyStack(stack);
        }
    }
}
