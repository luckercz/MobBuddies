package com.lucker.mobbuddies;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static void initialize(){
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
//                .register((itemGroup) -> itemGroup.add(ModItems.SUMMONERS_BOOK));
    }

    public static final Item SUMMONERS_BOOK = register(
            new Item(new Item.Settings()),
            "summoners_book"
    );

    public static Item register(Item item, String id){
        Identifier itemID = Identifier.of("mob-buddies", id);

        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        return registeredItem;
    }
}
