package dev.runefox.jedt.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;

import java.util.ArrayList;
import java.util.List;

public class DebugTests {
    @GameTest(batch = "DebugTests")
    public static void succeed_in_50_ticks(GameTestHelper helper) {
        helper.runAfterDelay(50, helper::succeed);
    }

    @GameTest(batch = "DebugTests", timeoutTicks = 300)
    public static void cat_walk_to_pos(GameTestHelper helper) {
        Cat cat = helper.spawnWithNoFreeWill(EntityType.CAT, 1, 2, 1);

        BlockPos dest = new BlockPos(9, 2, 9);
        helper.onEachTick(() -> helper.walkTo(cat, dest, 1));
        helper.succeedWhen(() -> helper.assertEntityInstancePresent(cat, dest));
    }

    @GameTestGenerator
    public static List<TestFunction> sand_fall() {
        List<TestFunction> list = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof FallingBlock && !(block instanceof AnvilBlock)) {
                list.add(new TestFunction(
                    "DebugTests",
                    "debugtests.fall_" + BuiltInRegistries.BLOCK.getKey(block).getPath(),
                    "debugtests.sand_fall",
                    100,
                    0,
                    true,
                    helper -> {
                        helper.setBlock(2, 6, 2, block);

                        BlockPos dest = new BlockPos(2, 2, 2);
                        helper.succeedWhenBlockPresent(block, dest);
                    }
                ));
            }
        }

        return list;
    }
}
