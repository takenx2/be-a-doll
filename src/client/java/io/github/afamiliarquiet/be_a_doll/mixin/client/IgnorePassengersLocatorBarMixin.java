package io.github.afamiliarquiet.be_a_doll.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.LocatorBar;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.UUID;

@Mixin(LocatorBar.class)
public abstract class IgnorePassengersLocatorBarMixin implements ContextualBar {
	//for some gosh darn reason this was being forced to be static??? -taken
	@Definition(id = "equals", method = "Ljava/util/UUID;equals(Ljava/lang/Object;)Z")
	@Expression("?.equals(?)")
	@ModifyExpressionValue(method = "lambda$extractRenderState$2", at = @At("MIXINEXTRAS:EXPRESSION")) // ah hey that's how you mixin to a lambda
	private static boolean shouldIgnoreMarker(boolean original, @Local(name = "uuid", argsOnly = true) UUID uuid, @Local(name="cameraEntity",argsOnly = true) Entity cameraEntity) {
		// notodo - locator bar still shows even when this filters out all waypoints. minor issue, maybe fix
		if (original) { // fail fast
			return true;
		} else if (cameraEntity != null) {
			for (Entity passenger : cameraEntity.getIndirectPassengers()) {
				if (passenger.getUUID().equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}
}
