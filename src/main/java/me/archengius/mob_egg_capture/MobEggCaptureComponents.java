package me.archengius.mob_egg_capture;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class MobEggCaptureComponents {

    public static final String MOB_CAPTURE_PROJECTILE = "mob_capture_projectile";
    public static final String CAPTURED_MOB_ENTITY = "captured_mob_entity";

    public static boolean isMobCaptureProjectile(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().getBooleanOr(MOB_CAPTURE_PROJECTILE, false);
    }

    public static CustomData makeMobCaptureProjectile() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean(MOB_CAPTURE_PROJECTILE, true);
        return CustomData.of(compoundTag);
    }

    public static boolean isCapturedMobEntity(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        return customData != null && customData.copyTag().getBooleanOr(CAPTURED_MOB_ENTITY, false);
    }

    public static CustomData makeCapturedMobEntity() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putBoolean(CAPTURED_MOB_ENTITY, true);
        return CustomData.of(compoundTag);
    }
}
