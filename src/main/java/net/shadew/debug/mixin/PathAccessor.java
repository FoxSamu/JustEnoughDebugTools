package net.shadew.debug.mixin;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(Path.class)
public interface PathAccessor {
    @Accessor("nodes")
    List<PathNode> getNodes();

    @Accessor("field_57")
    PathNode[] getOpenList();

    @Accessor("field_55")
    PathNode[] getClosedList();

    @Accessor("currentNodeIndex")
    int getCurrentNodeIndex();

    @Accessor("target")
    BlockPos getTarget();

    @Accessor("reachesTarget")
    boolean reachesTarget();
}
