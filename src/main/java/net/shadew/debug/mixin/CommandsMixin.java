package net.shadew.debug.mixin;

import net.minecraft.commands.Commands;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Commands.class)
public class CommandsMixin {
    @Redirect(method = {
        "performCommand",
        "<init>"
    }, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;IS_RUNNING_IN_IDE:Z", opcode = Opcodes.GETSTATIC))
    private boolean redirectIsRunningInIDE() {
        return true;
    }
}
