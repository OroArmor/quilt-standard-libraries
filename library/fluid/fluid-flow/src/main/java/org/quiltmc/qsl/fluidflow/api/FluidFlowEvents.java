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

package org.quiltmc.qsl.fluidflow.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.base.api.event.Event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


public final class FluidFlowEvents {
	private FluidFlowEvents() {
	}

	// Flowing Block to Interacting Block to Direction to Event
	private static final Map<Block, Map<Block, Map<Direction, Event<FluidFlowInteractionCallback>>>> EVENT_MAP = new HashMap<>();

	/**
	 * Registers a new event on a fluid flow. The same two blocks can register a different event for different directions, but the same directions will run the event that was registered first.
	 *
	 * @param flowingBlock          The fluid block that flowed.
	 * @param interactionBlock      The block in one of the {@code interactionDirections}.
	 * @param interactionDirections The direction to search for {@code interactionBlock}.
	 * @param interactionEvent      The event to run when the conditions are met.
	 */
	public static void register(Block flowingBlock, Block interactionBlock, Direction[] interactionDirections, FluidFlowInteractionCallback interactionEvent) {
		Map<Block, Map<Direction, Event<FluidFlowInteractionCallback>>> flowBlockEvents = EVENT_MAP.computeIfAbsent(flowingBlock, flowing -> new HashMap<>());
		Map<Direction, Event<FluidFlowInteractionCallback>> interactionEvents = flowBlockEvents.computeIfAbsent(interactionBlock, interacting -> new HashMap<>());

		// Create events for all the different directions for the specified blocks
		if (interactionEvents.isEmpty()) {
			for (Direction direction : Direction.values()) {
				interactionEvents.put(direction, Event.create(FluidFlowInteractionCallback.class, fluidFlowInteractionEvents -> (flowingBlockState, interactingBlockState, flowPos, world) -> {
					for (FluidFlowInteractionCallback event : fluidFlowInteractionEvents) {
						if (!event.onFlow(flowingBlockState, interactingBlockState, flowPos, world)) {
							return false;
						}
					}

					return true;
				}));
			}
		}

		// Register the new event callbacks
		for (Map.Entry<Direction, Event<FluidFlowInteractionCallback>> pair : interactionEvents.entrySet()) {
			for (Direction direction : interactionDirections) {
				if (pair.getKey() == direction) {
					pair.getValue().register(interactionEvent);
				}
			}
		}
	}

	/**
	 * Gets the event from the following blocks and direction.
	 *
	 * @param flowingBlock         The fluid block that flowed.
	 * @param interactionBlock     The block it interacts with.
	 * @param interactionDirection The interaction direction
	 * @return An event if the conditions are met, otherwise {@code null}
	 */
	public static @Nullable Event<FluidFlowInteractionCallback> getEvent(Block flowingBlock, Block interactionBlock, Direction interactionDirection) {
		if (EVENT_MAP.containsKey(flowingBlock)) {
			Map<Block, Map<Direction, Event<FluidFlowInteractionCallback>>> flowBlockEvents = EVENT_MAP.get(flowingBlock);

			if (flowBlockEvents.containsKey(interactionBlock)) {
				Map<Direction, Event<FluidFlowInteractionCallback>> interactionEvents = flowBlockEvents.get(interactionBlock);

				for (Map.Entry<Direction, Event<FluidFlowInteractionCallback>> pair : interactionEvents.entrySet()) {
					if (pair.getKey() == interactionDirection) {
						return pair.getValue();
					}
				}
			}
		}

		return null;
	}

	public interface FluidFlowInteractionCallback {

		/**
		 * An event run when a fluid flows next to a block.
		 *
		 * @param flowingBlockState     The block state of the fluid block.
		 * @param interactingBlockState The block state of the interacting block.
		 * @param flowPos               The position in the world that the fluid flowed into.
		 * @param world                 The world the event took place in.
		 * @return {@code false} if the event was successful, and {@code true} if it was unsuccessful (don't blame us its minecraft that does this).
		 */
		boolean onFlow(BlockState flowingBlockState, BlockState interactingBlockState, BlockPos flowPos, World world);
	}
}
