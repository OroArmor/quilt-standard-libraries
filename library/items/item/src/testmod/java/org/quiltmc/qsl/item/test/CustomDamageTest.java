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

package org.quiltmc.qsl.item.test;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import org.quiltmc.qsl.item.api.item.v1.CustomDamageHandler;
import org.quiltmc.qsl.item.api.item.v1.QuiltItemSettings;

public class CustomDamageTest implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("qsl_items_item_testmod", "weird_pickaxe"), new WeirdPick());
	}

	public static final CustomDamageHandler WEIRD_DAMAGE_HANDLER = (stack, amount, entity, breakCallback) -> {
		// If sneaking, apply all damage to vanilla. Otherwise, increment a tag on the stack by one and don't apply any damage
		if (entity.isSneaking()) {
			return amount;
		} else {
			NbtCompound nbt = stack.getOrCreateNbt();
			nbt.putInt("weird", nbt.getInt("weird") + 1);
			return 0;
		}
	};

	public static class WeirdPick extends PickaxeItem {
		protected WeirdPick() {
			super(ToolMaterials.GOLD, 1, -2.8F, new QuiltItemSettings().customDamage(WEIRD_DAMAGE_HANDLER));
		}

		@Override
		public Text getName(ItemStack stack) {
			int v = stack.getOrCreateNbt().getInt("weird");
			return super.getName(stack).shallowCopy().append(" (Weird Value: " + v + ")");
		}
	}
}