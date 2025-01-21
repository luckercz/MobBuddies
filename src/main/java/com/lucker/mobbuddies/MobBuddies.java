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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
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

	public static final EntityType<CubeEntity> CUBE = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of("mob-buddies", "cube"),
			EntityType.Builder.create(CubeEntity::new, SpawnGroup.CREATURE).dimensions(0.75f, 0.75f).build("cube")
	);


	public static final Set<EntityType<?>> MOB_BUDDY_TYPES = Set.of(
			ZOMBIE_BUDDY,
			CUBE
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
		FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createMobAttributes());

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
				if(handler.getType() == ZOMBIE_BUDDY){
					PlayerData playerData = StateSaverAndLoader.getPlayerState(((ZombieBuddyEntity) handler).getOwner());
					playerData.zombieBuddyHealth = 5.0f;
				}
				MobBuddies.LOGGER.info("ZombieBuddy has been destroyed!");
			}
		});
	}

	private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("specialsummon")
				.then(CommandManager.argument("choice", StringArgumentType.word())
						.suggests((context, builder) -> {
							builder.suggest("zombie-buddy");
							builder.suggest("notin");
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
		else if(choice.equals("notin")) {
			source.sendFeedback(()->Text.literal("Summoned nothing!!!"), true);
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
						if(entity instanceof ZombieBuddyEntity) {
							PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
							playerData.zombieBuddyHealth = ((ZombieBuddyEntity) entity).getHealth();
						}
						entity.discard();
						MobBuddies.LOGGER.info("Removed existing Mob Buddy for player: " + player.getName().getString());
					}
				}
			}
		}
	}
}