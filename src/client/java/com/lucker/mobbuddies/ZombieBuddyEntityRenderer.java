package com.lucker.mobbuddies;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class ZombieBuddyEntityRenderer extends MobEntityRenderer<ZombieBuddyEntity, ZombieEntityModel<ZombieBuddyEntity>> {

    public ZombieBuddyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieEntityModel<ZombieBuddyEntity>(context.getPart(EntityModelLayers.ZOMBIE) ), 1);
    }

    @Override
    public Identifier getTexture(ZombieBuddyEntity entity) {
        return Identifier.of("mob-buddies", "textures/entity/zombie-buddy/zombie-buddy.png");
    }
}