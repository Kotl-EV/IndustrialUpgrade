package com.denfop.block.base;

import com.denfop.Constants;
import com.denfop.IUCore;
import com.denfop.IUItem;
import com.denfop.proxy.ClientProxy;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BlockIngot extends Block {
    public static List<String> itemNames;
    private IIcon[][] IIconsList;

    public BlockIngot() {
        super(Material.iron);
        itemNames = new ArrayList<>();
        this.setCreativeTab(IUCore.tabssp);
        this.setHarvestLevel("pickaxe", 1);
        this.setHardness(1F);
        this.setStepSound(Block.soundTypeStone);
        this.addItemsNames();
        GameRegistry.registerBlock(this, ItemBlockIngot.class, "IUBlock");

    }

    public static List<String> getlist() {
        return itemNames;
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        ret.add(new ItemStack(IUItem.block, 1, metadata));

        return ret;
    }

    @Override
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    public void addItemsNames() {
        for (int i = 0; i < 16; i++)
            itemNames.add(IUItem.name_mineral.get(i) + "_block");

    }

    @Override
    public IIcon getIcon(final int blockSide, final int blockMeta) {

        return this.IIconsList[blockMeta][ClientProxy.sideAndFacingToSpriteOffset[blockSide][3]];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
        int blockMeta = world.getBlockMetadata(x, y, z);
        return this.IIconsList[blockMeta][ClientProxy.sideAndFacingToSpriteOffset[blockSide][3]];

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        this.IIconsList = new IIcon[itemNames.size()][6];

        for (int i = 0; i < itemNames.size(); i++)
            for (int j = 0; j < 6; j++)
                this.IIconsList[i][j] = par1IconRegister.registerIcon(Constants.TEXTURES_MAIN + itemNames.get(i));

    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(final Item item, final CreativeTabs tab, final List subItems) {
        for (int ix = 0; ix < itemNames.size(); ++ix) {
            subItems.add(new ItemStack(this, 1, ix));
        }
    }

}
