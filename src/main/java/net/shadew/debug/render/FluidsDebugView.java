package net.shadew.debug.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldView;
import org.lwjgl.opengl.GL11;

import net.shadew.debug.api.render.DebugView;

@Environment(EnvType.CLIENT)
public class FluidsDebugView implements DebugView {
    private final MinecraftClient client;

    public FluidsDebugView(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void clear() {

    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        assert client.player != null;

        BlockPos playerPos = client.player.getBlockPos();
        WorldView world = client.player.world;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0, 1, 0, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(1);
        VertexConsumer buff = vertexConsumers.getBuffer(RenderLayer.getLines());

        matrices.push();
        matrices.translate(-cameraX, -cameraY, -cameraZ);

        Matrix4f matrix = matrices.peek().getModel();
        for (BlockPos pos : BlockPos.iterate(playerPos.add(-8, -8, -8), playerPos.add(8, 8, 8))) {
            FluidState fluid = world.getFluidState(pos);
            if (fluid.getLevel() <= 0) continue;
            float fheight = fluid.getHeight(world, pos);

            double height = pos.getY() + fheight;

            WorldRenderer.drawBox(
                matrices, buff,
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1, height + 0.01, pos.getZ() + 1,
                1, 1, 1, 1
            );

            Vec3d flow = fluid.getVelocity(world, pos);
            if (fheight < 0.4) {
                flow = flow.multiply(fheight);
            }

            float x1 = pos.getX() + 0.5f;
            float y1 = pos.getY() + fheight + 0.06f;
            float z1 = pos.getZ() + 0.5f;
            float x2 = x1 + (float) flow.x * 0.8f;
            float y2 = y1;
            float z2 = z1 + (float) flow.z * 0.8f;

            buff.vertex(matrix, x1, y1, z1).color(0f, 0f, 1f, 1f).next();
            buff.vertex(matrix, x2, y2, z2).color(0f, 0f, 1f, 1f).next();
        }

        matrices.pop();

        for (BlockPos pos : BlockPos.iterate(playerPos.add(-8, -8, -8), playerPos.add(8, 8, 8))) {
            FluidState fluid = world.getFluidState(pos);
            if (fluid.getLevel() <= 0) continue;
            DebugRenderer.drawString(
                String.valueOf(fluid.getLevel()),
                pos.getX() + 0.5, pos.getY() + fluid.getHeight(world, pos), pos.getZ() + 0.5,
                0xFF000000
            );
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public boolean isEnabled() {
        return DebugRenderers.FLUID_LEVELS_SHOWN.booleanValue();
    }
}
