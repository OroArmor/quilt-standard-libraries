package org.quiltmc.qsl.registry.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import org.quiltmc.qsl.registry.impl.dynamic.DynamicMetaRegistryImpl;

@Mixin(RegistryKeys.class)
public class RegistryKeysMixin {
	@WrapOperation(
			method = {"method_60915", "method_60916"},
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/util/Identifier;getPath()Ljava/lang/String;"
			)
	)
	private static String replaceDynamicRegistryPath(Identifier id, Operation<String> original) {
		if (DynamicMetaRegistryImpl.isModdedRegistryId(id)) {
			return id.getNamespace() + "/" + original.call(id);
		}

		return original.call(id);
	}
}
