package com.foodbag;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Пакет клиент -> сервер: "открой мою сумку с едой". */
public record OpenBagPayload() implements CustomPacketPayload {
	public static final Type<OpenBagPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(FoodBagMod.MOD_ID, "open_bag"));
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenBagPayload> CODEC =
			StreamCodec.unit(new OpenBagPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
