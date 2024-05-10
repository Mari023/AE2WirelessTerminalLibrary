package de.mari_023.ae2wtlib;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import appeng.blockentity.qnb.QuantumBridgeBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.server.testplots.CraftingPatternHelper;
import appeng.server.testplots.TestPlot;
import appeng.server.testplots.TestPlotClass;
import appeng.server.testworld.PlotBuilder;

import de.mari_023.ae2wtlib.recipe.Common;
import de.mari_023.ae2wtlib.wut.WUTHandler;

@TestPlotClass
public class AE2wtlibTestPlots {
    @TestPlot("wireless_terminal")
    public static void wireless_terminal(PlotBuilder plot) {
        var o = BlockPos.ZERO;
        plot.creativeEnergyCell(o);
        plot.cable("[1,17] 0 0");
        plot.blockState(o.east(17).above(), AEBlocks.WIRELESS_ACCESS_POINT.block().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP));

        plot.block(o.north().below(), Blocks.RED_WOOL);
        plot.block(o.north().below().east(), Blocks.GREEN_WOOL);
        plot.block(o.above(), AEBlocks.QUARTZ_VIBRANT_GLASS);
        plot.block(o.above().east(), AEBlocks.QUARTZ_VIBRANT_GLASS);

        plot.block("[2,4] [0,2] 1", AEBlocks.QUANTUM_RING);
        plot.blockEntity(o.above().east(3).south(), AEBlocks.QUANTUM_LINK, (quantumBridge) -> {
            var singularity = AEItems.QUANTUM_ENTANGLED_SINGULARITY.stack();
            QuantumBridgeBlockEntity.assignFrequency(singularity);
            quantumBridge.getInternalInventory().addItems(singularity, false);
        });
    }

    @TestPlot("universal_terminal")
    public static void universal_terminal(PlotBuilder plot) {
        var stack1 = AEItems.WIRELESS_CRAFTING_TERMINAL.stack();
        var stack2 = new ItemStack(AE2wtlibItems.PATTERN_ACCESS_TERMINAL);
        var stack3 = new ItemStack(AE2wtlibItems.PATTERN_ENCODING_TERMINAL);
        var universalStack = Common.mergeTerminal(
                Common.mergeTerminal(new ItemStack(AE2wtlibItems.UNIVERSAL_TERMINAL), stack1,
                        WUTHandler.getCurrentTerminal(stack1)),
                stack2, WUTHandler.getCurrentTerminal(stack2));

        plot.hopper("2 2 0", Direction.WEST, stack2);

        plot.hopper("1 3 0", Direction.DOWN, stack1);
        plot.creativeEnergyCell("1 2 1");
        plot.blockEntity("1 2 0", AEBlocks.MOLECULAR_ASSEMBLER,
                molecularAssembler -> {
                    ServerLevel level = (ServerLevel) Objects.requireNonNull(molecularAssembler.getLevel());
                    molecularAssembler.getInternalInventory().setItemDirect(10,
                            CraftingPatternHelper.encodeShapelessCraftingRecipe(level, stack1, stack2));
                });

        plot.hopper("1 1 0", Direction.WEST);
        plot.hopper("0 2 0", Direction.DOWN, stack3);
        plot.creativeEnergyCell("0 1 1");
        plot.blockEntity("0 1 0", AEBlocks.MOLECULAR_ASSEMBLER,
                molecularAssembler -> {
                    ServerLevel level = (ServerLevel) Objects.requireNonNull(molecularAssembler.getLevel());
                    molecularAssembler.getInternalInventory().setItemDirect(10,
                            CraftingPatternHelper.encodeShapelessCraftingRecipe(level, universalStack, stack3));
                });
        plot.hopper("0 0 0", Direction.DOWN);

        plot.test(test -> test.assertTrue(test.countContainerContentAt(BlockPos.ZERO).isEmpty(),
                "Failed to craft universal Terminal"));
    }
}
