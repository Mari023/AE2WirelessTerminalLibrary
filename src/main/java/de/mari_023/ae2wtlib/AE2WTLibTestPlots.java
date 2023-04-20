package de.mari_023.ae2wtlib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import de.mari_023.ae2wtlib.wut.WUTHandler;
import de.mari_023.ae2wtlib.wut.recipe.Common;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.menu.AutoCraftingMenu;
import appeng.server.testplots.TestPlot;
import appeng.server.testworld.PlotBuilder;

public class AE2WTLibTestPlots {
    @TestPlot("wireless_terminal")
    public static void wireless_terminal(PlotBuilder plot) {
        var o = BlockPos.ZERO;
        plot.creativeEnergyCell(o);
        plot.block(o.east(), AEBlocks.SECURITY_STATION);
        plot.cable("[2,17] 0 0");
        plot.blockState(o.east(17).above(), AEBlocks.WIRELESS_ACCESS_POINT.block().defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP));

        plot.block(o.north().below(), Blocks.RED_WOOL);
        plot.block(o.north().below().east(), Blocks.GREEN_WOOL);
        plot.block(o.above(), AEBlocks.QUARTZ_VIBRANT_GLASS);
        plot.block(o.above().east(), AEBlocks.QUARTZ_VIBRANT_GLASS);

        plot.block("[2,4] [0,2] 1", AEBlocks.QUANTUM_RING);
        plot.blockEntity(o.above().east(3).south(), AEBlocks.QUANTUM_LINK, (quantumBridge) -> {
            var singularity = AEItems.QUANTUM_ENTANGLED_SINGULARITY.stack();
            var tag = singularity.getOrCreateTag();
            tag.putLong("freq", 1);
            singularity.setTag(tag);
            quantumBridge.getInternalInventory().addItems(singularity, false);
        });
    }

    @TestPlot("universal_terminal")
    public static void universal_terminal(PlotBuilder plot) {
        var stack1 = AEItems.WIRELESS_CRAFTING_TERMINAL.stack();
        var stack2 = new ItemStack(AE2wtlib.PATTERN_ACCESS_TERMINAL);
        var stack3 = new ItemStack(AE2wtlib.PATTERN_ENCODING_TERMINAL);
        var universalStack = Common.mergeTerminal(
                Common.mergeTerminal(new ItemStack(AE2wtlib.UNIVERSAL_TERMINAL), stack1,
                        WUTHandler.getCurrentTerminal(stack1)),
                stack2, WUTHandler.getCurrentTerminal(stack2));

        plot.hopper("2 2 0", Direction.WEST, stack2);

        plot.hopper("1 3 0", Direction.DOWN, stack1);
        plot.creativeEnergyCell("1 2 1");
        plot.blockEntity("1 2 0", AEBlocks.MOLECULAR_ASSEMBLER, molecularAssembler -> {
            // recipe
            var craftingContainer = new CraftingContainer(new AutoCraftingMenu(), 3, 3);
            craftingContainer.setItem(0, stack1);
            craftingContainer.setItem(1, stack2);
            var level = molecularAssembler.getLevel();
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level).get();

            // Encode pattern
            var sparseInputs = new ItemStack[9];
            sparseInputs[0] = stack1;
            sparseInputs[1] = stack2;
            for (int i = 2; i < 9; ++i)
                sparseInputs[i] = ItemStack.EMPTY;
            var encodedPattern = PatternDetailsHelper.encodeCraftingPattern(recipe, sparseInputs, universalStack,
                    true, false);

            molecularAssembler.getInternalInventory().setItemDirect(10, encodedPattern);
        });

        plot.hopper("1 1 0", Direction.WEST);
        plot.hopper("0 2 0", Direction.DOWN, stack3);
        plot.creativeEnergyCell("0 1 1");
        plot.blockEntity("0 1 0", AEBlocks.MOLECULAR_ASSEMBLER, molecularAssembler -> {
            // recipe
            var craftingContainer = new CraftingContainer(new AutoCraftingMenu(), 3, 3);
            craftingContainer.setItem(0, universalStack);
            craftingContainer.setItem(1, stack3);
            var level = molecularAssembler.getLevel();
            var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level).get();

            // Encode pattern
            var sparseInputs = new ItemStack[9];
            sparseInputs[0] = universalStack;
            sparseInputs[1] = stack3;
            for (int i = 2; i < 9; ++i)
                sparseInputs[i] = ItemStack.EMPTY;
            var encodedPattern = PatternDetailsHelper.encodeCraftingPattern(recipe, sparseInputs, universalStack,
                    true, false);

            molecularAssembler.getInternalInventory().setItemDirect(10, encodedPattern);
        });
        plot.hopper("0 0 0", Direction.DOWN);

        plot.test(test -> test.assertTrue(test.countContainerContentAt(BlockPos.ZERO).isEmpty(),
                "Failed to craft universal Terminal"));
    }
}
