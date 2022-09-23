package org.quiltmc.qsl.config.value.test;

import org.quiltmc.config.api.WrappedConfig;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

public class ConfigTest extends WrappedConfig {
	public Vec3i test_vec3i = new Vec3i(1, 2, 3);
	public Identifier test_identifier = new Identifier("quilt_value_config_test", "test_identifier");
}
