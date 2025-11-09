package me.archengius.mob_egg_capture.mixin;

import me.archengius.mob_egg_capture.MobEggCaptureComponents;
import me.archengius.mob_egg_capture.MobEggCaptureMod;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEgg.class)
public class MobEggThrownEggMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableItemProjectile;onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", shift = At.Shift.AFTER), method = "onHitEntity", cancellable = true)
    private void onThrownEggHitEntity(EntityHitResult entityHitResult, CallbackInfo callbackInfo) {
        ThrownEgg thisEntity = ((ThrownEgg) (Object)this);

        if (MobEggCaptureComponents.isMobCaptureProjectile(thisEntity.getItem())) {
            Entity entity = entityHitResult.getEntity();
            if (!entity.level().isClientSide()) {
                // We can only capture mobs. Enemies must be affected by weakness status effect to be captured
                // Note that canUsePortal check is supposed to exclude boss mobs. This seems to be the easiest way to check for an enemy that should not be captured/cannot travel
                if (entity instanceof Mob mob && (!(mob instanceof Enemy) || mob.hasEffect(MobEffects.WEAKNESS)) && mob.canUsePortal(true)) {

                    // Remove weakness effect from enemies when capturing them into the egg
                    if (mob instanceof Enemy) {
                        mob.removeEffect(MobEffects.WEAKNESS);
                    }

                    // Serialize the entity into the compound tag and make entity data out of it, then remove the entity
                    CompoundTag entitySavedCompoundTag;
                    try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(entity.problemPath(), MobEggCaptureMod.LOGGER)) {
                        TagValueOutput entitySavedData = TagValueOutput.createWithContext(scopedCollector, entity.registryAccess());
                        entity.saveWithoutId(entitySavedData);
                        entitySavedCompoundTag = entitySavedData.buildResult();
                    }
                    TypedEntityData<EntityType<?>> resultEntityData = TypedEntityData.of(entity.getType(), entitySavedCompoundTag);
                    Component resultItemName = Component.literal("Safari Net ").append(Component.literal("[")).append(entity.getType().getDescription()).append(Component.literal("]"));
                    entity.remove(Entity.RemovalReason.DISCARDED);

                    // Build an item stack with the captured entity and drop it on the floor
                    DataComponentPatch resultDataComponents = DataComponentPatch.builder()
                            .set(DataComponents.CUSTOM_DATA, MobEggCaptureComponents.makeCapturedMobEntity())
                            .set(DataComponents.ENTITY_DATA, resultEntityData)
                            .set(DataComponents.MAX_STACK_SIZE, 1)
                            .set(DataComponents.ITEM_NAME, resultItemName)
                            .set(DataComponents.ITEM_MODEL, ResourceLocation.withDefaultNamespace("brown_egg"))
                            .set(DataComponents.RARITY, Rarity.RARE)
                            .build();

                    ItemStack capturedMobItemStack = new ItemStack(thisEntity.getItem().getItem(), 1);
                    capturedMobItemStack.applyComponents(resultDataComponents);

                    ItemEntity droppedItemEntity = new ItemEntity(thisEntity.level(), entity.getX(), entity.getY(), entity.getZ(), capturedMobItemStack);
                    droppedItemEntity.setPickUpDelay(20);
                    thisEntity.level().addFreshEntity(droppedItemEntity);
                    thisEntity.discard();
                }
            }

            // We do not want to damage the entity we have hit like normal thrown egg entity does
            callbackInfo.cancel();
        }
    }

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrowableItemProjectile;onHit(Lnet/minecraft/world/phys/HitResult;)V", shift = At.Shift.AFTER), method = "onHit", cancellable = true)
	private void init(CallbackInfo callbackInfo) {
        ThrownEgg thisEntity = ((ThrownEgg) (Object)this);
        LivingEntity projectileOwner = (LivingEntity) thisEntity.getOwner();

        if (MobEggCaptureComponents.isMobCaptureProjectile(thisEntity.getItem())) {
            if (!thisEntity.level().isClientSide() && !thisEntity.isRemoved()) {

                // If we did not capture an entity, drop ourselves as an item
                if (projectileOwner == null || !projectileOwner.hasInfiniteMaterials()) {
                    ItemEntity droppedItemEntity = new ItemEntity(thisEntity.level(), thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), thisEntity.getItem());
                    droppedItemEntity.setPickUpDelay(20);
                    thisEntity.level().addFreshEntity(droppedItemEntity);
                }
                thisEntity.discard();
            }

            // We do not want to spawn chicken entities on impact like normal thrown egg entity does
            callbackInfo.cancel();
        } else if (MobEggCaptureComponents.isCapturedMobEntity(thisEntity.getItem())) {

            if (!thisEntity.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) thisEntity.level();

                // Spawn the entity from the entity data we have stored on the item
                TypedEntityData<EntityType<?>> entityData = thisEntity.getItem().get(DataComponents.ENTITY_DATA);
                if (entityData != null && entityData.type() != null) {
                    Entity entity = entityData.type().create(serverLevel, EntitySpawnReason.TRIGGERED);
                    if (entity != null) {
                        entityData.loadInto(entity);

                        entity.snapTo(thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), thisEntity.getYRot(), 0.0F);
                        if (entity instanceof Mob mob) {
                            mob.yHeadRot = mob.getYRot();
                            mob.yBodyRot = mob.getYRot();
                        }

                        if (entity.fudgePositionAfterSizeChange(EntityDimensions.fixed(0.0f, 0.0f))) {

                            // Spawn the entity, we have the space to place it in the world at the hit location
                            serverLevel.addFreshEntityWithPassengers(entity);
                            if (entity instanceof Mob mob) {
                                mob.playAmbientSound();
                            }
                        } else {
                            // We could not place the entity at the hit location, drop ourselves as an item
                            if (projectileOwner == null || !projectileOwner.hasInfiniteMaterials()) {
                                ItemEntity droppedItemEntity = new ItemEntity(thisEntity.level(), thisEntity.getX(), thisEntity.getY(), thisEntity.getZ(), thisEntity.getItem());
                                droppedItemEntity.setPickUpDelay(20);
                                thisEntity.level().addFreshEntity(droppedItemEntity);
                            }
                            thisEntity.discard();
                        }
                    }
                }
                thisEntity.discard();
            }

            // We do not want to spawn chicken entities on impact like normal thrown egg entity does
            callbackInfo.cancel();
        }
	}
}