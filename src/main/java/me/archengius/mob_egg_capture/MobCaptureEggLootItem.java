package me.archengius.mob_egg_capture;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class MobCaptureEggLootItem extends LootPoolSingletonContainer {

    public static final MapCodec<MobCaptureEggLootItem> CODEC = RecordCodecBuilder.<MobCaptureEggLootItem>mapCodec((instance) ->
            singletonFields(instance).apply(instance, MobCaptureEggLootItem::new));;
    public static final LootPoolEntryType MOB_CAPTURE_EGG = Registry.register(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MobEggCaptureMod.MOD_ID, "mob_capture_egg"), new LootPoolEntryType(CODEC));

    public MobCaptureEggLootItem(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
    }

    public static void register() {
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        consumer.accept(MobEggCaptureMod.createMobCaptureEggItemStack());
    }

    @Override
    public LootPoolEntryType getType() {
        return MOB_CAPTURE_EGG;
    }

    public static LootPoolSingletonContainer.Builder<?> mobCaptureEgg() {
        return simpleBuilder((weight, quality, conditions, functions) -> new MobCaptureEggLootItem(weight, quality, conditions, functions));
    }
}
