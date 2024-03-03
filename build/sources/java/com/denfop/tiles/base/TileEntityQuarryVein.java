package com.denfop.tiles.base;

import com.denfop.IUItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.network.NetworkManager;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;

public class TileEntityQuarryVein extends TileEntityElectricMachine implements IHasGui, INetworkUpdateListener, INetworkDataProvider, INetworkClientTileEntityEventListener {
    public boolean empty = true;
    public int x = 0;
    public int y = 0;
    public int z = 0;
    public int progress = 0;
    public int number = 0;
    public boolean analysis = true;

    public TileEntityQuarryVein() {
        super(20.0D, 14, 1);
    }

    private void updateTileEntityField() {
        ((NetworkManager)IC2.network.get()).updateTileEntityField(this, "x");
        ((NetworkManager)IC2.network.get()).updateTileEntityField(this, "y");
        ((NetworkManager)IC2.network.get()).updateTileEntityField(this, "z");
        ((NetworkManager)IC2.network.get()).updateTileEntityField(this, "analysis");
        ((NetworkManager)IC2.network.get()).updateTileEntityField(this, "empty");
    }

    public void updateEntityServer() {
        super.updateEntityServer();
        if (this.worldObj.provider.getWorldTime() % 40L == 0L) {
            this.updateTileEntityField();
        }

        if (this.worldObj.provider.dimensionId != 0) {
            this.empty = true;
        } else {
            int chunkx = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord).getChunkCoordIntPair().chunkXPos * 16;
            int chunkz = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord).getChunkCoordIntPair().chunkZPos * 16;
            if (this.worldObj.getTileEntity(chunkx, 0, chunkz) != null && (this.worldObj.getTileEntity(chunkx, 0, chunkz) instanceof TileOilBlock || this.worldObj.getTileEntity(chunkx, 0, chunkz) instanceof TileEntityVein)) {
                if (this.worldObj.getTileEntity(chunkx, 0, chunkz) instanceof TileEntityVein) {
                    TileEntityVein tile1 = (TileEntityVein)this.worldObj.getTileEntity(chunkx, 0, chunkz);
                    if (tile1.change) {
                        this.number = tile1.number;
                        this.analysis = false;
                        this.empty = false;
                        this.progress = 1200;
                        this.x = chunkx;
                        this.y = 0;
                        this.z = chunkz;
                        return;
                    }
                } else if (this.worldObj.getTileEntity(chunkx, 0, chunkz) instanceof TileOilBlock) {
                    TileOilBlock tile1 = (TileOilBlock)this.worldObj.getTileEntity(chunkx, 0, chunkz);
                    if (tile1.change && !tile1.empty) {
                        this.number = tile1.number;
                        this.analysis = false;
                        this.progress = 1200;
                        this.x = chunkx;
                        this.y = 0;
                        this.z = chunkz;
                        this.empty = tile1.empty;
                        return;
                    }

                    this.empty = true;
                }
            }

            if (this.analysis && this.energy >= 5.0D) {
                ++this.progress;
                this.energy -= 5.0D;
                if (this.progress >= 1200) {
                    this.analysis = false;
                    chunkx = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord).getChunkCoordIntPair().chunkXPos * 16;
                    chunkz = this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord).getChunkCoordIntPair().chunkZPos * 16;
                    Random rand = new Random();
                    int p = rand.nextInt(100);
                    if (p >= 10) {
                        this.worldObj.setBlock(chunkx, 0, chunkz, IUItem.oilblock);
                        TileOilBlock oil = (TileOilBlock)this.worldObj.getTileEntity(chunkx, 0, chunkz);
                        oil.change = true;
                        this.getnumber(oil);
                        this.x = chunkx;
                        this.y = 0;
                        this.z = chunkz;
                        this.number = oil.number;
                    } else if (this.worldObj.getBlock(chunkx, 0, chunkz) != null && p < 10) {
                        int k = rand.nextInt(12);
                        this.worldObj.setBlock(chunkx, 0, chunkz, IUItem.vein, k, 3);
                        TileEntityVein vein = (TileEntityVein)this.worldObj.getTileEntity(chunkx, 0, chunkz);
                        vein.change = true;
                        vein.meta = k;
                        this.number = vein.number;
                        this.x = chunkx;
                        this.y = 0;
                        this.z = chunkz;
                    }
                }
            }

        }
    }

    private void getnumber(TileOilBlock tile) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(tile.xCoord, tile.zCoord);
        Random rand = this.worldObj.rand;
        int random;
        if (BiomeGenBase.desert.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 40) {
                tile.number = rand.nextInt(50000) + 20000;
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (biome.biomeID == 130) {
            random = rand.nextInt(100);
            if (random > 40) {
                tile.number = rand.nextInt(50000) + 20000;
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.ocean.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 65) {
                tile.number = rand.nextInt(80000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.deepOcean.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 40) {
                tile.number = rand.nextInt(80000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.frozenOcean.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 65) {
                tile.number = rand.nextInt(80000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.desertHills.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 40) {
                tile.number = rand.nextInt(60000) + 20000;
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.river.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 55) {
                tile.number = rand.nextInt(20000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else if (BiomeGenBase.savanna.equals(biome)) {
            random = rand.nextInt(100);
            if (random > 55) {
                tile.number = rand.nextInt(40000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        } else {
            random = rand.nextInt(100);
            if (random > 75) {
                tile.number = rand.nextInt(20000);
            } else {
                tile.empty = true;
                tile.number = 0;
            }
        }

        tile.max = tile.number;
    }

    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    public double getDemandedEnergy() {
        return this.maxEnergy - this.energy;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.empty = nbttagcompound.getBoolean("empty");
        this.analysis = nbttagcompound.getBoolean("analysis");
        this.progress = nbttagcompound.getInteger("progress");
        this.number = nbttagcompound.getInteger("number");
        this.x = nbttagcompound.getInteger("x1");
        this.y = nbttagcompound.getInteger("y1");
        this.z = nbttagcompound.getInteger("z1");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("empty", this.empty);
        nbttagcompound.setBoolean("analysis", this.analysis);
        nbttagcompound.setInteger("progress", this.progress);
        nbttagcompound.setInteger("number", this.number);
        nbttagcompound.setInteger("x1", this.x);
        nbttagcompound.setInteger("y1", this.y);
        nbttagcompound.setInteger("z1", this.z);
    }

    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return null;
    }

    public ContainerBase<? extends TileEntityAnalyzer> getGuiContainer(EntityPlayer entityPlayer) {
        return null;
    }

    public void onNetworkEvent(EntityPlayer player, int event) {
    }

    public float getWrenchDropRate() {
        return 0.85F;
    }

    public void onGuiClosed(EntityPlayer arg0) {
    }

    public String getInventoryName() {
        return StatCollector.translateToLocal("iu.blockAnaluzer.name");
    }
}
