package com.denfop.tiles.mechanism;

import com.denfop.invslot.InvSlotProcessableMultiGeneric;
import com.denfop.tiles.base.TileEntityMultiMachine;
import ic2.api.recipe.Recipes;
import ic2.core.upgrade.UpgradableProperty;
import net.minecraft.util.StatCollector;

import java.util.EnumSet;
import java.util.Set;

public class TileEntityDoubleExtractor extends TileEntityMultiMachine {
    public TileEntityDoubleExtractor() {
        super(EnumMultiMachine.DOUBLE_EXTRACTOR.usagePerTick, EnumMultiMachine.DOUBLE_EXTRACTOR.lenghtOperation, Recipes.extractor, 0);
        this.inputSlots = new InvSlotProcessableMultiGeneric(this, "input", 2, Recipes.extractor);
    }

    @Override
    public EnumMultiMachine getMachine() {
        return EnumMultiMachine.DOUBLE_EXTRACTOR;
    }

    public String getInventoryName() {
        return StatCollector.translateToLocal("iu.blockExtractor.name");
    }

    public String getStartSoundFile() {
        return "Machines/ExtractorOp.ogg";
    }

    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }

    public float getWrenchDropRate() {
        return 0.85F;
    }

    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing,
                UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer,
                UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming,
                UpgradableProperty.ItemProducing);
    }

}
