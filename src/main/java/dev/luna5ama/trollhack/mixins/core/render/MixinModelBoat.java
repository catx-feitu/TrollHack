package dev.luna5ama.trollhack.mixins.core.render;

import dev.luna5ama.trollhack.module.modules.movement.EntitySpeed;
import dev.luna5ama.trollhack.util.Wrapper;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBoat.class)
public class MixinModelBoat {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(
        Entity entityIn,
        float limbSwing,
        float limbSwingAmount,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        float scale,
        CallbackInfo info
    ) {
        if (Wrapper.getPlayer().getRidingEntity() == entityIn && EntitySpeed.INSTANCE.isEnabled()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, EntitySpeed.getOpacity());
            GlStateManager.enableBlend();
        }
    }

}
