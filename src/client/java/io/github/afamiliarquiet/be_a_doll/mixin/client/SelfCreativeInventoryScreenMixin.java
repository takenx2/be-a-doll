package io.github.afamiliarquiet.be_a_doll.mixin.client;

import io.github.afamiliarquiet.be_a_doll.BeASelf;
import io.github.afamiliarquiet.be_a_doll.letters.C2SCreativeEssenceAlterationLetter;
import net.fabricmc.fabric.api.client.creativetab.v1.FabricCreativeModeInventoryScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class SelfCreativeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> implements FabricCreativeModeInventoryScreen {
	public SelfCreativeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
	}

	// Hey! You, the one reading this code!
	// Are you annoyed/displeased/disgusted/revolted/terrified?
	// Tell me how I can do better! Save me! Please! Please, anyone, help me! Is anyone there?!
	// i don't like screens

	//girl idk either!!!!!!!!!!!!!!!!! -taken
	@Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
	private void clicky(MouseButtonEvent mouse, CallbackInfoReturnable<Boolean> cir) {

		// injecting at head seems fine here. i could've injected at mouseClicked instead here, but.. consistency
		if (BeASelf.isMouseInCreativeSelf(mouse.x(), mouse.y(), this.leftPos, this.topPos) && this.minecraft.player != null) {
			ItemStack cursorStack = this.menu.getCarried();
			ItemStack clickProcessedStack = null;

			if (mouse.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				ClientPlayNetworking.send(new C2SCreativeEssenceAlterationLetter(true, cursorStack));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, true);
			} else if (mouse.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				ClientPlayNetworking.send(new C2SCreativeEssenceAlterationLetter(false, cursorStack));
				clickProcessedStack = BeASelf.clickSelf(cursorStack, this.minecraft.player, false);
			}

			if (clickProcessedStack != null) {
				this.menu.setCarried(clickProcessedStack);
				cir.setReturnValue(true);
			}
		}
	}
}
