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
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
                .register((itemGroup) -> itemGroup.add(ModItems.SUMMONERS_BOOK));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.MOB_ENERGY_RAW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.MOB_ENERGY_INGOT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register((itemGroup) -> itemGroup.add(ModItems.SOULGRAIN_SEEDS));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
                .register((itemGroup) -> itemGroup.add(ModItems.SOULGRAIN));

        //Buddy Food
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.ZOMBIE_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.ZOMBIE_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SKELETON_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SKELETON_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SPIDER_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SPIDER_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.CREEPER_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.CREEPER_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.ENDERMAN_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.ENDERMAN_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.GHAST_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.GHAST_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SHULKER_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SHULKER_HEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.PIGLIN_UPGRADE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.PIGLIN_HEAL));
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

    //Zombie
    public static final Item ZOMBIE_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "zombie_upgrade"
    );
    public static final Item ZOMBIE_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "zombie_heal"
    );

    //Skeleton
    public static final Item SKELETON_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "skeleton_upgrade"
    );
    public static final Item SKELETON_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "skeleton_heal"
    );

    //Spider
    public static final Item SPIDER_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "spider_upgrade"
    );
    public static final Item SPIDER_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "spider_heal"
    );

    //Creeper
    public static final Item CREEPER_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "creeper_upgrade"
    );
    public static final Item CREEPER_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "creeper_heal"
    );

    //Enderman
    public static final Item ENDERMAN_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "enderman_upgrade"
    );
    public static final Item ENDERMAN_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "enderman_heal"
    );

    //Ghast
    public static final Item GHAST_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "ghast_upgrade"
    );
    public static final Item GHAST_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "ghast_heal"
    );

    //Shulker
    public static final Item SHULKER_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "shulker_upgrade"
    );
    public static final Item SHULKER_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "shulker_heal"
    );

    //Piglin
    public static final Item PIGLIN_UPGRADE = register( //Upgrade
            new Item(new Item.Settings()),
            "piglin_upgrade"
    );
    public static final Item PIGLIN_HEAL = register( //Heal
            new Item(new Item.Settings()),
            "piglin_heal"
    );

    public static Item register(Item item, String id){
        Identifier itemID = Identifier.of("mob-buddies", id);

        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        return registeredItem;
    }
}
