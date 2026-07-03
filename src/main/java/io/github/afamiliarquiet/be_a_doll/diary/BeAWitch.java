package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.effect.FragmentedMobEffect;
import io.github.afamiliarquiet.be_a_doll.effect.OverflowingMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.AbsorptionMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;


public class BeAWitch {
	public static final Holder.Reference<MobEffect> CARED_FOR = summonFamiliar("cared_for",
		new AbsorptionMobEffect(MobEffectCategory.BENEFICIAL, 0xb0b8dd)
			.addAttributeModifier(
				Attributes.MAX_ABSORPTION,
				BeADoll.id("effect.cared_for"),
				14.0,
				AttributeModifier.Operation.ADD_VALUE));

	public static final Holder.Reference<MobEffect> FRAGMENTED = summonFamiliar("fragmented",
		new FragmentedMobEffect(
			MobEffectCategory.HARMFUL,
			0xccb7c3,
			BeABug.FRAGMENTED));

	public static final Holder.Reference<MobEffect> OVERFLOWING = summonFamiliar("overflowing",
		new OverflowingMobEffect(MobEffectCategory.BENEFICIAL, 0x93a4ea)
	);

	public static void putOnHat() {

	}
	//okay. so why did you register four effects *without* a helper??? -taken
	private static Holder.Reference<MobEffect> summonFamiliar(String id, MobEffect effect) {
		return Registry.registerForHolder(
			BuiltInRegistries.MOB_EFFECT,
			BeADoll.id(id),
			effect
		);
	}

	// this essence ain't big enough for the two of us... *antimatter tumbleweed rolls past*
	public static void annihilate(LivingEntity entity, MobEffectInstance overflowingInstance, MobEffectInstance fragmentedInstance) {
		if (overflowingInstance != null && fragmentedInstance != null) { // my enemy-y-y-y-y
			int combatAdjustedOverflowDuration = overflowingInstance.getDuration() * 3;
			int fragmentedDuration = fragmentedInstance.getDuration();

			entity.removeEffect(OVERFLOWING);
			entity.removeEffect(FRAGMENTED);

			int overflowRemainder = (combatAdjustedOverflowDuration - fragmentedDuration) / 3;
			int fragmentedRemainder = fragmentedDuration - combatAdjustedOverflowDuration;

			if (overflowRemainder > 0) {
				entity.addEffect(new MobEffectInstance(OVERFLOWING, overflowRemainder, overflowingInstance.getAmplifier()));
			}
			if (fragmentedRemainder > 0) {
				entity.addEffect(new MobEffectInstance(FRAGMENTED, fragmentedRemainder, fragmentedInstance.getAmplifier()));
			}
		}
	}
}
