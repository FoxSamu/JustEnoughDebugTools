package dev.runefox.jedt;

import dev.runefox.jedt.api.DebugClientInitializer;
import dev.runefox.jedt.api.MenuInitializer;
import dev.runefox.jedt.api.menu.Menu;
import dev.runefox.jedt.gui.DebugConfigScreen;
import dev.runefox.jedt.impl.menu.MenuManagerImpl;
import dev.runefox.jedt.impl.status.ServerDebugStatusImpl;
import dev.runefox.jedt.render.DebugBuffers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class DebugClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final MenuManagerImpl MENU_MANAGER = new MenuManagerImpl();
    public static final Menu ROOT_MENU = MENU_MANAGER.getMenu(Menu.ROOT);

    public static KeyMapping debugOptionsKey;
    public static boolean f6KeyDown = true;

    public static ServerDebugStatusImpl serverDebugStatus;

    public static final DebugBuffers BUFFERS = new DebugBuffers();

    @Override
    public void onInitializeClient() {
        serverDebugStatus = Debug.createStatusInstance();

        if (!Debug.GAMETEST)
            Debug.loadClientTests();

        reloadMenus();
        Debug.entrypoint(
            "jedt:client", DebugClientInitializer.class,
            init -> init.onInitializeDebugClient(serverDebugStatus)
        );

        KeyBindingHelper.registerKeyBinding(
            debugOptionsKey = new KeyMapping("key.jedt.options", GLFW.GLFW_KEY_F6, "key.categories.misc")
        );

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            DebugConfigScreen.INSTANCE.receiveTick();
            if (debugOptionsKey.isDown() && !f6KeyDown) {
                f6KeyDown = true;
                DebugConfigScreen.show();
            } else if (!debugOptionsKey.isDown()) {
                f6KeyDown = false;
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            int mx = (int) (client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / client.getWindow().getWidth());
            int my = (int) (client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / client.getWindow().getHeight());
            DebugConfigScreen.INSTANCE.receiveRender(matrixStack, mx, my, tickDelta);
        });
    }

    public static void reloadMenus() {
        LOGGER.info("Reloading menus");
        MENU_MANAGER.clearAll();
        new DefaultMenuInitializer().onInitializeDebugMenu(ROOT_MENU, MENU_MANAGER, serverDebugStatus);
        Debug.entrypoint(
            "jedt:menu", MenuInitializer.class,
            init -> init.onInitializeDebugMenu(ROOT_MENU, MENU_MANAGER, serverDebugStatus)
        );
    }
}
