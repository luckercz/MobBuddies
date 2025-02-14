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
                .register((itemGroup) -> itemGroup.add(ModItems.ROTTEN_CORE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.DECAYED_LOAF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.BONY_BREW));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.BONE_MARROW_BISCUIT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.WEBBED_WRAP));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.STICKY_PASTRY));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.CHARGED_MEAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.EXPLODING_CRUST));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.VOID_ESSENCE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SHADOWBREAD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.BLAZING_TEARS));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.MISTY_FLAN));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.BOXED_DELIGHT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.SHELL_CAKE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.GILDED_TREAT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.GOLDEN_RATION));

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
    public static final Item ROTTEN_CORE = register(
            new Item(new Item.Settings()),
            "rotten_core"
    );
    public static final Item DECAYED_LOAF = register(
            new Item(new Item.Settings()),
            "decayed_loaf"
    );

    //Skeleton
    public static final Item BONY_BREW = register(
            new Item(new Item.Settings()),
            "bony_brew"
    );
    public static final Item BONE_MARROW_BISCUIT = register(
            new Item(new Item.Settings()),
            "bone_marrow_biscuit"
    );

    //Spider
    public static final Item WEBBED_WRAP = register(
            new Item(new Item.Settings()),
            "webbed_wrap"
    );
    public static final Item STICKY_PASTRY = register(
            new Item(new Item.Settings()),
            "sticky_pastry"
    );

    //Creeper
    public static final Item CHARGED_MEAL = register(
            new Item(new Item.Settings()),
            "charged_meal"
    );
    public static final Item EXPLODING_CRUST = register(
            new Item(new Item.Settings()),
            "exploding_crust"
    );

    //Enderman
    public static final Item VOID_ESSENCE = register(
            new Item(new Item.Settings()),
            "void_essence"
    );
    public static final Item SHADOWBREAD = register(
            new Item(new Item.Settings()),
            "shadowbread"
    );

    //Ghast
    public static final Item BLAZING_TEARS = register(
            new Item(new Item.Settings()),
            "blazing_tears"
    );
    public static final Item MISTY_FLAN = register(
            new Item(new Item.Settings()),
            "misty_flan"
    );

    //Shulker
    public static final Item BOXED_DELIGHT = register(
            new Item(new Item.Settings()),
            "boxed_delight"
    );
    public static final Item SHELL_CAKE = register(
            new Item(new Item.Settings()),
            "shell_cake"
    );

    //Piglin
    public static final Item GILDED_TREAT = register(
            new Item(new Item.Settings()),
            "gilded_treat"
    );
    public static final Item GOLDEN_RATION = register(
            new Item(new Item.Settings()),
            "golden_ration"
    );

    public static Item register(Item item, String id){
        Identifier itemID = Identifier.of("mob-buddies", id);

        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        return registeredItem;
    }
}
