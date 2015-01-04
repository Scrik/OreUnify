package org.prok.oreunify;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;

public final class OreUnifyEventHandler {
    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        if (!OreUnifyConfig.INSTANCE.harvestDrop) {
            return;
        }
        OreUnifyDictionary.unifyStack(event.drops);
    }

    @SubscribeEvent
    public void onEntityItemPickup(EntityItemPickupEvent event) {
        if (!OreUnifyConfig.INSTANCE.itemPickup) {
            return;
        }
        OreUnifyDictionary.unifyEntity(event.item);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!OreUnifyConfig.INSTANCE.livingDrops) {
            return;
        }
        OreUnifyDictionary.unifyEntity(event.drops);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!OreUnifyConfig.INSTANCE.entitySpawn) {
            return;
        }
        final Entity entity = event.entity;
        if (entity instanceof EntityItem) {
            OreUnifyDictionary.unifyEntity((EntityItem) entity);
        } else if (entity instanceof EntityPlayer) {
            InventoryPlayer inventoryPlayer = ((EntityPlayer) entity).inventory;
            OreUnifyDictionary.unifyStack(inventoryPlayer.armorInventory);
            OreUnifyDictionary.unifyStack(inventoryPlayer.mainInventory);
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load chunkEvent) {
        if (!OreUnifyConfig.INSTANCE.chunkHook) {
            return;
        }
        final Chunk chunk = chunkEvent.getChunk();
        final int maxHeight = chunk.heightMap.length;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < maxHeight; y++) {
                    final Block block = chunk.getBlock(x, y, z);
                    if (block == null || block == Blocks.air) {
                        continue;
                    }
                    final int blockMetadata = chunk.getBlockMetadata(x, y, z);
                    ItemStack stack = OreUnifyDictionary.getUnified(block, blockMetadata);
                    if (stack != null) {
                        final Block newBlock = OreUnifyDictionary.getBlock(stack);
                        final int newBlockMetadata = stack.getItemDamage();
                        if (newBlock != block || newBlockMetadata != blockMetadata) {
                            chunk.func_150807_a(x, y, z, newBlock, newBlockMetadata);
                        }
                    }
                }
            }
        }
    }
}
