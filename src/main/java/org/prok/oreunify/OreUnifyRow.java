package org.prok.oreunify;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public final class OreUnifyRow {
    public static OreUnifyRow[] parse(String[] array) {
        OreUnifyRow[] result = new OreUnifyRow[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = parse(array[i]);
        }
        return result;
    }

    public static OreUnifyRow parse(String s) {
        OreUnifyRow row = new OreUnifyRow();
        int c1 = s.indexOf(':');
        row.name = s.substring(0, c1).trim();
        int c2 = s.indexOf(';', c1 + 1);
        row.replacement = parseStack(s.substring(c1 + 1, c2).trim());
        c1 = s.indexOf('[', c2 + 1);
        c2 = s.lastIndexOf(']');
        row.candidates = parseList(s.substring(c1 + 1, c2).trim());
        return row;
    }

    public static String[] format(OreUnifyRow[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    public static List<ItemStack> parseList(String s) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        int c1 = 0, c2;
        while (c1 < s.length()) {
            c2 = s.indexOf(',', c1);
            String n = c2 < 0 ? s.substring(c1) : s.substring(c1, c2);
            ItemStack stack = parseStack(n.trim());
            if (stack != null) {
                list.add(stack);
            }
            if (c2 < 0) {
                break;
            }
            c1 = c2 + 1;
        }
        return list;
    }

    public static ItemStack parseStack(String s) {
        if ("null".equals(s) || s.length() == 0) {
            return null;
        }
        int meta = 0;
        int c = s.lastIndexOf('%');
        if (c > 0) {
            meta = Integer.parseInt(s.substring(c + 1).trim());
            s = s.substring(0, c);
        }
        Item item = GameData.getItemRegistry().getObject(s.trim());
        if (item == null) {
            return null;
        }
        return new ItemStack(item, 1, meta);
    }

    public String name;
    public ItemStack replacement;
    public List<ItemStack> candidates;

    @Override
    public String toString() {
        return String.format("%s: %s; [%s]", name, formatStack(replacement), formatList(candidates));
    }

    public static String formatList(List<ItemStack> list) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (ItemStack stack : list) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }
            builder.append(formatStack(stack));
        }
        return builder.toString();
    }

    public static String formatStack(ItemStack stack) {
        if (stack == null) {
            return "null";
        }
        final String name = GameData.getItemRegistry().getNameForObject(stack.getItem());
        if (name == null) {
            return "null";
        }
        final int meta = stack.getItemDamage();
        if (meta == 0) {
            return name;
        }
        return String.format("%s%%%d", name, meta);
    }

    public static OreUnifyRow create(String name) {
        OreUnifyRow row = new OreUnifyRow();
        row.name = name;
        row.candidates = OreDictionary.getOres(name);
        final int length = row.candidates.size();
        if (length == 0 || length == 1 && OreUnifyConfig.INSTANCE.ignoreUnique) {
            return null;
        }
        row.replacement = row.candidates.get(0);
        return row;
    }
}
