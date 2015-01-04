package org.prok.oreunify;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class OreUnifyConfig {
    public static OreUnifyConfig INSTANCE;

    public static void load(Configuration configuration) {
        if (INSTANCE != null) {
            throw new RuntimeException("Configuration already loaded");
        }
        INSTANCE = new OreUnifyConfig(configuration);
    }

    private static final String CATEGORY_MAIN = "main";
    private static final String CATEGORY_HOOKS = "hooks";
    private static final String CATEGORY_MAP = "map";

    private final Configuration configuration;

    private final Property dumpDictionaryProperty;
    public boolean dumpDictionary;

    private final Property mapSuggestionsProperty;
    public boolean mapSuggestions;

    private final Property ignoreUniqueProperty;
    public boolean ignoreUnique;

    private final Property itemStackHookProperty;
    public boolean itemStackHook;

    private final Property chunkHookProperty;
    public boolean chunkHook;

    private final Property harvestDropProperty;
    public boolean harvestDrop;

    private final Property itemPickupProperty;
    public boolean itemPickup;

    private final Property livingDropsProperty;
    public boolean livingDrops;

    private final Property entitySpawnProperty;
    public boolean entitySpawn;

    private final Property itemMappingProperty;
    public OreUnifyRow[] itemMapping;

    private final Property customOresProperty;
    public OreUnifyCustomOre[] customOres;

    private OreUnifyConfig(Configuration configuration) {
        this.configuration = configuration;

        dumpDictionaryProperty = configuration.get(CATEGORY_MAIN, "dumpDictionary", false, "Dump all possible combinations into %minecraft%/oreunify.txt");
        dumpDictionary = dumpDictionaryProperty.getBoolean();
        dumpDictionaryProperty.set(false);

        mapSuggestionsProperty = configuration.get(CATEGORY_MAIN, "mapSuggestions", true, "Replace map with suggestions");
        mapSuggestions = mapSuggestionsProperty.getBoolean();
        mapSuggestionsProperty.set(false);

        ignoreUniqueProperty = configuration.get(CATEGORY_MAIN, "ignoreUnique", true, "Ignore ore dictionary rows with only one item");
        ignoreUnique = ignoreUniqueProperty.getBoolean();

        itemStackHookProperty = configuration.get(CATEGORY_HOOKS, "itemStack", true, "Enable low-level ItemStack hook");
        itemStackHook = itemStackHookProperty.getBoolean();

        chunkHookProperty = configuration.get(CATEGORY_HOOKS, "chunk", false, "Replace all (even already generated) ores with rules (can be slow)");
        chunkHook = chunkHookProperty.getBoolean();

        harvestDropProperty = configuration.get(CATEGORY_HOOKS, "harvestDrop", true, "Hook on harvest blocks (mining, digging)");
        harvestDrop = harvestDropProperty.getBoolean();

        itemPickupProperty = configuration.get(CATEGORY_HOOKS, "itemPickup", true, "Hook on picking-up items (by players or mobs)");
        itemPickup = itemPickupProperty.getBoolean();

        livingDropsProperty = configuration.get(CATEGORY_HOOKS, "livingDrops", true, "Hook on drops items by entity (player's death)");
        livingDrops = livingDropsProperty.getBoolean();

        entitySpawnProperty = configuration.get(CATEGORY_HOOKS, "entitySpawn", true, "Hook on spawn entities in world (item drop, player join)");
        entitySpawn = entitySpawnProperty.getBoolean();

        itemMappingProperty = configuration.get(CATEGORY_MAP, "itemMapping", new String[0], "Items mapping");
        itemMapping = OreUnifyRow.parse(itemMappingProperty.getStringList());

        customOresProperty = configuration.get(CATEGORY_MAP, "customOres", new String[0], "Custom ore dictionary rules");
        customOres = OreUnifyCustomOre.parse(customOresProperty.getStringList());

        configuration.save();
    }

    public void setMapping(OreUnifyRow[] itemMapping) {
        itemMappingProperty.set(OreUnifyRow.format(this.itemMapping = itemMapping));
        configuration.save();
    }

    public void setCustomOres(OreUnifyCustomOre[] customOres) {
        itemMappingProperty.set(OreUnifyCustomOre.format(this.customOres = customOres));
        configuration.save();
    }
}
