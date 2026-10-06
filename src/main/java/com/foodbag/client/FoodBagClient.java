package com.foodbag.client;

import com.foodbag.FoodBagMod;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class FoodBagClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(FoodBagMod.MENU_TYPE, FoodBagScreen::new);
	}
}
