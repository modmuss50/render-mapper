package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.ModelPartExtension;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

	@Shadow protected M model;

	@Inject(method = "render", at = @At("HEAD"))
	public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		for (Field field : model.getClass().getFields()) {
			if (field.getType() == ModelPart.class) {
				String part = model.getClass().getSimpleName() + "." + field.getName();
				if (!ExampleMod.PARTS.contains(part)) {
					ExampleMod.PARTS.add(part);
				}
				if (ExampleMod.ACTIVE.equals(part)) {
					field.setAccessible(true);
					try {
						ModelPart modelPart = (ModelPart) field.get(model);
						ModelPartExtension extension = (ModelPartExtension) modelPart;
						extension.highlight();
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
