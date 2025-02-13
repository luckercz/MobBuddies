package com.lucker.mobbuddies;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class ScreenHelper {
    public static Component CreateSummonButton(String buddy, int x){
        Component ButtonComponent =  Containers.verticalFlow(Sizing.content() /**/, Sizing.content())
                .child((Component) Components.button(Text.literal("   Summon   "), button -> {
                    // Get current client player
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;

                    // Send command
                    player.networkHandler.sendChatCommand("specialsummon " + buddy);
                }))
                .padding(Insets.of(4)) //
                .surface(Surface.DARK_PANEL)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .sizing(Sizing.fixed(75), Sizing.fixed(20))
                .positioning(Positioning.relative(x,86));

        return ButtonComponent;
    }

    public static Component CreateChangeButton(String symbol, Screen screen, int x){
        Component ButtonComponent = Containers.verticalFlow(Sizing.content() /**/, Sizing.content())
                .child((Component) Components.button(Text.literal(symbol), button -> {
                    MinecraftClient.getInstance().setScreen(screen);
                }))
                .padding(Insets.of(4,4,2,2)) //
                .surface(Surface.DARK_PANEL)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .sizing(Sizing.fixed(25), Sizing.fixed(20))
                .positioning(Positioning.relative(x,86));

        return ButtonComponent;
    }
}
