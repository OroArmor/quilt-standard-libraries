package org.quiltmc.qsl.config.value.mixin;

import org.quiltmc.config.api.values.ComplexConfigValue;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.Identifier;

@Mixin(Identifier.class)
public abstract class IdentifierMixin implements ConfigSerializableObject<String> {
	@Shadow
	public abstract String toString();

	@Override
	public ConfigSerializableObject<String> convertFrom(String representation) {
		return (ConfigSerializableObject<String>) new Identifier(representation);
	}

	@Override
	public String getRepresentation() {
		return this.toString();
	}

	@Override
	public ComplexConfigValue copy() {
		return this;
	}
}
