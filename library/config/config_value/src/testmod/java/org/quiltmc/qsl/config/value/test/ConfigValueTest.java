package org.quiltmc.qsl.config.value.test;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.config.QuiltConfig;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ConfigValueTest implements ModInitializer {
	public static final ConfigTest CONFIG = QuiltConfig.create(
			"quilt_config_value_test",
			"config",
			ConfigTest.class);


	@Override
	public void onInitialize(ModContainer mod) {

	}
}
