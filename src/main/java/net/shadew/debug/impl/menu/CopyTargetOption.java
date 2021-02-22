package net.shadew.debug.impl.menu;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

import net.shadew.debug.api.menu.ActionOption;
import net.shadew.debug.api.menu.OptionSelectContext;

public class CopyTargetOption extends ActionOption {
    public CopyTargetOption(Text name) {
        super(name);
    }

    @Override
    public void onClick(OptionSelectContext context) {
        copyLookAt(context.client(), context, true, !context.client().hasReducedDebugInfo());
    }

    private void copyLookAt(MinecraftClient client, OptionSelectContext context, boolean copyNbt, boolean server) {
        assert client.player != null;
        assert client.world != null;

        HitResult target = client.crosshairTarget;
        if (target != null) {
            switch(target.getType()) {
                case BLOCK:
                    BlockPos pos = ((BlockHitResult)target).getBlockPos();
                    BlockState state = client.world.getBlockState(pos);

                    if (copyNbt) {
                        if (server) {
                            client.player.networkHandler.getDataQueryHandler().queryBlockNbt(pos, nbt -> {
                                copyBlock(context, state, pos, nbt);
                                context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_server_block"));
                            });
                        } else {
                            BlockEntity be = client.world.getBlockEntity(pos);
                            CompoundTag nbt = be != null ? be.toTag(new CompoundTag()) : null;
                            copyBlock(context, state, pos, nbt);
                            context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_client_block"));
                        }
                    } else {
                        copyBlock(context, state, pos, null);
                        context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_client_state"));
                    }
                    break;
                case ENTITY:
                    Entity entity = ((EntityHitResult)target).getEntity();
                    Identifier id = Registry.ENTITY_TYPE.getId(entity.getType());
                    if (copyNbt) {
                        if (server) {
                            client.player.networkHandler.getDataQueryHandler().queryEntityNbt(entity.getEntityId(), nbt -> {
                                copyEntity(context, id, entity.getPos(), nbt);
                                context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_server_entity"));
                            });
                        } else {
                            CompoundTag nbt = entity.toTag(new CompoundTag());
                            copyEntity(context, id, entity.getPos(), nbt);
                            context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_client_entity"));
                        }
                    } else {
                        copyEntity(context, id, entity.getPos(), null);
                        context.spawnResponse(new TranslatableText("debug.options.debug.copy_targeted.response_client_location"));
                    }
            }

        }
    }

    protected void copyBlock(OptionSelectContext context, BlockState state, BlockPos pos, CompoundTag tag) {
        if (tag != null) {
            tag.remove("x");
            tag.remove("y");
            tag.remove("z");
            tag.remove("id");
        }

        StringBuilder builder = new StringBuilder(BlockArgumentParser.stringifyBlockState(state));
        if (tag != null) {
            builder.append(tag);
        }

        String str = String.format(Locale.ROOT, "/setblock %d %d %d %s", pos.getX(), pos.getY(), pos.getZ(), builder);
        context.copyToClipboard(str);
    }

    protected void copyEntity(OptionSelectContext context, Identifier id, Vec3d pos, CompoundTag nbt) {
        String str;
        if (nbt != null) {
            nbt.remove("UUID");
            nbt.remove("Pos");
            nbt.remove("Dimension");
            String nbtStr = nbt.toText().getString();
            str = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", id.toString(), pos.x, pos.y, pos.z, nbtStr);
        } else {
            str = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", id.toString(), pos.x, pos.y, pos.z);
        }

        context.copyToClipboard(str);
    }
}
