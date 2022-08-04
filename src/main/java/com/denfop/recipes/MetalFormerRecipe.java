package com.denfop.recipes;

import com.denfop.IUItem;
import com.denfop.register.RegisterOreDict;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class MetalFormerRecipe {
    public static final String[] recipe;
    public static final String[] recipe1;

    public static void init() {
        for (int j = 0; j < recipe.length; j++)
            for (int i = 0; i < RegisterOreDict.itemNames().size(); i++) {
                if (j == 0)
                    addmolot(recipe[j] + RegisterOreDict.itemNames().get(i), new ItemStack(IUItem.plate, 1, i), 1);

                if (j != 2 && j != 0)
                    addmolot(recipe[j] + RegisterOreDict.itemNames().get(i), recipe1[j] + RegisterOreDict.itemNames().get(i), 1);
                if (j == 2)
                    addExtruding(recipe[j] + RegisterOreDict.itemNames().get(i), recipe1[j] + RegisterOreDict.itemNames().get(i), 1);

            }
        for (int j = 0; j < recipe.length; j++)
            for (int i = 0; i < RegisterOreDict.itemNames1().size(); i++) {
                if (j != 2)
                    addmolot(recipe[j] + RegisterOreDict.itemNames1().get(i), recipe1[j] + RegisterOreDict.itemNames1().get(i), 1);
            }
    }

    public static void addmolot(final String input, final String output, final int n) {
        final ItemStack stack = OreDictionary.getOres(output).get(0);
        ItemStack copied = stack.copy();
        copied.stackSize = n;
        if (Recipes.metalformerRolling.getRecipes().get(new RecipeInputOreDict(input, 1)) == null) {
            Recipes.metalformerRolling.addRecipe((IRecipeInput)new RecipeInputOreDict(input, 1), (NBTTagCompound)null, new ItemStack[] { copied });
        }

    }

    public static void addmolot(final String input, final ItemStack output, final int n) {
        ItemStack copied = output.copy();
        copied.stackSize = n;
        if (Recipes.metalformerRolling.getRecipes().get(new RecipeInputOreDict(input, 1)) == null) {
            Recipes.metalformerRolling.addRecipe((IRecipeInput)new RecipeInputOreDict(input, 1), (NBTTagCompound)null, new ItemStack[] { copied });
        }
    }

    public static void addExtruding(final String input, final String output, final int n) {
        final ItemStack stack = OreDictionary.getOres(output).get(0);
        ItemStack copied = stack.copy();
        copied.stackSize = n;
        if (Recipes.metalformerExtruding.getRecipes().get(new RecipeInputOreDict(input, 1)) == null) {
            Recipes.metalformerExtruding.addRecipe((IRecipeInput)new RecipeInputOreDict(input, 1), (NBTTagCompound)null, new ItemStack[] { copied });
        }

    }
    static {
        recipe = new String[] { "ingot", "plate", "ingot" };
        recipe1 = new String[] { "plate", "casing", "stik" };
    }
}
