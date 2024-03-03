package com.denfop.tiles.reactors;

import ic2.api.Direction;
import ic2.api.reactor.IReactorComponent;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.ExplosionIC2.Type;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.apache.logging.log4j.Level;

public class TileEntityAdvNuclearReactorElectric extends TileEntityBaseNuclearReactorElectric {
    public TileEntityAdvNuclearReactorElectric() {
        super(10, 6, "textures/gui/GUIAdvNuclearReaktor.png", 1.2D);
    }

    public void setblock() {
        Direction[] var1 = Direction.directions;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Direction direction = var1[var3];
            TileEntity target = direction.applyToTileEntity(this);
            if (target instanceof TileEntityAdvReactorChamberElectric) {
                this.worldObj.setBlockToAir(target.xCoord, target.yCoord, target.zCoord);
            }
        }

        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    }

    public void explode() {
        float boomPower = 10.0F;
        float boomMod = 1.0F;

        for(int i = 0; i < this.reactorSlot.size(); ++i) {
            ItemStack stack = this.reactorSlot.get(i);
            if (stack != null && stack.getItem() instanceof IReactorComponent) {
                float f = ((IReactorComponent)stack.getItem()).influenceExplosion(this, stack);
                if (f > 0.0F && f < 1.0F) {
                    boomMod *= f;
                } else {
                    boomPower += f;
                }
            }

            this.reactorSlot.put(i, (ItemStack)null);
        }

        boomPower *= this.hem * boomMod;
        IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Nuclear Reactor at %s melted (raw explosion power %f)", new Object[]{Util.formatPosition(this), boomPower});
        boomPower = Math.min(boomPower, ConfigUtil.getFloat(MainConfig.get(), "protection/reactorExplosionPowerLimit"));
        Direction[] var8 = Direction.directions;
        Direction[] var10 = var8;
        int var12 = var8.length;

        for(int var6 = 0; var6 < var12; ++var6) {
            Direction direction = var10[var6];
            TileEntity target = direction.applyToTileEntity(this);
            if (target instanceof TileEntityAdvReactorChamberElectric) {
                this.worldObj.setBlockToAir(target.xCoord, target.yCoord, target.zCoord);
            }
        }

        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, (Entity)null, (double)this.xCoord, (double)this.yCoord, (double)this.zCoord, boomPower, 0.01F, Type.Nuclear);
        explosion.doExplosion();
    }

    public short getReactorSize() {
        if (this.worldObj == null) {
            return (short)this.sizeX;
        } else {
            short cols = (short)(this.sizeX - 6);
            Direction[] var2 = Direction.directions;
            Direction[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Direction direction = var3[var5];
                TileEntity target = direction.applyToTileEntity(this);
                if (target instanceof TileEntityAdvReactorChamberElectric) {
                    ++cols;
                }
            }

            return cols;
        }
    }

    public List<TileEntity> getSubTiles() {
        if (this.subTiles == null) {
            this.subTiles = new ArrayList();
            this.subTiles.add(this);
            Direction[] var1 = Direction.directions;
            Direction[] var2 = var1;
            int var3 = var1.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Direction dir = var2[var4];
                TileEntity te = dir.applyToTileEntity(this);
                if (te instanceof TileEntityAdvReactorChamberElectric && !te.isInvalid()) {
                    this.subTiles.add(te);
                }
            }
        }

        return this.subTiles;
    }
}
