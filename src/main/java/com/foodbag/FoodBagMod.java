package com.foodbag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FoodBagMod implements ModInitializer {
	public static final String MOD_ID = "foodbag";

	public static final AttachmentType<List<ItemStack>> BAG = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(MOD_ID, "bag"),
			builder -> builder
					.persistent(Codec.list(ItemStack.OPTIONAL_CODEC))
					.copyOnDeath());

	public static final MenuType<FoodBagMenu> MENU_TYPE = Registry.register(
			BuiltInRegistries.MENU,
			Identifier.fromNamespaceAndPath(MOD_ID, "food_bag"),
			new MenuType<>(FoodBagMenu::new, FeatureFlagSet.of()));

	/** Еда с плохими/странными эффектами — автоматически не едим. */
	private static final Set<Item> NEVER_AUTO_EAT = Set.of(
			Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.POISONOUS_POTATO,
			Items.PUFFERFISH, Items.CHORUS_FRUIT);

	private static final int CHECK_INTERVAL_TICKS = 10;

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundPlay().register(OpenBagPayload.TYPE, OpenBagPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(OpenBagPayload.TYPE, (payload, context) -> {
			ServerPlayer player = context.player();
			player.openMenu(new SimpleMenuProvider(
					(id, inv, p) -> new FoodBagMenu(id, inv, BagContainer.load(player)),
					Component.literal("Сумка с едой")));
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % CHECK_INTERVAL_TICKS != 0) {
				return;
			}
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				tryAutoEat(player);
			}
		});
	}

	private static void tryAutoEat(ServerPlayer player) {
		if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
			return;
		}
		// Пока сумка открыта, актуальное содержимое лежит в меню — не трогаем.
		if (player.containerMenu instanceof FoodBagMenu) {
			return;
		}
		List<ItemStack> saved = player.getAttached(BAG);
		if (saved == null || saved.isEmpty()) {
			return;
		}

		int missing = 20 - player.getFoodData().getFoodLevel();
		if (missing <= 0) {
			return;
		}

		// Выбираем еду, которая восстанавливает больше всего, но не больше, чем не хватает.
		int bestIndex = -1;
		int bestNutrition = 0;
		for (int i = 0; i < saved.size(); i++) {
			ItemStack stack = saved.get(i);
			if (stack.isEmpty() || NEVER_AUTO_EAT.contains(stack.getItem())) {
				continue;
			}
			FoodProperties food = stack.get(DataComponents.FOOD);
			if (food == null) {
				continue;
			}
			int nutrition = food.nutrition();
			if (nutrition <= missing && nutrition > bestNutrition) {
				bestNutrition = nutrition;
				bestIndex = i;
			}
		}
		if (bestIndex < 0) {
			return;
		}

		List<ItemStack> updated = new ArrayList<>(saved.size());
		for (ItemStack s : saved) {
			updated.add(s.copy());
		}
		ItemStack stack = updated.get(bestIndex);
		ItemStack result = stack.finishUsingItem(player.level(), player);
		// Остаток (например миска от супа) возвращаем игроку.
				// Остаток (например миска от супа) возвращаем игроку.
		if (result != stack && !result.isEmpty()) {
			if (!player.getInventory().add(result) && stack.isEmpty()) {
				// инвентарь полон — оставляем остаток в той же ячейке сумки
				updated.set(bestIndex, result);
			}
		}
		player.setAttached(BAG, updated);
	}
}
