package com.lucker.mobbuddies;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobBuddyHelper {

    public static LivingEntity Create(World world, IMobBuddyEntity mobBuddy, PlayerEntity owner, BlockPos pos){
        //Handle other summoned Buddies
        MobBuddies.removeExistingMobBuddy(owner, world);

        ((LivingEntity) mobBuddy).refreshPositionAndAngles(pos, 0.0F, 0.0F);
        mobBuddy.setOwner(owner);

        return (LivingEntity) mobBuddy;
    }

    public static void Initialize(IMobBuddyEntity mobBuddy, int levels, float health, String name){
        mobBuddy.levelUp(levels - 1);
        ((LivingEntity) mobBuddy).setHealth(health);
        mobBuddy.setCustomName(Text.of(name));
    }

    public static ActionResult InteractMob(PlayerEntity player, IMobBuddyEntity mobBuddy, Hand hand, Item healItem, Item upgradeItem){
        ItemStack heldItem = player.getStackInHand(hand);
        //MobBuddies.LOGGER.info(heldItem.getName().toString());
        if (heldItem.isOf(healItem)) {
            if (((LivingEntity) mobBuddy).getHealth() < ((LivingEntity) mobBuddy).getMaxHealth()) {
                ((LivingEntity) mobBuddy).heal(5.0F);
                heldItem.decrement(1);
                player.sendMessage(Text.literal("You healed your buddy: " + ((LivingEntity) mobBuddy).getHealth() + "/" + mobBuddy.getCustomMaxHealth()), true);
            } else {
                player.sendMessage(Text.literal("Your buddy is already at full health!"), true);
            }
            return ActionResult.CONSUME;
        }
        else if (heldItem.isOf(upgradeItem)) {
            int held = heldItem.getCount();
            int price = mobBuddy.getLevel();

            if(held >= price){
                mobBuddy.levelUp(1);
                player.sendMessage(Text.literal("Your buddy's level is now " + mobBuddy.getLevel() + "!"), true);
                heldItem.decrement(price);
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                playerData.buddies.get(MobBuddies.NBT_Names.get(((LivingEntity) mobBuddy).getType())).level += 1;
                mobBuddy.setCustomName(Text.of(playerData.buddies.get(MobBuddies.NBT_Names.get(((LivingEntity) mobBuddy).getType())).name));

                // Play a sound effect
                ((LivingEntity) mobBuddy).getWorld().playSound(null, ((LivingEntity) mobBuddy).getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
            else{
                player.sendMessage(Text.literal("The price of an upgrade is: " + price + "!"), true);
            }

            return ActionResult.CONSUME;
        }

        return null;
    }

    public static Text CustomName(String name, int lvl){
        return Text.of("[§alvl." + lvl + "§r] " + name);
    }
}
