package io.github.afamiliarquiet.be_a_doll.item;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats.FoxTrustInvoker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.network.ServerPlayer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.logging.Level;

public class RibbonItem extends Item {
	public RibbonItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult useOnEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Player doll && BeAMaid.isDoll(doll)) {
			if (doll.startRiding(user)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return InteractionResult.SUCCESS;
			}
		} else {
			InteractionResult tried = useToTryRiding(stack, user, entity, hand);
			if (tried.isAccepted()) {
				return tried;
			}
		}

		return super.useOnEntity(stack, user, entity, hand);
	}

	public InteractionResult useToTryRiding(ItemStack stack, Player user, Entity entity, InteractionHand hand) {
		if (BeAMaid.isDoll(user)) {
			// ohh.. so the user was the doll!
			boolean shouldRide = false;
			if (entity instanceof TamableAnimal tameable) {
				EntityReference<LivingEntity> ownerRef = tameable.getOwnerReference();
				if (ownerRef != null && ownerRef.uuidEquals(user) && entity.getWidth() > user.getWidth()) {
					shouldRide = true;
				}
			} else if (entity instanceof Fox foxesAreSoCool && ((FoxTrustInvoker)foxesAreSoCool).invokeCanTrust(user)) {
				shouldRide = true;
			}

			if (shouldRide && user.startRiding(entity)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult use(Level world, Player user, InteractionHand hand) {
		// yeah no lol. did you not see the C2SDollDismountLetter i had to make? client's gotta hear about this
		if (/*!user.getWorld().isClient && */!user.getPassengerList().isEmpty() && user.shouldCancelInteraction()) {
//			user.removeAllPassengers();
			Entity doll = user.getPassengerList().getLast();
			BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidInteractionHandling.NONE);
			Vec3d pos;
			// fear my mega if statement of doom! it could be worse. i'm just being a little bit silly with it.
			if (!world.isClient()
				&& blockHitResult.getType() == HitResult.Type.BLOCK
				&& doll instanceof ServerPlayer serverPlayer
				&& (pos = getDollPlacementPos(blockHitResult, doll)) != null
			) {
				serverPlayer.teleportTo(new TeleportTarget(serverPlayer.getWorld(), pos, Vec3d.ZERO, user.getYaw() + 180, user.getPitch() * -1, TeleportTarget.NO_OP));
			} else {
				doll.stopRiding();
			}
			user.playSound(BeABirdwatcher.RAVEN_CRY, 1f, 1f);
			return InteractionResult.SUCCESS;
		} else {
			return super.use(world, user, hand);
		}
	}

	public static @Nullable Vec3d getDollPlacementPos(BlockHitResult blockHitResult, Entity doll) {
		Vec3d pos = blockHitResult.getPos();
		EntityDimensions dollStanding = doll.getDimensions(Pose.STANDING);

		if (blockHitResult.getSide().getAxis() == Direction.Axis.Y) {
			if (blockHitResult.getSide() == Direction.DOWN) {
				pos = pos.add(0, -dollStanding.height(), 0);
			}

			// just because i'm feeling extra nice, i'll give you a horizontal aim assist too.
			Vec3d firstCheck = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.X);
			if (firstCheck != null) {
				pos = firstCheck;
			} else {
				pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Z);
			}
		} else {
			Vector3f sideVec = blockHitResult.getSide().getUnitVector();
			pos = pos.add(sideVec.x * dollStanding.width() / 2, 0, sideVec.z * dollStanding.width() / 2);

			// adjust for being low or high enough to clip into a possible adjacent block
			pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Y);
		}

		// check for any other collisions, if collide then. bad aim, sorry, just gonna drop.
		// i'll maybe update the math later.
		if (pos == null || doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos)).iterator().hasNext()) {
			return null;
		} else {
			return pos;
		}
	}

	// todo - hey quiet if you come back to use this again, maybe do it more like Entity.adjustMovementForCollisions
	//  you've really got a lot of block collision checking going on here
	//  anyway this partial aim assist only helps in the case of one collision.
	//  redoing this like entity.amfc would probably be the best way to fix that. assuming amfc is.. what this is.
	private static @Nullable Vec3d checkForCollisionsOnAxis(Entity doll, EntityDimensions dollStanding, @Nullable Vec3d pos, Direction.Axis axis) {
		if (pos == null || !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos)).iterator().hasNext()) {
			return pos;
		} else {
			double xyz = pos.getComponentAlongAxis(axis);
			double positiveEdgeCrumb = (xyz + (axis.isVertical() ? dollStanding.height() : dollStanding.width() / 2)) - Math.ceil(xyz);
			double negativeEdgeCrumb = Math.floor(xyz) - (xyz - (axis.isVertical() ? 0 : dollStanding.width() / 2));
			if (positiveEdgeCrumb < 0.5 && positiveEdgeCrumb > 0 && !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos.withAxis(axis, xyz - positiveEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll down a bit works, try now
				return pos.withAxis(axis, xyz - positiveEdgeCrumb);
			} else if (negativeEdgeCrumb < 0.5 && negativeEdgeCrumb > 0 && !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos.withAxis(axis, xyz + negativeEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll up a bit works, try now
				return pos.withAxis(axis, xyz + negativeEdgeCrumb);
			} else {
				// well. if there's gonna be collision, just drop
				return null;
			}
		}
	}
}
