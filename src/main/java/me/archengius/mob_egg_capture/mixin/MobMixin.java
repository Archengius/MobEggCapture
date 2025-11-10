package me.archengius.mob_egg_capture.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.archengius.mob_egg_capture.MobEggCaptureComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;wantsToPickUp(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Z"), method = "aiStep")
    private boolean wrapWantsToPickUp(Mob instance, ServerLevel level, ItemStack itemStack, Operation<Boolean> original) {
        // We do not want mobs to attempt to pick up mob capture projectiles and captured mob entities under any circumstances
        return original.call(instance, level, itemStack) && !MobEggCaptureComponents.isMobCaptureProjectile(itemStack) && !MobEggCaptureComponents.isCapturedMobEntity(itemStack);
    }
}
