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

package org.quiltmc.qsl.fluid.flow.mixin;

import org.quiltmc.qsl.base.api.event.Event;
import org.quiltmc.qsl.fluid.flow.api.FluidFlowEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


@Mixin(FluidBlock.class)
public class FluidBlockMixin extends Block {
	private static final Direction[] DIRECTIONS = Direction.values();

	public FluidBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
	private void receiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		for (Direction direction : DIRECTIONS) {
			var event = FluidFlowEvents.getEvent(this, world.getBlockState(pos.offset(direction)).getBlock());

			if (event != null) {
				if (!event.invoker().onFlow(state, world.getBlockState(pos.offset(direction)), direction, pos, world)) {
					cir.setReturnValue(false);
					return;
				}
			}
		}
	}
}