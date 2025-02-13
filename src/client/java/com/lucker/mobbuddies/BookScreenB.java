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

public class BookScreenB extends BaseOwoScreen<FlowLayout> {

    // Path to the custom background image
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("mob-buddies", "textures/gui/book_page_b.png");

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

        Component leftButtonComponent = ScreenHelper.CreateSummonButton("creeper-buddy", 25);
        Component rightButtonComponent =  ScreenHelper.CreateSummonButton("spider-buddy", 75);

        Component previousButtonComponent = ScreenHelper.CreateChangeButton("<-", new BookScreenA(), 10);
        Component nextButtonComponent = ScreenHelper.CreateChangeButton(">", new BookScreenC(), 89);

        //Define screen using Components
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        layout.child(backgroundComponent);
        layout.child(leftButtonComponent);
        layout.child(rightButtonComponent);
        layout.child(previousButtonComponent);
        layout.child(nextButtonComponent);

        rootComponent.child(layout);
    }
}