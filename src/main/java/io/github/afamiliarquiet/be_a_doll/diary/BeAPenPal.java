package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.letters.C2SCreativeEssenceAlterationLetter;
import io.github.afamiliarquiet.be_a_doll.letters.C2SEssenceAlterationLetter;
import io.github.afamiliarquiet.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollDismountLetter;
import io.github.afamiliarquiet.be_a_doll.letters.S2CDollRepairedLetter;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class BeAPenPal {
	public static void fillPen() {


		PayloadTypeRegistry.serverboundPlay().register(C2SEssenceAlterationLetter.TYPE, C2SEssenceAlterationLetter.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(C2SCreativeEssenceAlterationLetter.TYPE, C2SCreativeEssenceAlterationLetter.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SEssenceAlterationLetter.TYPE, C2SEssenceAlterationLetter::receive);
		ServerPlayNetworking.registerGlobalReceiver(C2SCreativeEssenceAlterationLetter.TYPE, C2SCreativeEssenceAlterationLetter::receive);

		PayloadTypeRegistry.clientboundPlay().register(S2CDollRepairedLetter.TYPE, S2CDollRepairedLetter.STREAM_CODEC);
		PayloadTypeRegistry.clientboundPlay().register(S2CDollDismountLetter.TYPE, S2CDollDismountLetter.STREAM_CODEC);

		PayloadTypeRegistry.serverboundPlay().register(C2SKeysmashConfigSyncLetter.TYPE, C2SKeysmashConfigSyncLetter.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SKeysmashConfigSyncLetter.TYPE, C2SKeysmashConfigSyncLetter::receive);
	}
}
