package net.pirateyoda.aabba.blocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BarrelBlock extends Block {
    public static String NAMESPACE = "aabba";
    public static String PATH = "barrel";

    public BarrelBlock(Settings settings) {
        super(settings);
    }

    public void register() {
        Registry.register(Registry.BLOCK, new Identifier(net.pirateyoda.aabba.blocks.BarrelBlock.NAMESPACE, net.pirateyoda.aabba.blocks.BarrelBlock.PATH), this);
        Registry.register(Registry.ITEM, new Identifier(net.pirateyoda.aabba.blocks.BarrelBlock.NAMESPACE, net.pirateyoda.aabba.blocks.BarrelBlock.PATH), new BlockItem(this, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
