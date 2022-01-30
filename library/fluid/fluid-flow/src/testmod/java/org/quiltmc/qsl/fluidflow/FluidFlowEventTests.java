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

package org.quiltmc.qsl.fluidflow;

import org.quiltmc.qsl.fluidflow.api.FluidFlowEvents;

import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;

import net.fabricmc.api.ModInitializer;

public class FluidFlowEventTests implements ModInitializer {
	@Override
	public void onInitialize() {
		FluidFlowEvents.register(Blocks.WATER, Blocks.BLUE_ICE, new Direction[]{Direction.DOWN}, (flowingBlockState, interactingBlockState, flowPos, world) -> {
			world.setBlockState(flowPos, Blocks.ICE.getDefaultState());
			world.playSound(null, flowPos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1, 1);
			return false;
		});
	}
}
