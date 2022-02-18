package net.pirateyoda.aabba;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import net.pirateyoda.aabba.blockentity.BarrelBlockEntity;
import net.pirateyoda.aabba.blocks.BarrelBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AabbaMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("aabba");

	public static final BarrelBlock BARREL_BLOCK = new BarrelBlock(FabricBlockSettings.of(Material.WOOD).strength(4.0f).requiresTool());
	public static BlockEntityType<BarrelBlockEntity> BARREL_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Aabba initialization started");

		BARREL_BLOCK_ENTITY = BARREL_BLOCK.registerWithEntity(BARREL_BLOCK);

		LOGGER.info("Aabba initialization completed");
	}
}
