package com.lucker.mobbuddies;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MyScreen extends BaseOwoScreen<FlowLayout> {

    // Path to the custom background image
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("mob-buddies", "textures/gui/book_page.png");

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {

        FlowLayout layout = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER);

        //Define Components
        Component backgroundComponent = Components.texture(BACKGROUND_TEXTURE, 0, 0, 256, 256)
                .sizing(Sizing.fixed(300), Sizing.fixed(200));

        Component leftButtonComponent =  Containers.verticalFlow(Sizing.content() /**/, Sizing.content())
                .child((Component) Components.button(Text.literal("   Summon   "), button -> {
                    // Get current client player
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;

                    // Send command
                    player.networkHandler.sendChatCommand("specialsummon zombie-buddy");
                }))
                .padding(Insets.of(4)) //
                .surface(Surface.DARK_PANEL)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .sizing(Sizing.fixed(75), Sizing.fixed(20))
                .positioning(Positioning.relative(23,86));

        Component rightButtonComponent =  Containers.verticalFlow(Sizing.content() /**/, Sizing.content())
                .child((Component) Components.button(Text.literal("   Summon   "), button -> {
                    // Get current client player
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;

                    // Send command
                    player.networkHandler.sendChatCommand("specialsummon skeleton-buddy");
                }))
                .padding(Insets.of(4)) //
                .surface(Surface.DARK_PANEL)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .sizing(Sizing.fixed(75), Sizing.fixed(20))
                .positioning(Positioning.relative(77,86));

        //Define screen using Components
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        layout.child(backgroundComponent);
        layout.child(leftButtonComponent);
        layout.child(rightButtonComponent);

        rootComponent.child(layout);

//        rootComponent.child(backgroundComponent);
//        rootComponent.child(leftButtonComponent);
//        rootComponent.child(rightButtonComponent);
    }
}