package org.quiltmc.qsl.config.value.mixin;

import org.quiltmc.config.api.values.ComplexConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.ValueMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;

@Mixin(BlockBox.class)
public class BlockBoxMixin implements ConfigSerializableObject<ValueMap<Integer>> {
	@Shadow
	private int minX, minY, minZ, maxX, maxY, maxZ;

	@Override
	public ComplexConfigValue copy() {
		return this;
	}

	@Override
	public ConfigSerializableObject<ValueMap<Integer>> convertFrom(ValueMap<Integer> representation) {
		return (ConfigSerializableObject<ValueMap<Integer>>) new BlockBox(representation.get("minX"), representation.get("minY"), representation.get("minZ"), representation.get("maxX"), representation.get("maxY"), representation.get("maxZ"));
	}

	@Override
	public ValueMap<Integer> getRepresentation() {
		return ValueMap.builder(0)
				.put("minX", this.minX)
				.put("minY", this.minY)
				.put("minZ", this.minZ)
				.put("maxX", this.maxX)
				.put("maxY", this.maxY)
				.put("maxZ", this.maxZ)
				.build();
	}
}
