package com.mixzing.signature.common.impl;

import java.util.List;

import com.mixzing.signature.common.MixzingPeak;

public class MixzingPeakImpl implements MixzingPeak {

	private double offset;
	private double energy;
	private List<Double> samples;
    private int sampleSize;
    private double centroid;
    private int beginIndex;
    private double zfactor;
    private double sd;
    private double mean;
    
	public int getBeginIndex() {
        return beginIndex;
    }
    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }
    public MixzingPeakImpl(double o, double e) {
		this.energy = e;
		this.offset = o;
	}
	public double getEnergy() {
		return energy;
	}

	public double getOffset() {
		return offset;
	}
	
    public MixzingPeakImpl(double o, double e, int begIndex, List<Double> samples, double zf, double sd, double mean) {
        this.energy = e;
        this.offset = o;
        //this.samples = samples;
        this.sampleSize = samples.size();
        this.beginIndex = begIndex;
        this.zfactor = zf;
        this.sd = sd;
        this.mean = mean;
    }

	public int compareTo(MixzingPeak o) {
		if(this.getOffset() == o.getOffset())
			return 0;
		if(this.getOffset() > o.getOffset())
			return 1;
		return -1;
	}
    public double getCentroid() {
        return centroid;
    }
    public void setCentroid(double centroid) {
        this.centroid = centroid;
    }
    public List<Double> getSamples() {
        return samples;
    }
    public void setSamples(List<Double> samples) {
        this.sampleSize = samples.size();
    }
    
    public String toString() {
        return ((int) this.offset) + ":" + this.energy + ":" + this.beginIndex + ":" + this.sampleSize + ":" + sd + ":" + mean; 
    }

}
