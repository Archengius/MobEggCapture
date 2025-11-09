package me.archengius.mob_egg_capture;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class MobEggCaptureMod implements ModInitializer {
	public static final String MOD_ID = "mob_egg_capture";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        MobCaptureEggLootItem.register();
        UseItemCallback.EVENT.register(MobEggCaptureMod::handleUseItemEvent);
        LootTableEvents.MODIFY.register(MobEggCaptureMod::handleLootTableModificationEvent);
	}

    public static ItemStack createMobCaptureEggItemStack() {
        DataComponentPatch resultDataComponents = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, MobEggCaptureComponents.makeMobCaptureProjectile())
                .set(DataComponents.MAX_STACK_SIZE, 1)
                .set(DataComponents.ITEM_NAME, Component.literal("Safari Net"))
                .set(DataComponents.ITEM_MODEL, ResourceLocation.withDefaultNamespace("lead"))
                .set(DataComponents.RARITY, Rarity.RARE)
                .build();

        ItemStack resultItemStack = new ItemStack(Items.CLOCK, 1);
        resultItemStack.applyComponents(resultDataComponents);
        return resultItemStack;
    }

    private static InteractionResult handleUseItemEvent(Player player, Level world, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!itemInHand.isEmpty() && world instanceof ServerLevel) {

            // Spawn the entity in the world if this is a captured entity item or empty mob egg
            if (MobEggCaptureComponents.isMobCaptureProjectile(itemInHand) || MobEggCaptureComponents.isCapturedMobEntity(itemInHand)) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                Projectile.spawnProjectileFromRotation(ThrownEgg::new, (ServerLevel) world, itemInHand, player, 0.0F, 1.5F, 1.0F);
                itemInHand.consume(1, player);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private static void handleLootTableModificationEvent(ResourceKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, HolderLookup.Provider registries) {
        if (key.location().toString().equals("minecraft:chests/simple_dungeon") && source.isBuiltin()) {
            AtomicInteger lootPoolIndexCounter = new AtomicInteger();
            tableBuilder.modifyPools(builder -> {
                if (lootPoolIndexCounter.incrementAndGet() == 1) {
                    builder.with(MobCaptureEggLootItem.mobCaptureEgg().setWeight(30).build());
                }
            });
        }
        if (key.location().toString().equals("minecraft:chests/abandoned_mineshaft") && source.isBuiltin()) {
            AtomicInteger lootPoolIndexCounter = new AtomicInteger();
            tableBuilder.modifyPools(builder -> {
                if (lootPoolIndexCounter.incrementAndGet() == 1) {
                    builder.with(MobCaptureEggLootItem.mobCaptureEgg().setWeight(20).build());
                }
            });
        }
        if (key.location().toString().equals("minecraft:chests/jungle_temple") && source.isBuiltin()) {
            AtomicInteger lootPoolIndexCounter = new AtomicInteger();
            tableBuilder.modifyPools(builder -> {
                if (lootPoolIndexCounter.incrementAndGet() == 1) {
                    builder.with(MobCaptureEggLootItem.mobCaptureEgg().setWeight(4).build());
                }
            });
        }
        if (key.location().toString().equals("minecraft:chests/desert_pyramid") && source.isBuiltin()) {
            AtomicInteger lootPoolIndexCounter = new AtomicInteger();
            tableBuilder.modifyPools(builder -> {
                if (lootPoolIndexCounter.incrementAndGet() == 1) {
                    builder.with(MobCaptureEggLootItem.mobCaptureEgg().setWeight(30).build());
                }
            });
        }
    }
}