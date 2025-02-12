package com.lucker.mobbuddies;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.util.Identifier;

public class MyScreen extends BaseUIModelScreen<FlowLayout> {

    public MyScreen(){
        super(FlowLayout.class, DataSource.asset(Identifier.of("mob-buddies", "my_ui_model")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "the-button").onPress(button -> {
            System.out.println("click");
        });
    }
}
