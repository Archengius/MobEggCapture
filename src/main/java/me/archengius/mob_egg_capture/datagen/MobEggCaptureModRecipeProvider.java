package me.archengius.mob_egg_capture.datagen;

import me.archengius.mob_egg_capture.MobEggCaptureMod;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class MobEggCaptureModRecipeProvider extends FabricRecipeProvider {
    public MobEggCaptureModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {

                ItemStackShapedRecipeBuilder.shaped(provider.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, MobEggCaptureMod.createMobCaptureEggItemStack())
                    .pattern("ggg")
                    .pattern("geg")
                    .pattern("ggg")
                    .define('g', Items.GOLD_INGOT)
                    .define('e', ItemTags.EGGS)
                    .group("mob_capture_egg")
                    .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                    .unlockedBy(getHasName(Items.EGG), has(ItemTags.EGGS))
                    .save(recipeOutput, "mob_egg_capture:safari_net");
            }
        };
    }

    @Override
    public String getName() {
        return "MobEggCaptureModRecipeProvider";
    }
}
