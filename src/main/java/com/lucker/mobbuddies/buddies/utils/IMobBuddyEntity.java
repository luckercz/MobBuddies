package com.lucker.mobbuddies.buddies.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface IMobBuddyEntity
{
    public PlayerEntity getOwner();
    public void setOwner(PlayerEntity owner);
    public int getLevel();
    public void setCustomName(@Nullable Text name);
    public void levelUp(int levels);
    public double getCustomMaxHealth();
}
