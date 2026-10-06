package com.foodbag;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/** 6 ячеек, хранящиеся в данных игрока (сохраняются вместе с миром). */
public class BagContainer extends SimpleContainer {
	public static final int SIZE = 6;

	public BagContainer() {
		super(SIZE);
	}

	public static BagContainer load(ServerPlayer player) {
		BagContainer c = new BagContainer();
		List<ItemStack> saved = player.getAttachedOrElse(FoodBagMod.BAG, List.of());
		for (int i = 0; i < Math.min(SIZE, saved.size()); i++) {
			c.setItem(i, saved.get(i).copy());
		}
		return c;
	}

	public void saveTo(ServerPlayer player) {
		List<ItemStack> out = new ArrayList<>(SIZE);
		for (int i = 0; i < SIZE; i++) {
			out.add(getItem(i).copy());
		}
		player.setAttached(FoodBagMod.BAG, out);
	}
}
