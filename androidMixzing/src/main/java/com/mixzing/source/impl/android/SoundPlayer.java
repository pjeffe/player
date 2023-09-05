package com.mixzing.source.impl.android;


import android.media.MediaPlayer;

import com.mixmoxie.source.player.SourceSoundPlayer;
import com.mixmoxie.source.sourceobject.SourceTrack;
import com.mixzing.log.Logger;

public class SoundPlayer implements SourceSoundPlayer {
	private MediaPlayer mp;
	private static final Logger log = Logger.getRootLogger();
	

	public SoundPlayer() {
		mp = new MediaPlayer();
	}

	public void playFile(String filename) {
		try {
			mp.setDataSource(filename);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			log.error("Error trying to play file " + filename + " " +  e.toString());
		}
	}

	public void playTrack(SourceTrack track) {
		playFile(track.location());
	}

	public void playURL(String location) {
		playFile(location);
	}

	public void stop() {
		mp.stop();
	}

	public PlayerType getPlayerType() {
		return PlayerType.Android;
	}
}
