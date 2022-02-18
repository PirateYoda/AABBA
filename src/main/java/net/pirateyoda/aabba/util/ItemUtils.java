package net.pirateyoda.aabba.util;

import net.minecraft.item.ItemStack;

public class ItemUtils {
    public static boolean isItemEqual(final ItemStack a, final ItemStack b,
                                      final boolean matchNBT) {
        if (a.isEmpty() || b.isEmpty()) {
            return false;
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        return !matchNBT || ItemStack.areNbtEqual(a, b);
    }
}
