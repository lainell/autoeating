package com.foodbag;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FoodBagMenu extends AbstractContainerMenu {
	public static final int SIZE = BagContainer.SIZE;
	private final Container container;

	/** Клиентский конструктор. */
	public FoodBagMenu(int id, Inventory inv) {
		this(id, inv, new SimpleContainer(SIZE));
	}

	/** Серверный конструктор. */
	public FoodBagMenu(int id, Inventory inv, Container container) {
		super(FoodBagMod.MENU_TYPE, id);
		checkContainerSize(container, SIZE);
		this.container = container;

		for (int i = 0; i < SIZE; i++) {
			this.addSlot(new Slot(container, i, 35 + i * 18, 20) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.has(DataComponents.FOOD);
				}
			});
		}
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
			}
		}
		for (int col = 0; col < 9; col++) {
			this.addSlot(new Slot(inv, col, 8 + col * 18, 109));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack = slot.getItem();
			result = stack.copy();
			if (index < SIZE) {
				if (!this.moveItemStackTo(stack, SIZE, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack, 0, SIZE, false)) {
				return ItemStack.EMPTY;
			}
			if (stack.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return result;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (container instanceof BagContainer bag && player instanceof ServerPlayer sp) {
			bag.saveTo(sp);
		}
	}
}
