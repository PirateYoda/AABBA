package net.pirateyoda.aabba.blocks;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.pirateyoda.aabba.blockentity.BarrelBlockEntity;
import org.jetbrains.annotations.Nullable;

public class BarrelBlock extends Block implements BlockEntityProvider {
    public static String NAMESPACE = "aabba";
    public static String NAME = "barrel";
    public static String ENTITY_NAME = "barrel_block_entity";

    public BarrelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        BarrelBlockEntity inv = (BarrelBlockEntity) blockEntity;

        if (inv == null) throw new AssertionError();
        DefaultedList<ItemStack> all = inv.removeAll();

        for (ItemStack itemStack : all) player.getInventory().offerOrDrop(itemStack);
        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }

    @Override
    public MutableText getName() {
        return new TranslatableText(getTranslationKey());
    }

    @Override
    public String getTranslationKey() {
        return "block.aabba.barrel.tooltip";
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    //    if (world.isClient) return ActionResult.SUCCESS;
        //Creative mode?

        BarrelBlockEntity blockEntity = (BarrelBlockEntity) world.getBlockEntity(pos);
        if (null == blockEntity) return ActionResult.SUCCESS;

        if (player.getStackInHand(Hand.MAIN_HAND).isEmpty())
            blockEntity.addFromInventory(player.getInventory());

        blockEntity.addItems(player.getStackInHand(Hand.MAIN_HAND));

        return ActionResult.SUCCESS;
    }

    public BlockEntityType<BarrelBlockEntity> registerWithEntity(BarrelBlock instance) {

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
        {
            if (player.isSpectator()) return ActionResult.PASS;

            //TODO: if using appropriate tool for the block then don't remove, just break it

            if (world.getBlockState(pos).isOf(instance)) {
                BarrelBlockEntity inv = (BarrelBlockEntity) world.getBlockEntity(pos);
                if (null == inv) return ActionResult.PASS;

                int stack_size = (player.isSneaking()) ? 1 : inv.getStackSize();
                player.getInventory().offerOrDrop(inv.removeItems(stack_size));

                return ActionResult.PASS;
            }

            return ActionResult.PASS;
        });

        Registry.register(Registry.BLOCK, new Identifier(NAMESPACE, NAME), this);
        Registry.register(Registry.ITEM, new Identifier(NAMESPACE, NAME), new BlockItem(this, new FabricItemSettings().group(ItemGroup.MISC)));
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, String.format("%s:%s", NAMESPACE, ENTITY_NAME), FabricBlockEntityTypeBuilder.create(BarrelBlockEntity::new, instance).build(null));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BarrelBlockEntity(pos, state);
    }


}
