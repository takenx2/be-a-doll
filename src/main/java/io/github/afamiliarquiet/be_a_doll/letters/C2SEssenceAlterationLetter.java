package io.github.afamiliarquiet.be_a_doll.letters;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.BeASelf;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SEssenceAlterationLetter(boolean inserting) implements CustomPacketPayload {
	public static final Type<C2SEssenceAlterationLetter> TYPE = new Type<>(BeADoll.id("essence_alteration_letter"));

	public static final StreamCodec<ByteBuf, C2SEssenceAlterationLetter> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL,
		C2SEssenceAlterationLetter::inserting,
		C2SEssenceAlterationLetter::new
	);

	public static void receive(C2SEssenceAlterationLetter letter, ServerPlayNetworking.Context context) {
		InventoryMenu handler = context.player().inventoryMenu;
		BeADoll.LOGGER.info("{}",handler.getCarried());
		ItemStack clickProcessedStack = BeASelf.clickSelf(handler.getCarried(), context.player(), letter.inserting());
		if (clickProcessedStack != null && !context.player().isCreative()) {
			handler.setCarried(clickProcessedStack);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
