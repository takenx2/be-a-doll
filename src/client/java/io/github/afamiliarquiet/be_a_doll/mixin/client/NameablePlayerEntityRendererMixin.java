package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.DollishState;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class NameablePlayerEntityRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> extends LivingEntityRenderer<AvatarlikeEntity, AvatarRenderState, PlayerModel> {

	public NameablePlayerEntityRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadow) {
		super(context, model, shadow);
	}

	@Inject(at = @At("HEAD"), method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V")
	private void alsoCheckDollness(Entity entity, EntityRenderState state, float f, CallbackInfo ci) {
		if (entity instanceof Player player) {
			DollishState dollishState = (DollishState)state;
			dollishState.be_a_doll$setDoll(BeAMaid.isDoll(player));
			// no need for now
//			dollishState.be_a_doll$setVariant(BeALibrarian.inspectDollMaterial(player));
			dollishState.be_a_doll$setDollName(BeALibrarian.inspectDollLabel(player));
			dollishState.be_a_doll$setTargeted(player == this.entityRenderDispatcher.crosshairPickEntity || player == Minecraft.getInstance().getCameraEntity());
	//		if (dollishState.be_a_doll$isDoll() && state.squaredDistanceToCamera < 4096.0 && player == this.dispatcher.targetedEntity || player == MinecraftClient.getInstance().getCameraEntity()) {
	//			// todone - add f3 override for moderation + doll name from nametag
	//			dollishState.be_a_doll$setDollName(BeALibrarian.inspectDollLabel(player));
	//			if (dollishState.be_a_doll$getDollName() == null) { // notodo - remove this probably? but... it seems like attachments are broken
	//				dollishState.be_a_doll$setDollName(this.getDisplayName(player));
	//			}
	//		} else {
	//			dollishState.be_a_doll$setDollName(null);
	//		}
		}

	}

	@WrapMethod(method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V")
	private void butDollsAreNoDifferent(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState, Operation<Void> original) {
		DollishState dollishState = (DollishState) state;
		if (dollishState.be_a_doll$isDoll() && !Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
			if (dollishState.be_a_doll$isTargeted()) {
				if (dollishState.be_a_doll$getDollName() != null) {
					original.call(state, poseStack, collector, cameraRenderState);
					return;
				} // else { defer to the grand elser }
			} else {
				return; // don't render if doll and not targeted
			}
		} // else { defer to the grand elser }

		// the grand elser
		original.call(state, poseStack, collector, cameraRenderState);
	}
}
