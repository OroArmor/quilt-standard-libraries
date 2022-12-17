/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.resource.loader.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.resource.pack.DataPackSettings;
import net.minecraft.test.TestServer;

import org.quiltmc.qsl.resource.loader.impl.ModResourcePackUtil;

@Mixin(TestServer.class)
public class TestServerMixin {
	@ModifyArg(
			method = "create",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/FeatureAndDataSettings;<init>(Lnet/minecraft/resource/pack/DataPackSettings;Lnet/minecraft/feature_flags/FeatureFlagBitSet;)V"
			),
			index = 0
	)
	private static DataPackSettings replaceDefaultDataPackSettings(DataPackSettings initialDataPacks) {
		return ModResourcePackUtil.DEFAULT_SETTINGS;
	}
}
