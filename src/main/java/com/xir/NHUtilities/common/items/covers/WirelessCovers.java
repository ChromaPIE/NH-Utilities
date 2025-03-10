package com.xir.NHUtilities.common.items.covers;

import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.misc.WirelessNetworkManager.ticks_between_energy_addition;
import static java.lang.Long.min;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import com.xir.NHUtilities.common.api.interfaces.mixinHelper.IWirelessCoverEnergyProvider;

import gregtech.api.covers.CoverContext;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.ISerializableObject.LegacyCoverData;
import gregtech.common.covers.CoverBehavior;
import gregtech.common.misc.WirelessNetworkManager;

@SuppressWarnings("unused")
public class WirelessCovers {

    private WirelessCovers() {}

    // region Wireless Dynamo
    public static class CoverWirelessDynamo extends AbstractWirelessCover {

        public CoverWirelessDynamo(CoverContext context, int voltage, int ampere) {
            super(context, voltage, ampere);
        }

        @Override
        protected void tryOperate(ICoverable tileEntity) {
            if (tileEntity instanceof BaseMetaTileEntity bmt && bmt.getMetaTileEntity() instanceof MetaTileEntity mte) {
                var isEnergyProvider = bmt instanceof IWirelessCoverEnergyProvider provider;
                var currentEU = isEnergyProvider ? ((IWirelessCoverEnergyProvider) bmt).getEnergyToTransfer()
                    : mte.getEUVar();
                if (currentEU <= 0) return; // nothing to transfer
                var euToTransfer = min(currentEU, transferred_energy_per_operation);
                if (addEUToGlobalEnergyMap(bmt.getOwnerUuid(), euToTransfer)) {
                    if (isEnergyProvider) {
                        ((IWirelessCoverEnergyProvider) bmt).setEnergyCache(currentEU - euToTransfer);
                    } else {
                        bmt.decreaseStoredEnergyUnits(euToTransfer, true);
                    }
                }
            }
        }
    }
    // endregion

    // region Wireless Energy
    public static class CoverWirelessEnergy extends AbstractWirelessCover {

        public CoverWirelessEnergy(CoverContext context, int voltage, int ampere) {
            super(context, voltage, ampere);
        }

        @Override
        protected void tryOperate(ICoverable tileEntity) {
            if (tileEntity instanceof BaseMetaTileEntity bmt && bmt.getMetaTileEntity() instanceof MetaTileEntity mte) {
                var currentEU = mte.getEUVar();
                var euToTransfer = min(transferred_energy_per_operation - currentEU, transferred_energy_per_operation);
                if (euToTransfer <= 0) return; // nothing to transfer
                if (addEUToGlobalEnergyMap(bmt.getOwnerUuid(), -euToTransfer)) {
                    bmt.increaseStoredEnergyUnits(euToTransfer, true);
                }
            }
        }
    }
    // endregion

    // region AbstractWirelessCover
    public static abstract class AbstractWirelessCover extends CoverBehavior {

        protected final long transferred_energy_per_operation;

        public AbstractWirelessCover(CoverContext context, int voltage, int ampere) {
            super(context);
            this.transferred_energy_per_operation = ticks_between_energy_addition * (long) ampere * (long) voltage;
        }

        @Override
        public LegacyCoverData doCoverThings(byte aInputRedstone, long aTimer) {
            ICoverable coverable = coveredTile.get();
            int coverDataValue = coverData.get();
            if (coverDataValue == 0 || aTimer % ticks_between_energy_addition == 0) {
                tryOperate(coverable);
            }
            return LegacyCoverData.of(1);
        }

        @Override
        public void onBaseTEDestroyed() {
            ICoverable coverable = coveredTile.get();
            tryOperate(coverable);
        }

        @Override
        public void onCoverRemoval() {
            ICoverable coverable = coveredTile.get();
            tryOperate(coverable);
        }

        protected abstract void tryOperate(ICoverable tileEntity);

        @Override
        protected int getMinimumTickRate() {
            return 10;
        }
        
        @Override
        public int getTickRateAddition() {
            return super.getTickRateAddition();
        }
        
        @Override
        public boolean allowsCopyPasteTool() {
            return false;
        }

        @Override
        public boolean allowsTickRateAddition() {
            return false;
        }

        @Override
        public boolean isRedstoneSensitive(long aTimer) {
            return false;
        }

        @Override
        protected boolean onCoverRightClick(EntityPlayer aPlayer, float aX, float aY, float aZ) {
            return false;
        }

        @Override
        public boolean alwaysLookConnected() {
            return true;
        }

        @Override
        public boolean letsEnergyOut() {
            return true;
        }

        @Override
        public boolean letsEnergyIn() {
            return true;
        }

        @Override
        public boolean letsItemsIn(int slot) {
            return true;
        }

        @Override
        public boolean letsItemsOut(int slot) {
            return true;
        }

        @Override
        public boolean letsFluidIn(Fluid fluid) {
            return true;
        }

        @Override
        public boolean letsFluidOut(Fluid fluid) {
            return true;
        }
    }
    // endregion
}
