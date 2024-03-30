/*
 * Copyright 2024 The Quilt Project
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

package org.quiltmc.qsl.item.setting.api;

import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import net.minecraft.util.Identifier;

import org.quiltmc.qsl.item.setting.impl.RecipeRemainderLogicHandlerImpl;

/**
 * Contains the different recipe remainder locations that QSL supports.
 * Calling {@link #getOrCreate(Identifier)} allows mods to create their own remainder locations or get remainder locations without needing to compile against the other mod.
 *
 * <p> This class should not be extended.
 */
@ApiStatus.NonExtendable
public interface RecipeRemainderLocation {
	/**
	 * Remainder location for Vanilla crafting. Used in Crafting Tables and the inventory crafting screen.
	 */
	RecipeRemainderLocation CRAFTING = getOrCreate(new Identifier("minecraft:crafting"));

	/**
	 * Remainder location for the furnace fuel slot in the different furnace types.
	 */
	RecipeRemainderLocation FURNACE_FUEL = getOrCreate(new Identifier("minecraft:furnace_fuel"));

	/**
	 * Remainder location for the furnace ingredient slot in the different furnace types.
	 */
	RecipeRemainderLocation FURNACE_INGREDIENT = getOrCreate(new Identifier("minecraft:furnace_ingredient"));

	/**
	 * Remainder location for the dye slot in looms.
	 */
	RecipeRemainderLocation LOOM_DYE = getOrCreate(new Identifier("minecraft:loom_dye"));

	/**
	 * Remainder location for the potion addition in brewing stands.
	 */
	RecipeRemainderLocation POTION_ADDITION = getOrCreate(new Identifier("minecraft:potion_addition"));

	/**
	 * Remainder location for the input to the stone cutter.
	 */
	RecipeRemainderLocation STONE_CUTTER_INPUT = getOrCreate(new Identifier("minecraft:stone_cutter_input"));

	/**
	 * Remainder location for the smithing template slot.
	 */
	RecipeRemainderLocation SMITHING_TEMPLATE = getOrCreate(new Identifier("minecraft:smithing_template"));

	/**
	 * Remainder location for the smithing base slot.
	 */
	RecipeRemainderLocation SMITHING_BASE = getOrCreate(new Identifier("minecraft:smithing_base"));

	/**
	 * Remainder location for the smithing ingredient slot.
	 */
	RecipeRemainderLocation SMITHING_INGREDIENT = getOrCreate(new Identifier("minecraft:smithing_ingredient"));

	/**
	 * The default locations for a recipe remainder.
	 */
	RecipeRemainderLocation[] DEFAULT_LOCATIONS = {CRAFTING, FURNACE_FUEL};

	/**
	 * Gets a new remainder location if it already exists, creating it otherwise.
	 * @param id the id for the location
	 * @return the remainder location
	 */
	@Contract("null -> fail; _ -> new")
	static RecipeRemainderLocation getOrCreate(Identifier id) {
		record RecipeRemainderLocationImpl(Identifier id) implements RecipeRemainderLocation {
		}

		Objects.requireNonNull(id, "`id` must not be null.");

		return RecipeRemainderLogicHandlerImpl.LOCATIONS.computeIfAbsent(id, RecipeRemainderLocationImpl::new);
	}

	/**
	 *
	 * @return the id for the location.
	 */
	Identifier id();
}
