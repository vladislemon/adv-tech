package net.vladislemon.mc.advtech.core.energy;

/**
 * Created by user on 1/8/2017.
 */
public interface IEnergyItem {

    double getDefaultEnergy();

    double getDefaultMaxEnergy();

    double getDefaultMaxTransfer();

    boolean isDefaultProvideEU();

    int getDefaultTier();
}
