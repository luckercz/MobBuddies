package com.lucker.mobbuddies;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.Identifier;

public class BuddyPiglinEntityRenderer extends PiglinEntityRenderer {

	public BuddyPiglinEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_INNER_ARMOR, EntityModelLayers.PIGLIN_OUTER_ARMOR, false);
	}

	@Override
	public Identifier getTexture(MobEntity mobEntity) {
		return Identifier.ofVanilla("textures/entity/piglin/piglin.png");
	}
}