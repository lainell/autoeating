package com.foodbag.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.foodbag.OpenBagPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {
	@Unique
	private Button foodbag$button;

	protected InventoryScreenMixin(InventoryMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void foodbag$addButton(CallbackInfo ci) {
		// Кнопка книги рецептов: x = leftPos + 104, y = height / 2 - 22, размер 20x18.
		// Наша — сразу справа от неё.
		this.foodbag$button = Button.builder(Component.literal("F"),
						b -> ClientPlayNetworking.send(new OpenBagPayload()))
				.bounds(this.leftPos + 128, this.height / 2 - 22, 20, 18)
				.tooltip(Tooltip.create(Component.literal("Сумка с едой")))
				.build();
		this.addRenderableWidget(this.foodbag$button);
	}

	// Когда открывается книга рецептов, окно сдвигается — двигаем кнопку следом.
	@Inject(method = "containerTick", at = @At("TAIL"), require = 0)
	private void foodbag$follow(CallbackInfo ci) {
		if (this.foodbag$button != null) {
			this.foodbag$button.setPosition(this.leftPos + 128, this.height / 2 - 22);
		}
	}
}
