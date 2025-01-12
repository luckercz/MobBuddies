package com.lucker.mobbuddies;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
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


	public static final RegistryKey<PlacedFeature> MOB_ENERGY_ORE_PLACED_KEY = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("mob-buddies", "mob_energy_ore_custom"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		//Initialize Buddies
		FabricDefaultAttributeRegistry.register(ZOMBIE_BUDDY, ZombieBuddyEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(CUBE, CubeEntity.createMobAttributes());

		//Initialize Items
		ModItems.initialize();

		//Initialize Blocks
		ModBlocks.initialize();

		//Add ore generation
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, MOB_ENERGY_ORE_PLACED_KEY);

		//Add commands
		CommandRegistrationCallback.EVENT.register(MobBuddies::registerCommands);
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

		if(choice.equals("zombie-buddy")) {
			ZOMBIE_BUDDY.spawn(world, blockPos, SpawnReason.COMMAND);
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
}