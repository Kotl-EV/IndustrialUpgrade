package com.denfop.item.energy;

import buildcraft.api.tools.IToolWrench;
import com.denfop.Constants;
import com.denfop.IUCore;
import com.denfop.proxy.CommonProxy;
import com.denfop.tiles.base.TileEntitySolarPanel;
import com.denfop.utils.Helpers;
import com.denfop.utils.KeyboardClient;
import com.denfop.utils.ModUtils;
import com.eloraam.redpower.core.IRotatable;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.api.item.ElectricItem;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLever;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Method;
import java.util.*;

public class ItemGraviTool extends ItemTool implements IElectricItem, IToolWrench {
    public static final IIcon[] iconsList = new IIcon[5];
    public static final int hoeTextureIndex = 0;
    public static final int treeTapTextureIndex = 1;
    public static final int wrenchTextureIndex = 2;
    public static final int screwDriverTextureIndex = 3;
    private final Set<Class<? extends Block>> shiftRotations = Sets.newHashSet(BlockLever.class, BlockButton.class, BlockChest.class);
    private final String name;
    private final int energyPerWrenchStandartOperation = 500;

    public ItemGraviTool(String name, ToolMaterial toolMaterial) {
        super(0F, toolMaterial, new HashSet());
        this.setMaxDamage(27);
        super.efficiencyOnProperMaterial = 16F;
        setCreativeTab(IUCore.tabssp2);
        setUnlocalizedName(name);

        setNoRepair();
        this.name = name;
        GameRegistry.registerItem(this, name);
    }


    public static void setToolName(final ItemStack itemStack) {
    }

    public static void dropAsEntity(World world, int x, int y, int z, ItemStack stack) {
        if (stack != null) {
            double xOffset = world.rand.nextFloat() * 0.7D + (1D - 0.7D) * 0.5D;
            double yOffset = world.rand.nextFloat() * 0.7D + (1D - 0.7D) * 0.5D;
            double zOffset = world.rand.nextFloat() * 0.7D + (1D - 0.7D) * 0.5D;
            EntityItem entity = new EntityItem(world, x + xOffset, y + yOffset, z + zOffset, stack.copy());
            entity.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entity);
        }
    }

    public static MovingObjectPosition retraceBlock(World var0, EntityPlayer var1, int var2, int var3, int var4) {
        Vec3 var5 = Vec3.createVectorHelper(var1.posX, var1.posY + 1.62D - (double) var1.yOffset, var1.posZ);
        Vec3 var6 = var1.getLook(1.0F);
        Vec3 var7 = var5.addVector(var6.xCoord * 5.0D, var6.yCoord * 5.0D, var6.zCoord * 5.0D);
        Block var8 = var0.getBlock(var2, var3, var4);
        return var8 == null ? null : var8.collisionRayTrace(var0, var2, var3, var4, var5, var7);
    }

    public static Block getBlock(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemBlock ? ((ItemBlock) item).field_150939_a : null;
    }

    public static ItemStack copyWithSize(ItemStack stack, int size) {
        ItemStack ret = stack.copy();
        ret.stackSize = size;
        return ret;
    }

    public static Integer readToolMode(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ModUtils.nbt(itemstack);
        int mode = nbttagcompound.getInteger("toolMode");


        if (mode <= 0 || mode > 5)
            mode = 1;

        return mode;
    }

    public static Integer readTextureIndex(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = ModUtils.nbt(itemstack);
        int textureIndex = nbttagcompound.getInteger("textureIndex");
        if (textureIndex <= 0)
            textureIndex = hoeTextureIndex;

        return textureIndex;
    }

    public static void saveToolMode(ItemStack itemstack, Integer toolMode) {
        NBTTagCompound nbttagcompound = ModUtils.nbt(itemstack);
        nbttagcompound.setInteger("toolMode", toolMode);
        if (toolMode == 1)
            nbttagcompound.setInteger("textureIndex", hoeTextureIndex);

        if (toolMode == 2)
            nbttagcompound.setInteger("textureIndex", treeTapTextureIndex);

        if (toolMode == 3)
            nbttagcompound.setInteger("textureIndex", wrenchTextureIndex);

        if (toolMode == 4)
            nbttagcompound.setInteger("textureIndex", screwDriverTextureIndex);
        if (toolMode == 5)
            nbttagcompound.setInteger("textureIndex", 4);

    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);

        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            par3List.add(StatCollector.translateToLocal("press.lshift"));


        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            par3List.add(StatCollector.translateToLocal("iu.changemode_key") + Keyboard.getKeyName(KeyboardClient.changemode.getKeyCode()) + StatCollector.translateToLocal("iu.changemode_rcm"));

    }

    public boolean canDischarge(ItemStack stack, int amount) {
        return ElectricItem.manager.discharge(stack, amount, Integer.MAX_VALUE, true, false, true) == amount;
    }

    public void dischargeItem(ItemStack stack, EntityPlayer player, int amount) {
        ElectricItem.manager.use(stack, amount, player);
    }

    private boolean isShiftRotation(Class<? extends Block> clazz) {
        Iterator<Class<? extends Block>> iter = this.shiftRotations.iterator();

        Class<? extends Block> shift;
        do {
            if (!iter.hasNext())
                return false;

            shift = iter.next();
        }
        while (!shift.isAssignableFrom(clazz));

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (IC2.platform.isRendering() && IUCore.keyboard.isChangeKeyDown(player)) {
            IUCore.audioManager.playOnce(player, com.denfop.audio.PositionSpec.Hand, "Tools/toolChange.ogg", true, IC2.audioManager.getDefaultVolume());
        }
        if (IUCore.isSimulating() && IUCore.keyboard.isChangeKeyDown(player)) {
            int mode = readToolMode(stack);
            mode++;


            if (mode > 5)
                mode = 1;

            saveToolMode(stack, mode);
            setToolName(stack);
            if (mode == 1)
                CommonProxy.sendPlayerMessage(player, EnumChatFormatting.GOLD + Helpers.formatMessage("iu.graviTool.snap.Hoe") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("iu.message.text.activated"));
            else if (mode == 2)
                CommonProxy.sendPlayerMessage(player, EnumChatFormatting.LIGHT_PURPLE + Helpers.formatMessage("iu.graviTool.snap.TreeTap") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("iu.message.text.activated"));
            else if (mode == 3)
                CommonProxy.sendPlayerMessage(player, EnumChatFormatting.AQUA + Helpers.formatMessage("iu.graviTool.snap.Wrench") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("iu.message.text.activated"));
            else if (mode == 4)
                CommonProxy.sendPlayerMessage(player, EnumChatFormatting.YELLOW + Helpers.formatMessage("iu.graviTool.snap.Screwdriver") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("iu.message.text.activated"));

            else if (mode == 5)
                CommonProxy.sendPlayerMessage(player, EnumChatFormatting.DARK_AQUA + Helpers.formatMessage("iu.graviTool.snap.Purifier") + " " + EnumChatFormatting.GREEN + Helpers.formatMessage("iu.message.text.activated"));
        }


        return stack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float a, float b, float c) {
        setToolName(stack);
        Integer mode = readToolMode(stack);
        if (mode == 3)
            return this.onWrenchUse(stack, player, world, x, y, z, side);
        else if (mode == 4)
            return this.onScrewdriverUse(stack, player, world, x, y, z);
        else
            return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float a, float b, float c) {
        int mode = readToolMode(stack);
        if (mode == 1)
            return this.onHoeUse(stack, player, world, x, y, z, side);
        else if (mode == 2)
            return this.onTreeTapUse(stack, player, world, x, y, z, side);
        else if (mode == 5) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (!(tile instanceof TileEntitySolarPanel))
                return false;
            double energy = 10000;
            if (((TileEntitySolarPanel) tile).time > 0)
                energy = (double) 10000 / (double) (((TileEntitySolarPanel) tile).time / 20);
            if (ElectricItem.manager.canUse(stack, energy)) {
                ((TileEntitySolarPanel) tile).time = 28800;
                ((TileEntitySolarPanel) tile).time1 = 14400;
                ((TileEntitySolarPanel) tile).time2 = 14400;
                ElectricItem.manager.use(stack, energy, player);
                return true;
            }
            return false;
        } else
            return false;
    }

    public boolean onHoeUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        int energyPerHoe = 50;
        if (!player.canPlayerEdit(x, y, z, side, stack))
            return false;
        else if (!this.canDischarge(stack, energyPerHoe)) {
            CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));
            return false;
        } else {
            UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
            if (MinecraftForge.EVENT_BUS.post(event))
                return false;
            else if (event.getResult() == Result.ALLOW) {
                this.dischargeItem(stack, player, energyPerHoe);
                return true;
            } else {
                Block block = world.getBlock(x, y, z);
                if (side != 0 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && (block == Blocks.grass || block == Blocks.dirt)) {
                    Block farmland = Blocks.farmland;
                    world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, farmland.stepSound.getStepResourcePath(), (farmland.stepSound.getVolume() + 1F) / 2F, farmland.stepSound.getPitch() * 0.8F);
                    if (IUCore.isSimulating()) {

                        this.dischargeItem(stack, player, energyPerHoe);
                        world.setBlock(x, y, z, farmland);
                    }
                    return true;
                } else
                    return false;
            }
        }
    }

    public boolean onTreeTapUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        if (Helpers.equals(block, IC2Items.getItem("blockBarrel")))
            try {
                Method method = world.getTileEntity(x, y, z).getClass().getMethod("useTreetapOn", EntityPlayer.class, Integer.TYPE);
                return (Boolean) method.invoke(null, player, side);
            } catch (Throwable ignored) {
            }

        if (Helpers.equals(block, IC2Items.getItem("rubberWood"))) {
            this.attemptExtract(stack, player, world, x, y, z, side, null);
            return true;
        } else
            return false;
    }

    public boolean onWrenchUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side) {
        int energyPerSwitchSide = 50;
        if (!this.canDischarge(stack, energyPerSwitchSide)) {
        } else {


            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            TileEntity tile = world.getTileEntity(x, y, z);

            if (tile instanceof IWrenchable) {
                if (tile instanceof TileEntityTerra)
                    if (((TileEntityTerra) tile).ejectBlueprint()) {
                        if (IUCore.isSimulating())
                            this.dischargeItem(stack, player, energyPerSwitchSide);


                        return IUCore.isSimulating();
                    }

                IWrenchable wrenchable = (IWrenchable) tile;
                if (IC2.keyboard.isAltKeyDown(player)) {
                    if (player.isSneaking())
                        side = (wrenchable.getFacing() + 5) % 6;
                    else
                        side = (wrenchable.getFacing() + 1) % 6;
                } else if (player.isSneaking())
                    side += side % 2 * -2 + 1;

                if (wrenchable.wrenchCanSetFacing(player, side)) {
                    if (IUCore.isSimulating()) {
                        wrenchable.setFacing((short) side);
                        this.dischargeItem(stack, player, energyPerSwitchSide);
                    }
                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
                    }

                    return IUCore.isSimulating();
                }

                if (this.canDischarge(stack, this.energyPerWrenchStandartOperation) && wrenchable.wrenchCanRemove(player)) {
                    if (IUCore.isSimulating()) {
                        boolean dropOriginalBlock;
                        if (wrenchable.getWrenchDropRate() < 1F) {
                            int energyPerWrenchFineOperation = 10000;
                            if (!this.canDischarge(stack, energyPerWrenchFineOperation)) {
                                CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));
                                return true;
                            }

                            dropOriginalBlock = true;
                            this.dischargeItem(stack, player, energyPerWrenchFineOperation);
                        } else {
                            dropOriginalBlock = world.rand.nextFloat() <= wrenchable.getWrenchDropRate();
                            this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);
                        }

                        ArrayList<ItemStack> drops = block.getDrops(world, x, y, z, meta, 0);

                        if (dropOriginalBlock)
                            if (drops.isEmpty())
                                drops.add(wrenchable.getWrenchDrop(player));
                            else
                                drops.set(0, wrenchable.getWrenchDrop(player));

                        for (ItemStack itemStack : drops)
                            dropAsEntity(world, x, y, z, itemStack);

                        world.setBlockToAir(x, y, z);

                    }
                    if (IC2.platform.isRendering()) {
                        IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
                    }
                }
            }

            if (player.isSneaking() != this.isShiftRotation(block.getClass())) {
            } else {
                if (this.canDischarge(stack, this.energyPerWrenchStandartOperation)) {
                    if (block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
                        if (IUCore.isSimulating()) {
                            player.swingItem();
                            this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);
                        }
                        if (IC2.platform.isRendering()) {
                            IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
                        }
                        return true;
                    }
                } else
                    CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));

            }
        }
        return false;
    }

    public void ejectHarz(World world, int x, int y, int z, int side, int quantity) {
        double ejectX = x + 0.5D;
        double ejectY = y + 0.5D;
        double ejectZ = z + 0.5D;
        if (side == 2)
            ejectZ -= 0.3D;
        else if (side == 5)
            ejectX += 0.3D;
        else if (side == 3)
            ejectZ += 0.3D;
        else if (side == 4)
            ejectX -= 0.3D;

        for (int i = 0; i < quantity; ++i) {
            EntityItem entityitem = new EntityItem(world, ejectX, ejectY, ejectZ, IC2Items.getItem("resin").copy());
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public void attemptExtract(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, List<ItemStack> stacks) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta >= 2 && meta % 6 == side) {
            int energyPerTreeTap = 50;
            if (meta < 6) {
                if (!this.canDischarge(stack, energyPerTreeTap)) {
                    CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));
                } else {
                    if (IUCore.isSimulating()) {
                        world.setBlockMetadataWithNotify(x, y, z, meta + 6, 3);
                        if (stacks != null)
                            stacks.add(copyWithSize(IC2Items.getItem("resin"), world.rand.nextInt(3) + 1));
                        else
                            this.ejectHarz(world, x, y, z, side, world.rand.nextInt(3) + 1);

                        Block woodBlock = getBlock(IC2Items.getItem("rubberWood"));
                        if (woodBlock != null) {
                            world.scheduleBlockUpdate(x, y, z, woodBlock, woodBlock.tickRate(world));
                        }
                        if (IC2.platform.isRendering() && player != null) {
                            IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
                        }

                        this.dischargeItem(stack, player, energyPerTreeTap);
                    }

                }
            } else {
                if (world.rand.nextInt(5) == 0 && IUCore.isSimulating())
                    world.setBlockMetadataWithNotify(x, y, z, 1, 3);

                if (world.rand.nextInt(5) == 0) {
                    if (!this.canDischarge(stack, energyPerTreeTap)) {
                        CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));
                    } else {
                        if (IUCore.isSimulating()) {
                            this.ejectHarz(world, x, y, z, side, 1);
                            if (stacks != null)
                                stacks.add(copyWithSize(IC2Items.getItem("resin"), 1));
                            else
                                this.ejectHarz(world, x, y, z, side, 1);
                            if (IC2.platform.isRendering() && player != null) {
                                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
                            }

                            this.dischargeItem(stack, player, energyPerTreeTap);
                        }

                    }
                }
            }
        }
    }

    public boolean onScrewdriverUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        boolean isSneaking = player != null && player.isSneaking();

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block != Blocks.unpowered_repeater && block != Blocks.powered_repeater) {
            if (block == Blocks.dispenser) {
                if (!this.canDischarge(stack, this.energyPerWrenchStandartOperation)) {
                    if (IUCore.isSimulating())
                        CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));

                    return false;
                } else {
                    if (!IUCore.isSimulating())
                        ;

                    if (IUCore.isSimulating())
                        this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);

                    return IUCore.isSimulating();
                }
            } else if (block != Blocks.piston && block != Blocks.sticky_piston) {
                TileEntity tile = world.getTileEntity(x, y, z);
                if (tile instanceof IRotatable) {
                    if (!this.canDischarge(stack, this.energyPerWrenchStandartOperation)) {
                        if (IUCore.isSimulating())
                            CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));

                        return false;
                    } else {
                        MovingObjectPosition mop = null;
                        if (player != null) {
                            mop = retraceBlock(world, player, x, y, z);
                        }
                        if (mop == null)
                            return false;
                        else {
                            int maxRotation = ((IRotatable) tile).getPartMaxRotation(mop.subHit, isSneaking);
                            if (maxRotation == 0)
                                return false;
                            else {
                                int rotation = ((IRotatable) tile).getPartRotation(mop.subHit, isSneaking) + 1;
                                if (rotation > maxRotation)
                                    rotation = 0;


                                if (IUCore.isSimulating()) {
                                    this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);
                                    ((IRotatable) tile).setPartRotation(mop.subHit, isSneaking, rotation);
                                }

                                return IUCore.isSimulating();
                            }
                        }
                    }
                } else
                    return false;
            } else {
                ++meta;
                if (!this.canDischarge(stack, this.energyPerWrenchStandartOperation)) {
                    if (IUCore.isSimulating())
                        CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));

                    return false;
                } else {
                    if (meta > 5)
                        meta = 0;


                    if (IUCore.isSimulating()) {
                        this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);
                        world.setBlockMetadataWithNotify(x, y, z, meta, 7);
                    }

                    return IUCore.isSimulating();
                }
            }
        } else if (!this.canDischarge(stack, this.energyPerWrenchStandartOperation)) {
            if (IUCore.isSimulating())
                CommonProxy.sendPlayerMessage(player, Helpers.formatMessage("message.text.noenergy"));

            return false;
        } else {

            if (IUCore.isSimulating()) {
                this.dischargeItem(stack, player, this.energyPerWrenchStandartOperation);
                world.setBlockMetadataWithNotify(x, y, z, meta & 12 | meta + 1 & 3, 7);
            }

            return IUCore.isSimulating();
        }
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return 300000;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 2;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 10000;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs var2, List var3) {
        ItemStack var4 = new ItemStack(this, 1);
        ElectricItem.manager.charge(var4, 2.147483647E9D, Integer.MAX_VALUE, true, false);
        var3.add(var4);
        var3.add(new ItemStack(this, 1, this.getMaxDamage()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        iconsList[0] = iconRegister.registerIcon(Constants.TEXTURES + ":" + name + "Hoe");
        iconsList[1] = iconRegister.registerIcon(Constants.TEXTURES + ":" + name + "TreeTap");
        iconsList[2] = iconRegister.registerIcon(Constants.TEXTURES + ":" + name + "Wrench");
        iconsList[3] = iconRegister.registerIcon(Constants.TEXTURES + ":" + name + "Screwdriver");
        iconsList[4] = iconRegister.registerIcon(Constants.TEXTURES + ":" + name + "Purifier");
        super.itemIcon = iconsList[0];
    }

    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack itemStack, int pass) {
        Integer myIndex = readTextureIndex(itemStack);
        return iconsList[myIndex];
    }

    @Override
    public Item getChargedItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack itemStack) {
        return this;
    }

    @Override
    public boolean canWrench(EntityPlayer paramEntityPlayer, int paramInt1, int paramInt2, int paramInt3) {
        ItemStack itemstack = paramEntityPlayer.inventory.getCurrentItem();
        Integer toolMode = readToolMode(itemstack);
        if (toolMode == 3) {
            if (canDischarge(itemstack, this.energyPerWrenchStandartOperation))
                return true;
            if (IC2.platform.isSimulating())
                IC2.platform.messagePlayer(paramEntityPlayer, Helpers.formatMessage("message.text.noenergy"));
            return false;
        }
        return false;

    }

    @Override
    public void wrenchUsed(EntityPlayer paramEntityPlayer, int paramInt1, int paramInt2, int paramInt3) {
        if (IC2.platform.isSimulating()) {
            ItemStack itemstack = paramEntityPlayer.inventory.getCurrentItem();
            dischargeItem(itemstack, paramEntityPlayer, this.energyPerWrenchStandartOperation);
        }
    }
}
