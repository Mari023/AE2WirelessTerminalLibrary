package de.mari_023.ae2wtlib;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
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
    /**
     * Copied from ae2 <a href=
     * "https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/6ba7da1ce8fd98e9ec5ce65e8d0d16fcde9c3fd3/src/main/java/appeng/server/testplots/AutoCraftingTestPlots.java#L212">...</a>
     * TODO PR: make this public
     */
    private static ItemStack encodeCraftingPattern(ServerLevel level,
            Object[] ingredients,
            boolean allowSubstitutions,
            boolean allowFluidSubstitutions) {

        // Allow a mixed input of items or item stacks as ingredients
        var stacks = Arrays.stream(ingredients)
                .map(in -> {
                    if (in instanceof ItemLike itemLike) {
                        return new ItemStack(itemLike);
                    } else if (in instanceof ItemStack itemStack) {
                        return itemStack;
                    } else if (in == null) {
                        return ItemStack.EMPTY;
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + in);
                    }
                })
                .toArray(ItemStack[]::new);

        var c = new TransientCraftingContainer(new AutoCraftingMenu(), 3, 3);
        for (int i = 0; i < stacks.length; i++) {
            c.setItem(i, stacks[i]);
        }

        var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, c, level).orElseThrow();

        var result = recipe.assemble(c, level.registryAccess());

        return PatternDetailsHelper.encodeCraftingPattern(
                recipe,
                stacks,
                result,
                allowSubstitutions,
                allowFluidSubstitutions);
    }

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
        plot.blockEntity("1 2 0", AEBlocks.MOLECULAR_ASSEMBLER,
                molecularAssembler -> molecularAssembler.getInternalInventory().setItemDirect(10, encodeCraftingPattern(
                        (ServerLevel) molecularAssembler.getLevel(), new Object[] { stack1, stack2 }, false, false)));

        plot.hopper("1 1 0", Direction.WEST);
        plot.hopper("0 2 0", Direction.DOWN, stack3);
        plot.creativeEnergyCell("0 1 1");
        plot.blockEntity("0 1 0", AEBlocks.MOLECULAR_ASSEMBLER,
                molecularAssembler -> molecularAssembler.getInternalInventory().setItemDirect(10, encodeCraftingPattern(
                        (ServerLevel) molecularAssembler.getLevel(), new Object[] { universalStack, stack3 }, false,
                        false)));
        plot.hopper("0 0 0", Direction.DOWN);

        plot.test(test -> test.assertTrue(test.countContainerContentAt(BlockPos.ZERO).isEmpty(),
                "Failed to craft universal Terminal"));
    }
}
