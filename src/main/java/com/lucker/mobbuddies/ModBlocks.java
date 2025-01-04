package com.lucker.mobbuddies;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static void initialize(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register((itemGroup) -> itemGroup.add(ModBlocks.MOB_ENERGY_ORE.asItem()));
    }

    public static final Block MOB_ENERGY_ORE = register(
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.ANCIENT_DEBRIS).requiresTool().strength(4.0f)),
            "mob_energy_ore",
            true
    );

    public static Block register(Block block, String name, boolean shouldRegisterItem){
        // Register the block and its item.
        Identifier id = Identifier.of("mob-buddies", name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }
}
