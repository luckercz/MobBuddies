package com.lucker.mobbuddies;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobBuddies implements ModInitializer {
	public static final String MOD_ID = "mob-buddies";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<ZombieBuddyEntity> ZOMBIE_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "zombie-buddy"),
			EntityType.Builder.create(ZombieBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.95F).build("zombie-buddy")
	);

	public static final EntityType<CubeEntity> CUBE = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "cube"),
			EntityType.Builder.create(CubeEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 0.75f).build("cube")
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		FabricDefaultAttributeRegistry.register(ZOMBIE_BUDDY, ZombieBuddyEntity.createMobAttributes());
		FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createMobAttributes());
	}
}