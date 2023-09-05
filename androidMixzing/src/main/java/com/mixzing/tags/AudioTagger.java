package com.mixzing.tags;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import com.mixzing.data.CoreMetaData;


public class AudioTagger {
	protected static boolean isLoaded = false;

	private static final String[] supportedTypes = new String[] {
			"mp3",
			"ogg",
			"flac",
			"m4a",
			"m4b",
			"m4p",
			"mp4",
			"3g2",
			"wma",
			"asf",
			"wv",
			"wav",
			"ape"
	};

	private static final HashSet<String> supported = new HashSet<String>(Arrays.asList(supportedTypes));


	private static native int readTags(String fileName, TagObject tags);

	private static native boolean setTags(String fileName, String title, String artist, String album, String comment, String genre,
			String year, String trackNum);


	protected static void ensureLoaded() {
		synchronized (AudioTagger.class) {
			if (!isLoaded) {
				System.loadLibrary("taglib");
				isLoaded = true;
			}
		}
	}

	public static CoreMetaData readTags(String fileName) {
		CoreMetaData meta = null;
		ensureLoaded();
		TagObject tags = new TagObject();
		if (readTags(fileName, tags) != 0) {
			meta = new CoreMetaData(tags.artist, tags.album, tags.title, tags.genre, 0, tags.year, tags.track, 0, 0, 0, tags.comment);
		}
		return meta;
	}

	public static boolean updateTags(String fileName, String title, String artist, String album, String comment,
			String genre, int yearI, int trackNumI) {

		ensureLoaded();

		title = (title == null) ? "" : title;
		artist = (artist == null) ? "" : artist;
		album = (album == null) ? "" : album;
		comment = (comment == null) ? "" : comment;
		genre = (genre == null) ? "" : genre;
		yearI = (yearI < 0) ? 0 : yearI;
		trackNumI = (trackNumI < 0) ? 0 : trackNumI;
		String year = Integer.toString(yearI);
		String trackNum = Integer.toString(trackNumI);
		
		return setTags(fileName, title, artist, album, comment, genre, year, trackNum);
	}

	public static boolean updateTags(String filename, CoreMetaData meta) {
		return updateTags(filename, meta.getTitle(), meta.getArtist(), meta.getAlbum(), meta.getComment(), meta.getGenre(),
				meta.getYear(), meta.getTrackNum());
	}

	public static boolean isSupported(String fileName) {
		boolean ret = false;
		final int pos = fileName.lastIndexOf('.');
		if (pos >= 0) {
			final String ext = fileName.substring(pos + 1).toLowerCase(Locale.US);
			ret = supported.contains(ext);
		}
		return ret;
	}
}
