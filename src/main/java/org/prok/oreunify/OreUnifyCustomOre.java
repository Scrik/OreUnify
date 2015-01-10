package org.prok.oreunify;

import java.util.List;

public class OreUnifyCustomOre {
    public String name;
    public List<OreUnifyStack> stacks;

    public static OreUnifyCustomOre parse(String s) {
        OreUnifyCustomOre customOre = new OreUnifyCustomOre();
        int c = s.indexOf(':');
        customOre.name = s.substring(0, c).trim();
        customOre.stacks = OreUnifyRow.parseList(s.substring(c + 1).trim());
        return customOre;
    }

    public static OreUnifyCustomOre[] parse(String[] array) {
        OreUnifyCustomOre[] result = new OreUnifyCustomOre[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = parse(array[i]);
        }
        return result;
    }

    public static String[] format(OreUnifyCustomOre[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, OreUnifyRow.formatList(stacks));
    }
}
