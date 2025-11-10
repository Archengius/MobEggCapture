package me.archengius.mob_egg_capture;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;

public class MobEggCaptureDispenseBehavior extends DefaultDispenseItemBehavior {

    public static final MobEggCaptureDispenseBehavior INSTANCE = new MobEggCaptureDispenseBehavior();

    private MobEggCaptureDispenseBehavior() {
    }

    @Override
    protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        ProjectileItem.DispenseConfig dispenseConfig = ProjectileItem.DispenseConfig.DEFAULT;
        ServerLevel level = blockSource.level();
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        Position position = dispenseConfig.positionFunction().getDispensePosition(blockSource, direction);

        Projectile.spawnProjectileUsingShoot(new ThrownEgg(level, position.x(), position.y(), position.z(), itemStack),
                level, itemStack, direction.getStepX(), direction.getStepY(), direction.getStepZ(),
                dispenseConfig.power(),dispenseConfig.uncertainty());
        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        blockSource.level().levelEvent(LevelEvent.SOUND_DISPENSER_PROJECTILE_LAUNCH, blockSource.pos(), 0);
    }
}
