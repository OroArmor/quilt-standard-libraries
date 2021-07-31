/*
 * Copyright 2021 QuiltMC
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

package org.quiltmc.qsl.item.mixin;

import org.quiltmc.qsl.item.api.item.v1.RecipeRemainderProvider;
import org.quiltmc.qsl.item.impl.CustomItemSettingImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
	@Inject(method = "getRemainingStacks", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	public <C extends Inventory, T extends Recipe<C>> void interceptGetRemainingStacks(RecipeType<T> recipeType, C inventory, World world, CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
		cir.setReturnValue(RecipeRemainderProvider.getRemainingStacks(inventory, recipeType, world, null));
	}
}