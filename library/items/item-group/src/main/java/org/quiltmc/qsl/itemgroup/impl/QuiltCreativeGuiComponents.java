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

package org.quiltmc.qsl.itemgroup.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class QuiltCreativeGuiComponents {
	private static final Identifier BUTTON_TEXTURE = new Identifier("qsl_items_item_group", "textures/gui/creative_buttons.png");
	public static final Set<ItemGroup> ALWAYS_SHOWN_GROUPS = new HashSet<>();

	static {
		ALWAYS_SHOWN_GROUPS.add(ItemGroup.SEARCH);
		ALWAYS_SHOWN_GROUPS.add(ItemGroup.INVENTORY);
		ALWAYS_SHOWN_GROUPS.add(ItemGroup.HOTBAR);
	}

	public static class ItemGroupButtonWidget extends ButtonWidget {
		CreativeGuiExtensions extensions;
		CreativeInventoryScreen gui;
		Type type;

		public ItemGroupButtonWidget(int x, int y, Type type, CreativeGuiExtensions extensions) {
			super(x, y, 11, 10, type.text, (bw) -> type.clickConsumer.accept(extensions));
			this.extensions = extensions;
			this.type = type;
			this.gui = (CreativeInventoryScreen) extensions;
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			this.visible = extensions.qsl$isButtonVisible(type);
			this.active = extensions.qsl$isButtonEnabled(type);

			if (this.visible) {
				int u = active && this.isHovered() ? 22 : 0;
				int v = active ? 0 : 10;

				RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				this.drawTexture(matrices, this.x, this.y, u + (type == Type.NEXT ? 11 : 0), v, 11, 10);

				if (this.hovered) {
					gui.renderTooltip(matrices, new TranslatableText("qsl_items_item_group.gui.creativeTabPage", extensions.qsl$currentPage() + 1, ((ItemGroup.GROUPS.length - 12) / 9) + 2), mouseX, mouseY);
				}
			}
		}
	}

	public enum Type {
		NEXT(new LiteralText(">"), CreativeGuiExtensions::qsl$nextPage),
		PREVIOUS(new LiteralText("<"), CreativeGuiExtensions::qsl$previousPage);

		Text text;
		Consumer<CreativeGuiExtensions> clickConsumer;

		Type(Text text, Consumer<CreativeGuiExtensions> clickConsumer) {
			this.text = text;
			this.clickConsumer = clickConsumer;
		}
	}
}