package io.github.afamiliarquiet.be_a_doll;

import io.github.afamiliarquiet.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import io.github.afamiliarquiet.be_a_doll.personal_diary.BeALocalBug;
import io.github.afamiliarquiet.be_a_doll.personal_diary.BeALocalPenPal;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class BeALocalDoll implements ClientModInitializer {
	public static final BeALocalTinkerer CLIENT_CONFIG = BeALocalTinkerer.createToml(FabricLoader.getInstance().getConfigDir(), "", BeADoll.MOD_ID, BeALocalTinkerer.class);

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BeALocalPenPal.fillPen();
		BeALocalBug.lookAtBug();

		ClientTickEvents.START_CLIENT_TICK.register((client -> {
			if (CLIENT_CONFIG.dirty) {
				if (ClientPlayNetworking.canSend(C2SKeysmashConfigSyncLetter.TYPE)) {
					ClientPlayNetworking.send(CLIENT_CONFIG.writtenForAFriend());
				}
				CLIENT_CONFIG.dirty = false;
			}
		}));
	}
}
