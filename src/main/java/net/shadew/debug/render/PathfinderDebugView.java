package net.shadew.debug.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;
import java.util.Map;

import net.shadew.debug.api.render.DebugView;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class PathfinderDebugView implements DebugView {
    public static boolean enabled = false;

    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> pathNodeSizes = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();

    public void addPath(int id, Path path, float nodeSize) {
        paths.put(id, path);
        pathTimes.put(id, Util.getMeasuringTimeMs());
        pathNodeSizes.put(id, nodeSize);
    }

    @Override
    public void clear() {
        paths.clear();
        pathNodeSizes.clear();
        pathTimes.clear();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (!this.paths.isEmpty()) {
            long time = Util.getMeasuringTimeMs();

            for (Integer id : paths.keySet()) {
                Path path = paths.get(id);
                float nodeSize = pathNodeSizes.get(id);
                drawPath(path, nodeSize, true, true, cameraX, cameraY, cameraZ);
            }

            Integer[] ids = pathTimes.keySet().toArray(new Integer[0]);
            for (int id : ids) {
                if (time - pathTimes.get(id) > 5000) {
                    paths.remove(id);
                    pathTimes.remove(id);
                }
            }

        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static void drawPath(Path path, float nodeSize, boolean drawOpenAndClosedList, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0, 1, 0, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6);
        drawPathInternal(path, nodeSize, drawOpenAndClosedList, drawLabels, cameraX, cameraY, cameraZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void drawPathInternal(Path path, float nodeSize, boolean drawOpenAndClosedList, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        drawPathLines(path, cameraX, cameraY, cameraZ);
        BlockPos target = path.getTarget();
        if (getManhattanDistance(target, cameraX, cameraY, cameraZ) <= 80) {
            // Green target box
            DebugRenderer.drawBox(
                new Box(
                    target.getX() + 0.25,
                    target.getY() + 0.25,
                    target.getZ() + 0.25,
                    target.getX() + 0.75,
                    target.getY() + 0.75,
                    target.getZ() + 0.75
                ).offset(-cameraX, -cameraY, -cameraZ),
                0, 1, 0, 0.5f
            );

            for (int i = 0; i < path.getLength(); i++) {
                PathNode node = path.getNode(i);
                if (getManhattanDistance(node.getPos(), cameraX, cameraY, cameraZ) <= 80) {
                    float red = i == path.getCurrentNodeIndex() ? 1 : 0;
                    float blue = i == path.getCurrentNodeIndex() ? 0 : 1;
                    DebugRenderer.drawBox(
                        new Box(
                            node.x + 0.5 - nodeSize,
                            node.y + 0.01 * i, // Prevent z-fighting, add small offsets vertically
                            node.z + 0.5 - nodeSize,
                            node.x + 0.5 + nodeSize,
                            node.y + 0.25 + 0.01 * i,
                            node.z + 0.5F + nodeSize
                        ).offset(-cameraX, -cameraY, -cameraZ),
                        red, 0, blue, 0.5f
                    );
                }
            }
        }

        if (drawOpenAndClosedList) {
            PathNode[] closedList = path.method_22881();
            for (PathNode node : closedList) {
                if (getManhattanDistance(node.getPos(), cameraX, cameraY, cameraZ) <= 80) {
                    DebugRenderer.drawBox(
                        new Box(
                            node.x + 0.5 - nodeSize / 2.0,
                            node.y + 0.01,
                            node.z + 0.5 - nodeSize / 2.0,
                            node.x + 0.5 + nodeSize / 2.0,
                            node.y + 0.1,
                            node.z + 0.5F + nodeSize / 2.0
                        ).offset(-cameraX, -cameraY, -cameraZ),
                        1f, 0.8f, 0.8f, 0.5f
                    );
                }
            }

            PathNode[] openList = path.method_22880();
            for (PathNode node : openList) {
                if (getManhattanDistance(node.getPos(), cameraX, cameraY, cameraZ) <= 80) {
                    DebugRenderer.drawBox(
                        new Box(
                            node.x + 0.5 - nodeSize / 2.0,
                            node.y + 0.01,
                            node.z + 0.5 - nodeSize / 2.0,
                            node.x + 0.5 + nodeSize / 2.0,
                            node.y + 0.1,
                            node.z + 0.5 + nodeSize / 2.0
                        ).offset(-cameraX, -cameraY, -cameraZ),
                        0.8f, 1f, 1f, 0.5f
                    );
                }
            }
        }

        if (drawLabels) {
            for (int i = 0; i < path.getLength(); i++) {
                PathNode pathNode = path.getNode(i);
                if (getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) <= 80) {
                    DebugRenderer.drawString(
                        String.format("%s", pathNode.type),
                        pathNode.x + 0.5,
                        pathNode.y + 0.75,
                        pathNode.z + 0.5,
                        0xFFFFFFFF, 0.02F, true, 0, true
                    );
                    DebugRenderer.drawString(
                        String.format(Locale.ROOT, "%.2f", pathNode.penalty),
                        pathNode.x + 0.5,
                        pathNode.y + 0.25,
                        pathNode.z + 0.5,
                        0xFFFFFFFF, 0.02F, true, 0, true
                    );
                }
            }
        }
    }

    public static void drawPathLines(Path path, double cameraX, double cameraY, double cameraZ) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buff = tess.getBuffer();
        buff.begin(3, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < path.getLength(); i++) {
            PathNode node = path.getNode(i);
            if (getManhattanDistance(node.getPos(), cameraX, cameraY, cameraZ) <= 80) {
                float hue = (float) i / path.getLength() * 0.33F;
                int color = i == 0 ? 0 : MathHelper.hsvToRgb(hue, 0.9F, 0.9F);
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                buff.vertex(node.x - cameraX + 0.5, node.y - cameraY + 0.5, node.z - cameraZ + 0.5)
                    .color(r, g, b, 255)
                    .next();
            }
        }

        tess.draw();
    }

    private static double getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return Math.abs(pos.getX() - x) + Math.abs(pos.getY() - y) + Math.abs(pos.getZ() - z);
    }
}
