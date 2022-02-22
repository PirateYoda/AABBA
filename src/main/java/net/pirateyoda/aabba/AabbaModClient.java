package net.pirateyoda.aabba;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.pirateyoda.aabba.renderers.BarrelEntityRenderer;

import static net.pirateyoda.aabba.AabbaMod.BARREL_BLOCK_ENTITY;

public class AabbaModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(BARREL_BLOCK_ENTITY, BarrelEntityRenderer::new);
    }
}