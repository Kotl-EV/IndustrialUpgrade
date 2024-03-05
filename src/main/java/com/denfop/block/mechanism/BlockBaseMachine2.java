package com.denfop.block.mechanism;

import com.denfop.IUCore;
import com.denfop.api.utils.textures.TextureAtlasSheet;
import com.denfop.item.mechanism.ItemBaseMachine2;
import com.denfop.proxy.ClientProxy;
import com.denfop.tiles.base.TileEntityMagnetGenerator;
import com.denfop.tiles.mechanism.TileEntityAdvAlloySmelter;
import com.denfop.tiles.mechanism.TileEntityAdvGeoGenerator;
import com.denfop.tiles.mechanism.TileEntityAdvKineticGenerator;
import com.denfop.tiles.mechanism.TileEntityEnrichment;
import com.denfop.tiles.mechanism.TileEntityHandlerHeavyOre;
import com.denfop.tiles.mechanism.TileEntityImpGeoGenerator;
import com.denfop.tiles.mechanism.TileEntityImpKineticGenerator;
import com.denfop.tiles.mechanism.TileEntityMagnet;
import com.denfop.tiles.mechanism.TileEntityPerGeoGenerator;
import com.denfop.tiles.mechanism.TileEntityPerKineticGenerator;
import com.denfop.tiles.mechanism.TileEntitySynthesis;
import com.denfop.tiles.mechanism.TileEntityWitherMaker;
import com.denfop.tiles.neutroniumgenerator.TileneutronGenerator;
import com.denfop.tiles.reactors.TileEntityAdvNuclearReactorElectric;
import com.denfop.tiles.reactors.TileEntityImpNuclearReactor;
import com.denfop.tiles.reactors.TileEntityPerNuclearReactor;
import com.denfop.utils.CheckWrench;
import cpw.mods.fml.common.registry.GameRegistry;
import ic2.api.item.IC2Items;
import ic2.api.tile.IWrenchable;
import ic2.core.block.TileEntityBlock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBaseMachine2 extends BlockContainer {
    public static final String[] names = new String[]{"AdvKG", "ImpKG", "PerKG", "AdvAlloy", "AdvGeo", "ImpGeo", "PerGeo", "AdvRea", "ImpRea", "PerRea", "Enriched", "Synthesis", "HandlerHO", "WitherMaker", "Magnet", "genMagnet"};
    private IIcon[][] iconBuffer;

    public BlockBaseMachine2() {
        super(Material.iron);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(IUCore.tabssp);
        GameRegistry.registerBlock(this, ItemBaseMachine2.class, "BlockBaseMachine2");
    }

    public TileEntity createTileEntity(World world, int meta) {
        switch(meta) {
            case 0:
                return new TileEntityAdvKineticGenerator();
            case 1:
                return new TileEntityImpKineticGenerator();
            case 2:
                return new TileEntityPerKineticGenerator();
            case 3:
                return new TileEntityAdvAlloySmelter();
            case 4:
                return new TileEntityAdvGeoGenerator();
            case 5:
                return new TileEntityImpGeoGenerator();
            case 6:
                return new TileEntityPerGeoGenerator();
            case 7:
                return new TileEntityAdvNuclearReactorElectric();
            case 8:
                return new TileEntityImpNuclearReactor();
            case 9:
                return new TileEntityPerNuclearReactor();
            case 10:
                return new TileEntityEnrichment();
            case 11:
                return new TileEntitySynthesis();
            case 12:
                return new TileEntityHandlerHeavyOre();
            case 13:
                return new TileEntityWitherMaker();
            case 14:
                return new TileEntityMagnet();
            case 15:
                return new TileEntityMagnetGenerator();
            default:
                return null;
        }
    }

    public void registerIcons(IIconRegister par1IconRegister) {
        this.iconBuffer = new IIcon[names.length][12];

        for(int i = 0; i < names.length; ++i) {
            IIcon[] icons = TextureAtlasSheet.unstitchIcons(par1IconRegister, "industrialupgrade:block" + names[i], 12, 1);
            System.arraycopy(icons, 0, this.iconBuffer[i], 0, icons.length);
        }

    }

    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
        int blockMeta = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        int facing = te instanceof TileEntityBlock ? ((TileEntityBlock)te).getFacing() : 0;
        return this.isActive(world, x, y, z) ? this.iconBuffer[blockMeta][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6] : this.iconBuffer[blockMeta][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    }

    public IIcon getIcon(int blockSide, int blockMeta) {
        return this.iconBuffer[blockMeta][ClientProxy.sideAndFacingToSpriteOffset[blockSide][3]];
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return null;
    }

    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> dropList = super.getDrops(world, x, y, z, metadata, fortune);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInventory) {
            IInventory iinv = (IInventory)te;

            for(int index = 0; index < iinv.getSizeInventory(); ++index) {
                ItemStack itemstack = iinv.getStackInSlot(index);
                if (itemstack != null) {
                    dropList.add(itemstack);
                    iinv.setInventorySlotContents(index, (ItemStack)null);
                }
            }
        }

        return dropList;
    }

    public void breakBlock(World world, int x, int y, int z, Block blockID, int blockMeta) {
        super.breakBlock(world, x, y, z, blockID, blockMeta);
        boolean var5 = true;

        for(Iterator iter = this.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0).iterator(); iter.hasNext(); var5 = false) {
            ItemStack var7 = (ItemStack)iter.next();
            if (!var5) {
                if (var7 == null) {
                    return;
                }

                double var8 = 0.7D;
                double var10 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
                double var12 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
                double var14 = (double)world.rand.nextFloat() * var8 + (1.0D - var8) * 0.5D;
                EntityItem var16 = new EntityItem(world, (double)x + var10, (double)y + var12, (double)z + var14, var7);
                var16.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(var16);
                return;
            }
        }

    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return IC2Items.getItem("advancedMachine").getItem();
    }

    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        int heading = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntityBlock te = (TileEntityBlock)world.getTileEntity(x, y, z);
        switch(heading) {
            case 0:
                te.setFacing((short)2);
                break;
            case 1:
                te.setFacing((short)5);
                break;
            case 2:
                te.setFacing((short)3);
                break;
            case 3:
                te.setFacing((short)4);
        }

    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
        if (world.isRemote) {
            return true;
        } else if (CheckWrench.getwrench(entityPlayer)) {
            return false;
        } else if (!entityPlayer.isSneaking()) {
            entityPlayer.openGui(IUCore.instance, 0, world, x, y, z);
            return true;
        } else {
            return false;
        }
    }

    private boolean isActive(IBlockAccess iba, int x, int y, int z) {
        return ((TileEntityBlock)iba.getTileEntity(x, y, z)).getActive();
    }

    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
        if (axis == ForgeDirection.UNKNOWN) {
            return false;
        } else {
            TileEntity tileEntity = worldObj.getTileEntity(x, y, z);
            if (tileEntity instanceof IWrenchable) {
                IWrenchable te = (IWrenchable)tileEntity;
                int newFacing = ForgeDirection.getOrientation(te.getFacing()).getRotation(axis).ordinal();
                if (te.wrenchCanSetFacing((EntityPlayer)null, newFacing)) {
                    te.setFacing((short)newFacing);
                }
            }

            return false;
        }
    }

    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        TileEntityBlock te = (TileEntityBlock)world.getTileEntity(x, y, z);
        return te instanceof TileneutronGenerator ? (int)Math.floor(((TileneutronGenerator)te).energy / 1000000.0D * 15.0D) : 0;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }
}
