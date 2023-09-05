package com.mixzing.signature.common.impl;

import com.mixzing.signature.common.MixzingAudioInfo;

public class MixzingAudioInfoImpl implements MixzingAudioInfo {

	    public MixzingAudioInfoImpl() {
	    	
	    }

		protected int channels, frequency, bitRate;
		protected float msPerframe;

		public int getBitRate() {
			return bitRate;
		}

		public void setBitRate(int bitRate) {
			this.bitRate = bitRate;
		}

		public int getChannels() {
			return channels;
		}

		public void setChannels(int channels) {
			this.channels = channels;
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(int frequency) {
			this.frequency = frequency;
		}

		public float getMsPerframe() {
			return msPerframe;
		}

		public void setMsPerframe(float msPerframe) {
			this.msPerframe = msPerframe;
		}	
		
		public String toString() {
			return "Channels: " + channels + " Freq: " + frequency + " BitRate: " + bitRate + " msPerframe: " + msPerframe ;
		}

}
