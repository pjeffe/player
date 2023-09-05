package com.mixzing.signature.common.impl;

import java.util.List;

import com.mixzing.signature.common.MixzingAudioInfo;
import com.mixzing.signature.common.MixzingSignatureData;
import com.mixzing.signature.common.MixzingSignatureResponse;
import com.mixzing.signature.common.MixzingSignatureWindow;

public class MixzingSignatureResponseImpl implements MixzingSignatureResponse {

	private MixzingAudioInfo audioInfo;
	private List<MixzingSignatureData> signatures;

	
	public MixzingSignatureResponseImpl(MixzingAudioInfo info, List<MixzingSignatureData> sigs ) {
		this.audioInfo = info;
		signatures = sigs;
	}
	public MixzingAudioInfo getAudioInfo() {
		return audioInfo;
	}

	public List<MixzingSignatureData> getSignatures() {
		return signatures;
	}	
	
	public String toString() {
		String s = "INFO: " + audioInfo.getFrequency() + " Channels: " + audioInfo.getChannels() + "\n";
		for(MixzingSignatureData sd : signatures) {
			s += "SIGNATURE FOR: " + sd.getWindow().getSkip() + " " + sd.getWindow().getDuration() + "\n";
			List<Long> pks = sd.getSignature();
			s += "SIGVAL: ";
			String e =  "\nENERGY: ";
			for(Long pk : pks) {
				s += pk + "|";
			}
			s += e;
			s += "\n\n";
		}
		
		return s;
	}
    public MixzingSignatureData getSignatureForWindow(int skip, int dur, int superWin) {
        for(MixzingSignatureData sig : signatures) {
            if(sig.getWindow().getSkip() == skip && sig.getWindow().getDuration() == dur && sig.getWindow().getSuperWindow() == superWin) {
                return sig;
            }
        }
        return null;
    }
    
    public MixzingSignatureData getSignatureForWindow(MixzingSignatureWindow win) {
        return getSignatureForWindow(win.getSkip(), win.getDuration(), win.getSuperWindow());
    }

}
