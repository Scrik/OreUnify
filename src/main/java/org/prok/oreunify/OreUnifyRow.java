package org.prok.oreunify;

import cpw.mods.fml.common.registry.GameData;
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

    public static List<OreUnifyStack> parseList(String s) {
        String[] stacks = s.split(",");
        List<OreUnifyStack> list = new ArrayList<OreUnifyStack>(stacks.length);
        for (String stackRaw : stacks) {
            OreUnifyStack stack = parseStack(stackRaw.trim());
            if (stack != null) {
                list.add(stack);
            }
        }
        return list;
    }

    public static OreUnifyStack parseStack(String s) {
        if ("null".equals(s) || s.length() == 0) {
            return null;
        }
        int meta = 0;
        int c = s.lastIndexOf('%');
        if (c > 0) {
            meta = Integer.parseInt(s.substring(c + 1).trim());
            s = s.substring(0, c);
        }
        return new OreUnifyStack(s.trim(), 1, meta);
    }

    public String name;
    public OreUnifyStack replacement;
    public List<OreUnifyStack> candidates;

    @Override
    public String toString() {
        return String.format("%s: %s; [%s]", name, formatStack(replacement), formatList(candidates));
    }

    public static String formatList(List<OreUnifyStack> list) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (OreUnifyStack stack : list) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }
            builder.append(formatStack(stack));
        }
        return builder.toString();
    }

    public static String formatStack(OreUnifyStack stack) {
        if (stack == null) {
            return "null";
        }
        final ItemStack itemStack = stack.get();
        if (itemStack == null) {
            return "null";
        }
        final String name = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        final int meta = itemStack.getItemDamage();
        if (meta == 0) {
            return name;
        }
        return String.format("%s%%%d", name, meta);
    }

    public static OreUnifyRow create(String name) {
        OreUnifyRow row = new OreUnifyRow();
        row.name = name;
        row.candidates = convert(OreDictionary.getOres(name));
        final int length = row.candidates.size();
        if (length == 0 || length == 1 && OreUnifyConfig.INSTANCE.ignoreUnique) {
            return null;
        }
        row.replacement = row.candidates.get(0);
        return row;
    }

    private static List<OreUnifyStack> convert(List<ItemStack> stacks) {
        List<OreUnifyStack> result = new ArrayList(stacks.size());
        for (ItemStack stack : stacks) {
            result.add(new OreUnifyStack(GameData.getItemRegistry().getNameForObject(stack.getItem()), stack.stackSize, stack.getItemDamage()));
        }
        return result;
    }
}
