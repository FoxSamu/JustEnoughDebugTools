package net.shadew.debug.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRules.IntRule.class)
public interface GameRulesIntRuleAccessor {
    @Accessor("value")
    void setRuleValue(int value);
}
