package com.lucker.mobbuddies;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {

        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerData.buddies.forEach((k, v) -> {
                playerNbt.putInt(k+"Level", v.level);
                playerNbt.putFloat(k+"Health", v.health);
                playerNbt.putString(k+"Name", v.name);
            });

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            if (!playerData.buddies.containsKey("zombie")) {
                playerData.buddies.put("zombie", new BuddyData());
            }
            if (!playerData.buddies.containsKey("skeleton")) {
                playerData.buddies.put("skeleton", new BuddyData());
            }
            if (!playerData.buddies.containsKey("creeper")) {
                playerData.buddies.put("creeper", new BuddyData());
            }
            if (!playerData.buddies.containsKey("spider")) {
                playerData.buddies.put("spider", new BuddyData());
            }
            if (!playerData.buddies.containsKey("enderman")) {
                playerData.buddies.put("enderman", new BuddyData());
            }
            if (!playerData.buddies.containsKey("piglin")) {
                playerData.buddies.put("piglin", new BuddyData());
            }
            if (!playerData.buddies.containsKey("ghast")) {
                playerData.buddies.put("ghast", new BuddyData());
            }

            playersNbt.getCompound(key).getKeys().forEach((k) -> {
                if(k.contains("Level")) {
                    playerData.buddies.get(k.split("Level")[0]).level = playersNbt.getCompound(key).getInt(k);
                }
                else if(k.contains("Health")) {
                    playerData.buddies.get(k.split("Health")[0]).health = playersNbt.getCompound(key).getFloat(k);
                }
                else if(k.contains("Name")) {
                    playerData.buddies.get(k.split("Name")[0]).name = playersNbt.getCompound(key).getString(k);
                }
            });

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, MobBuddies.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> {
            PlayerData newPlayerData = new PlayerData();
            newPlayerData.buddies.put("zombie", new BuddyData()); // Add default BuddyData
            newPlayerData.buddies.put("skeleton", new BuddyData());
            newPlayerData.buddies.put("creeper", new BuddyData());
            newPlayerData.buddies.put("spider", new BuddyData());
            newPlayerData.buddies.put("enderman", new BuddyData());
            newPlayerData.buddies.put("piglin", new BuddyData());
            newPlayerData.buddies.put("ghast", new BuddyData());
            return newPlayerData;
        });

        // Ensure default entries exist in buddies
        playerState.buddies.computeIfAbsent("zombie", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("skeleton", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("creeper", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("spider", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("enderman", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("piglin", k -> new BuddyData());
        playerState.buddies.computeIfAbsent("ghast", k -> new BuddyData());

        return playerState;
    }

}