package com.lucker.mobbuddies;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CubeEntityRenderer extends MobEntityRenderer<CubeEntity, CubeEntityModel> {

    public CubeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CubeEntityModel(context.getPart(MobBuddiesClient.MODEL_CUBE_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(CubeEntity entity) {
        return Identifier.of("mob-buddies", "textures/entity/cube/cube.png");
    }
}