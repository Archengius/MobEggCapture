package me.archengius.mob_egg_capture.mixin;

import me.archengius.mob_egg_capture.MobEggCaptureComponents;
import me.archengius.mob_egg_capture.MobEggCaptureDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(at = @At("HEAD"), method = "getDispenseMethod", cancellable = true)
    private void getDispenseMethodOverride(Level level, ItemStack itemStack, CallbackInfoReturnable<DispenseItemBehavior> callbackInfo) {
        // We want to override the dispense behavior for capture projectiles and captured mob entities
        if (MobEggCaptureComponents.isMobCaptureProjectile(itemStack) || MobEggCaptureComponents.isCapturedMobEntity(itemStack)) {
            callbackInfo.setReturnValue(MobEggCaptureDispenseBehavior.INSTANCE);
        }
    }
}
