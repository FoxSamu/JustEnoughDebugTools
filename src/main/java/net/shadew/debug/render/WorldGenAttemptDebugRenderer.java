package net.shadew.debug.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@Environment(EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer implements DebugRenderer.Renderer {
   private final List<BlockPos> pos = Lists.newArrayList();
   private final List<Float> boxSizes = Lists.newArrayList();
   private final List<Float> alpha = Lists.newArrayList();
   private final List<Float> red = Lists.newArrayList();
   private final List<Float> green = Lists.newArrayList();
   private final List<Float> blue = Lists.newArrayList();

   public void method_3872(BlockPos blockPos, float f, float g, float h, float i, float j) {
      pos.add(blockPos);
      boxSizes.add(f);
      alpha.add(j);
      red.add(g);
      green.add(h);
      blue.add(i);
   }

   @Override
   @SuppressWarnings("deprecation")
   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();

      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      buff.begin(5, VertexFormats.POSITION_COLOR);

      for(int i = 0; i < pos.size(); ++i) {
         BlockPos blockPos = pos.get(i);
         Float boxSize = boxSizes.get(i);
         float boxRadius = boxSize / 2.0F;
         WorldRenderer.drawBox(
             buff,
             blockPos.getX() + 0.5 - boxRadius - cameraX,
             blockPos.getY() + 0.5 - boxRadius - cameraY,
             blockPos.getZ() + 0.5 - boxRadius - cameraZ,
             blockPos.getX() + 0.5 + boxRadius - cameraX,
             blockPos.getY() + 0.5 + boxRadius - cameraY,
             blockPos.getZ() + 0.5 + boxRadius - cameraZ,
             red.get(i), green.get(i), blue.get(i), alpha.get(i)
         );
      }

      tess.draw();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }
}
