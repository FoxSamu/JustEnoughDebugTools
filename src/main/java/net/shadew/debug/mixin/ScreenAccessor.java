package net.shadew.debug.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor("children")
    List<GuiEventListener> getChildren();
    @Accessor("renderables")
    List<Renderable> getRenderables();
}
