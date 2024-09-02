/*
 * Copyright 2022 The Quilt Project
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.feature_flags.FeatureFlagBitSet;
import net.minecraft.registry.LayeredRegistryManager;
import net.minecraft.registry.ServerRegistryLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerReloadableResources;
import net.minecraft.server.command.CommandManager;

import org.quiltmc.qsl.resource.loader.impl.QuiltMultiPackResourceManagerHooks;
import org.quiltmc.qsl.resource.loader.impl.ResourceLoaderImpl;

@Mixin(ServerReloadableResources.class)
public class ServerReloadableResourcesMixin {
	@Shadow
	@Final
	private ServerReloadableResources.ServerProvider provider;

	@ModifyReturnValue(method = "getReloaders", at = @At("RETURN"))
	private List<ResourceReloader> onGetResourceReloaders(List<ResourceReloader> original) {
		// Re-inject resource reloaders server-side.
		// It is currently unknown why ReloadableResourceManager#reload isn't called anymore.
		var list = new ArrayList<>(original);
		ResourceLoaderImpl.sort(ResourceType.SERVER_DATA, list, this.provider);
		return list;
	}

	@Inject(method = "loadResources", at = @At("HEAD"))
	private static void onLoadResources(ResourceManager resources,
										LayeredRegistryManager<ServerRegistryLayer> registryManager,
										FeatureFlagBitSet featureFlags,
										CommandManager.RegistrationEnvironment environment,
										int level, Executor prepareExecutor,
										Executor applyExecutor,
										CallbackInfoReturnable<CompletableFuture<ServerReloadableResources>> cir) {
		if (resources instanceof QuiltMultiPackResourceManagerHooks hooks) {
			hooks.quilt$appendTopPacks();
		}
	}
}
