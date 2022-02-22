package net.pirateyoda.aabba.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.pirateyoda.aabba.AabbaMod;
import net.pirateyoda.aabba.util.ItemUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class BarrelBlockEntity extends BlockEntity implements Inventory {
    private final int max_capacity = 32 * 64;  //max number of items
    private int stored_count = 0;
    private ItemStack storedStack = ItemStack.EMPTY;


    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(AabbaMod.BARREL_BLOCK_ENTITY, pos, state);

        this.clear();
    }

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

        markDirty();
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

        markDirty();

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

        markDirty();

        return ret;
    }

    public int getStackSize() {
        return storedStack.getItem().getMaxCount();
    }

    @Override
    public int size() {
        return max_capacity;
    }

    @Override
    public boolean isEmpty() {
        return freeSpace() == max_capacity;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (!isEmpty()) {
            storedStack.setCount(getStackSize());
        }

        return storedStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeItems(amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeItems(storedStack.getItem().getMaxCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        addItems(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    private boolean isFull() {
        return freeSpace() == 0;
    }

    private int freeSpace() {
        return max_capacity - stored_count;
    }


    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        storedStack = ItemStack.EMPTY;

        stored_count = nbt.getInt("stored_count");
        if (!nbt.getBoolean("isEmpty")) {
            storedStack = ItemStack.fromNbt(nbt.getCompound("storedStack"));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

        nbt.putInt("stored_count", stored_count);
        nbt.putBoolean("isEmpty", this.isEmpty());
        if (!isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            storedStack.writeNbt(nbtCompound);
            nbt.put("storedStack", nbtCompound);
        }

        super.writeNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        NbtCompound nbtTag = new NbtCompound();
        writeNbt(nbtTag);
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public boolean isSameType(ItemStack inputStack) {

        if (inputStack != ItemStack.EMPTY) {
            //return ItemUtils.isItemEqual(getStack(0), inputStack, true);
            return ItemUtils.isItemEqual(storedStack, inputStack, true);

        }
        return false;
    }

    @Override
    public void clear() {
        stored_count = 0;
        storedStack = ItemStack.EMPTY;
    }

    public void addFromInventory(Inventory inventory) {
        if (isEmpty()
                || inventory.isEmpty()
                || !inventory.containsAny(Set.of(storedStack.getItem())))
            return;
        for (int idx = inventory.size() -1; idx >=0; --idx) {
                addItems(inventory.getStack(idx));
        }

        markDirty();
    }

}
