package net.pirateyoda.aabba.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.pirateyoda.aabba.AabbaMod;
import net.pirateyoda.aabba.util.ImplementedInventory;
import net.pirateyoda.aabba.util.ItemUtils;
import org.jetbrains.annotations.Nullable;

public class BarrelBlockEntity extends BlockEntity implements ImplementedInventory {
    private final int max_capacity = 32 * 64;  //max number of items
    private int stored_count = 0;
    private ItemStack storedStack = ItemStack.EMPTY;

    //Add items to the inventory
    public void addItems(ItemStack stack) {
        if (isFull()
            || (!isEmpty() && !this.isSameType(stack))) return;

        if (isEmpty()) storedStack = stack.copy();

        stored_count += stack.getCount();

        if (stored_count > max_capacity) {
            stack.setCount(stored_count - max_capacity); //overflow
            stored_count = max_capacity;
        } else {
            stack.setCount(0);
        }
    }

    public ItemStack removeItems(int count) {
        if (isEmpty()) return ItemStack.EMPTY;

        if (count > storedStack.getItem().getMaxCount()) count = storedStack.getItem().getMaxCount(); // trim to stack size
        if (count > stored_count) count = stored_count; //trim to available inventory

        stored_count -= count;

        ItemStack ret = storedStack.copy();
        ret.setCount(count);

        if (isEmpty()) storedStack = ItemStack.EMPTY;

        System.out.println("removed: " + count + "  stored count: " + stored_count);

        return ret;
    }

    public DefaultedList<ItemStack> removeAll() {
        int stacks = stored_count / getStackSize();
        int rem = stored_count % getStackSize();
        if (rem > 0) stacks++;

        DefaultedList<ItemStack> ret = DefaultedList.ofSize(stacks);

        storedStack.setCount(getStackSize());
        while ( ret.size() < stored_count / getStackSize() )
            ret.add(storedStack.copy());

        if (rem > 0) {
            storedStack.setCount(rem);
            ret.add(storedStack.copy());
        }

        //not really necessary but..
        stored_count = 0;
        storedStack = ItemStack.EMPTY;

        return ret;
    }

    public int getStackSize() {
        return storedStack.getItem().getMaxCount();
    }

    @Override
    public boolean isEmpty() {
        return freeSpace() == max_capacity;
    }

    private boolean isFull() {
        return freeSpace() == 0;
    }

    private int freeSpace() {
        return max_capacity - stored_count;
    }


    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(AabbaMod.BARREL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbt, items);
        storedStack = items.get(0).copy();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, DefaultedList.ofSize(1, storedStack));
        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }
    public boolean isSameType(ItemStack inputStack) {

        if (inputStack != ItemStack.EMPTY) {
            //return ItemUtils.isItemEqual(getStack(0), inputStack, true);
            return ItemUtils.isItemEqual(storedStack, inputStack, true);

        }
        return false;
    }

}
