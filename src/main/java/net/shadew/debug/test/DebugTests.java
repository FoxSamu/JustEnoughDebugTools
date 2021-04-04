package net.shadew.debug.test;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.block.Blocks;

public class DebugTests {
    @GameTest
    public static void succeed_in_50_ticks(GameTestHelper helper) {
        helper.runAfterDelay(50, helper::succeed);
    }

    @GameTest(timeoutTicks = 300)
    public static void cat_walk_to_pos(GameTestHelper helper) {
        Cat cat = helper.spawnWithNoFreeWill(EntityType.CAT, 1, 2, 1);

        BlockPos dest = new BlockPos(9, 2, 9);
        helper.onEachTick(() -> helper.walkTo(cat, dest, 1));
        helper.succeedWhen(() -> helper.assertEntityInstancePresent(cat, dest));
    }

    @GameTest
    public static void sand_fall(GameTestHelper helper) {
        helper.setBlock(2, 6, 2, Blocks.SAND);

        BlockPos dest = new BlockPos(2, 2, 2);
        helper.succeedWhenBlockPresent(Blocks.SAND, dest);
    }
}
