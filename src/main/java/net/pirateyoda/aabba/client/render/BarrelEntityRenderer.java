package net.pirateyoda.aabba.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.pirateyoda.aabba.blockentity.BarrelBlockEntity;

/**
 * Core item and text functionality taken with gratitude from TechReborn
 */
//TODO:Implement directionality on the BarrelBLock so we can render the items properly
public class BarrelEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity> {

    public BarrelEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BarrelBlockEntity barrel, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (barrel.getWorld() == null) {
            return;
        }

        if (!barrel.isEmpty()) {
            ItemStack stack = barrel.getStack(0);
            Direction facing = Direction.NORTH;  //No facing on block use north for now

            //Item Image
            matrices.push();

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((facing.getHorizontal() - 2) * 90F));
            matrices.scale(0.5F, 0.5F, 0.5F);
            switch (facing) {
                case NORTH, WEST -> matrices.translate(1, 1, 0);
                case SOUTH -> matrices.translate(-1, 1, -2);
                case EAST -> matrices.translate(-1, 1, 2);
            }
            //int lightAbove = WorldRenderer.getLightmapCoordinates(barrel.getWorld(), barrel.getPos().offset(barrel.getFacing()));
            int lightAbove = WorldRenderer.getLightmapCoordinates(barrel.getWorld(), barrel.getPos().offset(facing));
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
            matrices.pop();

            //Text
            matrices.push();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            //Direction facing = storage.getFacing();

            // Render item only on horizontal facing #2183
            if (Direction.Type.HORIZONTAL.test(facing) ){
                matrices.translate(0.5, 0.5, 0.5); // Translate center
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-facing.rotateYCounterclockwise().asRotation() + 90)); // Rotate depending on face
                matrices.translate(0, 0, -0.505); // Translate forward
            }

            matrices.scale(-0.01f, -0.01F, -0.01f);

            float xPosition;

              //Item Count
            String count = String.valueOf(barrel.getStoredAmount());
            xPosition = (float) (-textRenderer.getWidth(count) / 2);
            textRenderer.draw(count, xPosition, -4f + 40, 0, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);

              //Item Name
            String item = stack.getName().asTruncatedString(18);
            xPosition = (float) (-textRenderer.getWidth(item) / 2);
            textRenderer.draw(item, xPosition, -4f - 40, 0, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);

            matrices.pop();
        }
    }
}