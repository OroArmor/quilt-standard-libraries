/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package org.quiltmc.qsl.networking.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.NetworkSide;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.payload.CustomPayload;
import net.minecraft.util.Identifier;

@ApiStatus.Internal
public final class GlobalReceiverRegistry<H> {
	public static final int DEFAULT_CHANNEL_NAME_MAX_LENGTH = 128;
	private final NetworkSide side;
	private final NetworkState state;
	@Nullable
	private final PayloadTypeRegistryImpl<?> payloadTypeRegistry;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<CustomPayload.Id<?>, H> receivers = new Object2ObjectOpenHashMap<>();
	private final Set<AbstractNetworkAddon<H>> trackedAddons = new HashSet<>();

	public GlobalReceiverRegistry(NetworkSide side, NetworkState state, @Nullable PayloadTypeRegistryImpl<?> payloadTypeRegistry) {
		this.side = side;
		this.state = state;
		this.payloadTypeRegistry = payloadTypeRegistry;

		if (payloadTypeRegistry != null) {
			if(state != payloadTypeRegistry.getPhase() || side != payloadTypeRegistry.getSide()) {
				throw new AssertionError();
			}
		}
	}

	@Nullable
	public H getReceiver(CustomPayload.Id<?> channelName) {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return this.receivers.get(channelName);
		} finally {
			lock.unlock();
		}
	}

	public boolean registerGlobalReceiver(CustomPayload.Id<?> channelName, H handler) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(handler, "Channel handler cannot be null");

		if (NetworkingImpl.isReservedCommonChannel(channelName)) {
			throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel with name \"%s\"", channelName));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			final boolean inserted = this.receivers.putIfAbsent(channelName, handler) == null;

			if (inserted) {
				this.handleRegistration(channelName, handler);
			}

			return inserted;
		} finally {
			lock.unlock();
		}
	}

	public H unregisterGlobalReceiver(CustomPayload.Id<?> channelName) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		if (NetworkingImpl.isReservedCommonChannel(channelName)) {
			throw new IllegalArgumentException(String.format("Cannot unregister packet handler for reserved channel with name \"%s\"", channelName));
		}

		this.assertPayloadType(channelName);
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			final H removed = this.receivers.remove(channelName);

			if (removed != null) {
				this.handleUnregistration(channelName);
			}

			return removed;
		} finally {
			lock.unlock();
		}
	}

	public Map<CustomPayload.Id<?>, H> getReceivers() {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			return new Object2ObjectOpenHashMap<>(this.receivers);
		} finally {
			lock.unlock();
		}
	}

	public Set<CustomPayload.Id<?>> getChannels() {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return new HashSet<>(this.receivers.keySet());
		} finally {
			lock.unlock();
		}
	}

	// State tracking methods

	public void startSession(AbstractNetworkAddon<H> addon) {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			this.trackedAddons.add(addon);
		} finally {
			lock.unlock();
		}
	}

	public void endSession(AbstractNetworkAddon<H> addon) {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			this.trackedAddons.remove(addon);
		} finally {
			lock.unlock();
		}
	}

	private void handleRegistration(CustomPayload.Id<?> channelName, H handler) {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			for (AbstractNetworkAddon<H> addon : this.trackedAddons) {
				addon.registerChannel(channelName, handler);
			}
		} finally {
			lock.unlock();
		}
	}

	private void handleUnregistration(CustomPayload.Id<?> channelName) {
		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			for (AbstractNetworkAddon<H> addon : this.trackedAddons) {
				addon.unregisterChannel(channelName);
			}
		} finally {
			lock.unlock();
		}
	}

	public void assertPayloadType(CustomPayload.Id<?> channelName) {
		if (this.payloadTypeRegistry == null) {
			return;
		}

		if (this.payloadTypeRegistry.get(channelName) == null) {
			throw new IllegalArgumentException(String.format("Cannot register handler as no payload type has been registered with name \"%s\" for %s %s", channelName, this.side, this.state));
		}

		if (channelName.toString().length() > DEFAULT_CHANNEL_NAME_MAX_LENGTH) {
			throw new IllegalArgumentException(String.format("Cannot register handler for channel with name \"%s\" as it exceeds the maximum length of 128 characters", channelName));
		}
	}

	public NetworkState getState() {
		return this.state;
	}
}
