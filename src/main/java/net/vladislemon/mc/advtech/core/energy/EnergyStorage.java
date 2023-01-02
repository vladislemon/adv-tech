package net.vladislemon.mc.advtech.core.energy;

/**
 * Created by user on 1/8/2017.
 */
public class EnergyStorage {

    private double stored, capacity, bandwidth;
    private int tier;

    public EnergyStorage(double stored, double capacity, double bandwidth, int tier) {
        this.stored = stored;
        this.capacity = capacity;
        this.bandwidth = bandwidth;
        this.tier = tier;
    }

    public double getStored() {
        return stored;
    }

    public void setStored(double stored) {
        this.stored = stored;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }


}
