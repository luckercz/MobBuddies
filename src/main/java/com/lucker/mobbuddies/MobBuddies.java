package com.lucker.mobbuddies;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	public static final EntityType<SkeletonBuddyEntity> SKELETON_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "skeleton-buddy"),
			EntityType.Builder.create(SkeletonBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.95F).build("skeleton-buddy")
	);

	public static final EntityType<CreeperBuddyEntity> CREEPER_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "creeper-buddy"),
			EntityType.Builder.create(CreeperBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.7F).build("creeper-buddy")
	);

	public static final EntityType<SpiderBuddyEntity> SPIDER_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddie", "spider-buddy"),
			EntityType.Builder.create(SpiderBuddyEntity::new, SpawnGroup.CREATURE).dimensions(1.4f, 0.9f).build("spider-buddy")
	);

	public static final EntityType<EndermanBuddyEntity> ENDERMAN_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "enderman-buddy"),
			EntityType.Builder.create(EndermanBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 2.9f).build("enderman-buddy")
	);

	public static final EntityType<PiglinBuddyEntity> PIGLIN_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "piglin-buddy"),
			EntityType.Builder.create(PiglinBuddyEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.95F).build("piglin-buddy")
	);

	public static final EntityType<GhastBuddyEntity> GHAST_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "ghast-buddy"),
			EntityType.Builder.create(GhastBuddyEntity::new, SpawnGroup.CREATURE).dimensions(4.0F, 4.0F).build("ghast-buddy")
	);

	public static final EntityType<ShulkerBuddyEntity> SHULKER_BUDDY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "shulker-buddy"),
			EntityType.Builder.create(ShulkerBuddyEntity::new, SpawnGroup.CREATURE).dimensions(1.0f, 1.5f).build("shulker-buddy")
	);


	public static final Set<EntityType<?>> MOB_BUDDY_TYPES = Set.of(
			ZOMBIE_BUDDY,
			SKELETON_BUDDY,
			CREEPER_BUDDY,
			SPIDER_BUDDY,
			ENDERMAN_BUDDY,
			PIGLIN_BUDDY,
			GHAST_BUDDY,
			SHULKER_BUDDY
	);

	public static final Map<EntityType, String> NBT_Names = Map.of(
			ZOMBIE_BUDDY, "zombie",
			SKELETON_BUDDY, "skeleton",
			CREEPER_BUDDY, "creeper",
			SPIDER_BUDDY, "spider",
			ENDERMAN_BUDDY, "enderman",
			PIGLIN_BUDDY, "piglin",
			GHAST_BUDDY, "ghast",
			SHULKER_BUDDY, "shulker"
	);

	public static final RegistryKey<PlacedFeature> MOB_ENERGY_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("mob-buddies", "mob_energy_ore_custom"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		//Initialize Buddies
		FabricDefaultAttributeRegistry.register(ZOMBIE_BUDDY, ZombieBuddyEntity.createCustomZombieAttributes());
		FabricDefaultAttributeRegistry.register(SKELETON_BUDDY, SkeletonBuddyEntity.createCustomSkeletonAttributes());
		FabricDefaultAttributeRegistry.register(CREEPER_BUDDY, CreeperBuddyEntity.createCustomCreeperAttributes());
		FabricDefaultAttributeRegistry.register(SPIDER_BUDDY, SpiderBuddyEntity.createCustomSpiderAttributes());
		FabricDefaultAttributeRegistry.register(ENDERMAN_BUDDY, EndermanBuddyEntity.createCustomEndermanAttributes());
		FabricDefaultAttributeRegistry.register(PIGLIN_BUDDY, PiglinBuddyEntity.createCustomPiglinAttributes());
		FabricDefaultAttributeRegistry.register(GHAST_BUDDY, GhastBuddyEntity.createCustomGhastAttributes());
		FabricDefaultAttributeRegistry.register(SHULKER_BUDDY, ShulkerBuddyEntity.createCustomShulkerAttributes());

		//Initialize Items
		ModItems.initialize();

		//Initialize Blocks
		ModBlocks.initialize();

		//Add ore generation
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MOB_ENERGY_ORE_PLACED_KEY);

		//Add Commands
		CommandRegistrationCallback.EVENT.register(MobBuddies::registerCommands);

		//Add Events
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			PlayerEntity player = handler.player;
			removeExistingMobBuddy(player, player.getWorld());
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((handler, server) -> {
			if(MOB_BUDDY_TYPES.contains(handler.getType())){
				PlayerData playerData = StateSaverAndLoader.getPlayerState(((IMobBuddyEntity) handler).getOwner());
				playerData.buddies.get(NBT_Names.get(handler.getType())).health = 5.0f;
				MobBuddies.LOGGER.info("ZombieBuddy has been destroyed!");
			}
		});
	}

	private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("specialsummon")
				.then(CommandManager.argument("choice", StringArgumentType.word())
						.suggests((context, builder) -> {
							builder.suggest("zombie-buddy");
							builder.suggest("skeleton-buddy");
							builder.suggest("creeper-buddy");
							builder.suggest("spider-buddy");
							builder.suggest("enderman-buddy");
							builder.suggest("piglin-buddy");
							builder.suggest("ghast-buddy");
							builder.suggest("shulker-buddy");
							return builder.buildFuture();
						})
						.executes(MobBuddies::executeSummonCommand) // Attach executes here
				)
		);
	}

	private static int executeSummonCommand(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();
		Vec3d pos = source.getPosition();

		BlockPos blockPos = BlockPos.ofFloored(pos);

		String choice = StringArgumentType.getString(context, "choice");
		PlayerEntity player = source.getPlayer();

		if(choice.equals("zombie-buddy")) {
			ZombieBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(()->Text.literal("Summoned a zombie buddy!"), true);
		}
		else if(choice.equals("skeleton-buddy")) {
			SkeletonBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a skeleton-buddy!"), true);
		}
		else if(choice.equals("creeper-buddy")) {
			CreeperBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a creeper-buddy!"), true);
		}
		else if(choice.equals("spider-buddy")) {
			SpiderBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a spider-buddy!"), true);
		}
		else if(choice.equals("enderman-buddy")) {
			EndermanBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a enderman-buddy!"), true);
		}
		else if(choice.equals("piglin-buddy")) {
			PiglinBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a piglin-buddy!"), true);
		}
		else if(choice.equals("ghast-buddy")) {
			GhastBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a ghast-buddy!"), true);
		}
		else if(choice.equals("shulker-buddy")) {
			ShulkerBuddyEntity.create(world, player, blockPos);
			source.sendFeedback(() -> Text.literal("Summoned a shulker-buddy!"), true);
		}
		else {
			context.getSource().sendError(Text.literal("Invalid choice!"));
		}

		return 1;
	}

	public static void removeExistingMobBuddy(PlayerEntity player, World world) {
		// Iterate through all entities in the world
		for (Entity entity : ((ServerWorld)world).getEntitiesByType(TypeFilter.instanceOf(Entity.class), EntityPredicates.VALID_ENTITY)) {
			if (MOB_BUDDY_TYPES.contains(entity.getType())) {
				if (entity instanceof IMobBuddyEntity MobBuddyEntity) {
					if (MobBuddyEntity.getOwner() == player) {
						PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
						playerData.buddies.get(NBT_Names.get(entity.getType())).health = ((LivingEntity) entity).getHealth();
						entity.discard();
						MobBuddies.LOGGER.info("Removed existing Mob Buddy for player: " + player.getName().getString());
					}
				}
			}
		}
	}
}