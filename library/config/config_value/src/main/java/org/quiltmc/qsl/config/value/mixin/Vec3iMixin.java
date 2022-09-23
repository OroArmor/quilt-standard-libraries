package org.quiltmc.qsl.config.value.mixin;

import org.quiltmc.config.api.values.ComplexConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.ValueMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.Vec3i;

@Mixin(Vec3i.class)
public class Vec3iMixin implements ConfigSerializableObject<ValueMap<Integer>> {
	@Shadow
	private int x, y, z;

	@Override
	public ComplexConfigValue copy() {
		return this;
	}

	@Override
	public ConfigSerializableObject<ValueMap<Integer>> convertFrom(ValueMap<Integer> representation) {
		return (ConfigSerializableObject<ValueMap<Integer>>) new Vec3i(representation.get("x"), representation.get("y"), representation.get("z"));
	}

	@Override
	public ValueMap<Integer> getRepresentation() {
		return ValueMap.builder(0)
				.put("x", this.x)
				.put("y", this.y)
				.put("z", this.z)
				.build();
	}
}
