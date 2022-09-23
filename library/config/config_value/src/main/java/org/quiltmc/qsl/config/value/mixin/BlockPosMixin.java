package org.quiltmc.qsl.config.value.mixin;

import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.ValueMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.math.BlockPos;

@Mixin(BlockPos.class)
public abstract class BlockPosMixin implements ConfigSerializableObject<ValueMap<Integer>> {
	@Override
	public ConfigSerializableObject<ValueMap<Integer>> convertFrom(ValueMap<Integer> representation) {
		return (ConfigSerializableObject<ValueMap<Integer>>) new BlockPos(representation.get("x"), representation.get("y"), representation.get("z"));
	}
}
