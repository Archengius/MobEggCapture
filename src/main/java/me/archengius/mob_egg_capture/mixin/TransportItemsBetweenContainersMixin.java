package me.archengius.mob_egg_capture.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.archengius.mob_egg_capture.MobEggCaptureComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TransportItemsBetweenContainers.class)
public class TransportItemsBetweenContainersMixin {

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"), method = "hasItemMatchingHandItem")
    private static boolean isSameItem(ItemStack first, ItemStack second, Operation<Boolean> original) {
        // Call the parent to make sure that items are of the same type
        if (!original.call(first, second)) {
            return false;
        }
        // If one of the items is a mob capture projectile, both of them must be one
        if (MobEggCaptureComponents.isMobCaptureProjectile(first) != MobEggCaptureComponents.isMobCaptureProjectile(second)) {
            return false;
        }
        // If one of the items is a captured mob, both of them must be one
        boolean isFirstCapturedMobEntity = MobEggCaptureComponents.isCapturedMobEntity(first);
        if (isFirstCapturedMobEntity != MobEggCaptureComponents.isCapturedMobEntity(second)) {
            return false;
        }
        // For captured mob entities, both of them must refer to the entity of the same type to be considered the same item
        if (isFirstCapturedMobEntity) {
            TypedEntityData<EntityType<?>> firstEntityData = first.get(DataComponents.ENTITY_DATA);
            TypedEntityData<EntityType<?>> secondEntityData = second.get(DataComponents.ENTITY_DATA);
            return firstEntityData != null && secondEntityData != null && firstEntityData.type() == secondEntityData.type();
        }
        return true;
    }
}
