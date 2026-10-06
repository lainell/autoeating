package com.foodbag.client;

import com.foodbag.FoodBagMenu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FoodBagScreen extends AbstractContainerScreen<FoodBagMenu> {
	public FoodBagScreen(FoodBagMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.imageWidth = 176;
		this.imageHeight = 133;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractBackground(graphics, mouseX, mouseY, delta);
		int x0 = this.leftPos;
		int y0 = this.topPos;
		// Панель в духе ванильного GUI
		graphics.fill(x0, y0, x0 + this.imageWidth, y0 + this.imageHeight, 0xFFC6C6C6);
		graphics.fill(x0, y0, x0 + this.imageWidth, y0 + 1, 0xFFFFFFFF);
		graphics.fill(x0, y0, x0 + 1, y0 + this.imageHeight, 0xFFFFFFFF);
		graphics.fill(x0, y0 + this.imageHeight - 1, x0 + this.imageWidth, y0 + this.imageHeight, 0xFF555555);
		graphics.fill(x0 + this.imageWidth - 1, y0, x0 + this.imageWidth, y0 + this.imageHeight, 0xFF555555);
		// Рамки ячеек
		for (Slot s : this.menu.slots) {
			int x = x0 + s.x - 1;
			int y = y0 + s.y - 1;
			graphics.fill(x, y, x + 18, y + 18, 0xFF373737);
			graphics.fill(x + 1, y + 1, x + 18, y + 18, 0xFFFFFFFF);
			graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
		}
	}
}
