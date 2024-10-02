package dev.danielvip3.mixin;

import dev.danielvip3.ThePondHopperFix;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @Unique
    private final static List<Block> disabledBlocks = ThePondHopperFix.CONFIG.getDisabledBlocks().stream().map(name -> Registries.BLOCK.get(new Identifier(name))).toList();

    @Unique
    private static Block getInputBlock(World world, BlockPos pos) {
        BlockPos inputBlockPos = pos.up();
        BlockState inputBlockState = world.getBlockState(inputBlockPos);

        if (!(world.getBlockEntity(inputBlockPos) instanceof Inventory)) return null;

        return inputBlockState.getBlock();
    }

    @Unique
    private static Block getOutputBlock(World world, BlockPos pos, BlockState state) {
        BlockPos outputBlockPos;

        Direction outputDirection = Direction.DOWN;
        try {
            outputDirection = state.get(HopperBlock.FACING);
        } catch (IllegalArgumentException ignored) {}

        if (outputDirection == Direction.DOWN) {
            outputBlockPos = pos.down();
        } else {
            outputBlockPos = pos.offset(outputDirection);
        }

        BlockState outputBlockState = world.getBlockState(outputBlockPos);

        if (!(world.getBlockEntity(outputBlockPos) instanceof Inventory)) return null;

        return outputBlockState.getBlock();
    }

    @Inject(method="insert", at = @At("HEAD"), cancellable = true)
    private static void disableHopperInsert(World world, BlockPos pos, BlockState state, Inventory inventory, CallbackInfoReturnable<Boolean> cir) {
        Block inputBlock = getInputBlock(world, pos);
        Block outputBlock = getOutputBlock(world, pos, state);

        if (disabledBlocks.contains(inputBlock) || disabledBlocks.contains(outputBlock)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method="Lnet/minecraft/block/entity/HopperBlockEntity;extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void disableHopperExtract(World world, Hopper hopper, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        BlockState state = world.getBlockState(pos);

        Block inputBlock = getInputBlock(world, pos);
        Block outputBlock = getOutputBlock(world, pos, state);

        if (disabledBlocks.contains(inputBlock) || disabledBlocks.contains(outputBlock)) {
            cir.setReturnValue(false);
        }
    }
}
