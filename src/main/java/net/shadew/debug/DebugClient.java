package net.shadew.debug;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.entrypoint.minecraft.hooks.EntrypointUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.shadew.debug.api.DebugClientInitializer;
import net.shadew.debug.api.DebugMenuInitializer;
import net.shadew.debug.api.menu.DebugMenu;
import net.shadew.debug.gui.DebugConfigScreen;
import net.shadew.debug.impl.menu.DebugMenuManagerImpl;
import net.shadew.debug.impl.status.ServerDebugStatusImpl;

@Environment(EnvType.CLIENT)
public class DebugClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DebugMenuManagerImpl MENU_MANAGER = new DebugMenuManagerImpl();
    public static final DebugMenu ROOT_MENU = MENU_MANAGER.getMenu("debug:root");

    public static KeyBinding debugOptionsKey;
    public static boolean f6KeyDown = true;

    public static ServerDebugStatusImpl serverDebugStatus;

    @Override
    public void onInitializeClient() {
        serverDebugStatus = Debug.createStatusInstance();

        new DefaultMenuInitializer().onInitializeDebugMenu(ROOT_MENU, MENU_MANAGER, serverDebugStatus);
        EntrypointUtils.invoke(
            "debug:menu", DebugMenuInitializer.class,
            init -> init.onInitializeDebugMenu(ROOT_MENU, MENU_MANAGER, serverDebugStatus)
        );
        EntrypointUtils.invoke(
            "debug:client", DebugClientInitializer.class,
            init -> init.onInitializeDebugClient(serverDebugStatus)
        );

        KeyBindingHelper.registerKeyBinding(
            debugOptionsKey = new KeyBinding("key.debug.options", GLFW.GLFW_KEY_F6, "key.categories.misc")
        );

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            DebugConfigScreen.INSTANCE.receiveTick();
            if (debugOptionsKey.isPressed() && !f6KeyDown) {
                f6KeyDown = true;
                DebugConfigScreen.show();
            } else if(!debugOptionsKey.isPressed()) {
                f6KeyDown = false;
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            int mx = (int)(client.mouse.getX() * (double)client.getWindow().getScaledWidth() / client.getWindow().getWidth());
            int my = (int)(client.mouse.getY() * (double)client.getWindow().getScaledHeight() / client.getWindow().getHeight());
            DebugConfigScreen.INSTANCE.receiveRender(matrixStack, mx, my, tickDelta);
        });
    }
}
