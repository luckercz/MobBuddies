package com.lucker.mobbuddies;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static void initialize(){
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
//                .register((itemGroup) -> itemGroup.add(ModItems.SUMMONERS_BOOK));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.MOB_ENERGY_RAW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.MOB_ENERGY_INGOT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register((itemGroup) -> itemGroup.add(ModItems.SOULGRAIN_SEEDS));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.SOULGRAIN));
    }

    public static final Item SUMMONERS_BOOK = register(
            new Item(new Item.Settings()),
            "summoners_book"
    );

    public static final Item MOB_ENERGY_INGOT = register(
            new Item(new Item.Settings()),
            "mob_energy_ingot"
    );

    public static final Item MOB_ENERGY_RAW = register(
            new Item(new Item.Settings()),
            "mob_energy_raw"
    );

    public static final Item SOULGRAIN_SEEDS = register(
            new AliasedBlockItem(ModBlocks.SOULGRAIN_CROP, new Item.Settings()),
            "soulgrain_seeds"
    );

    public static final Item SOULGRAIN = register(
            new Item(new Item.Settings()),
            "soulgrain"
    );

    public static Item register(Item item, String id){
        Identifier itemID = Identifier.of("mob-buddies", id);

        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        return registeredItem;
    }
}
