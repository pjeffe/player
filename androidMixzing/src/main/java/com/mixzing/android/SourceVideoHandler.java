package com.mixzing.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;

import com.mixzing.log.Logger;

public class SourceVideoHandler {
	protected static Logger lgr = Logger.getRootLogger();
	protected Context con;

	public SourceVideoHandler(Context cont) {
		con = cont;
	}

	public List<SourceVideoImpl> getVideos() {
		final ArrayList<SourceVideoImpl> videos = new ArrayList<SourceVideoImpl>();
		final Cursor cur = con.getContentResolver().query(Media.getContentUri(SdCardHandler.getVolume()),
			null, null, null, null);

		if (cur != null) {
			try {
				final int albumIdx = cur.getColumnIndex(Media.ALBUM);
				final int artistIdx = cur.getColumnIndex(Media.ARTIST);
				final int titleIdx = cur.getColumnIndex(Media.TITLE);
				final int catIdx = cur.getColumnIndex(Media.CATEGORY);
				final int addIdx = cur.getColumnIndex(Media.DATE_ADDED);
				final int modIdx = cur.getColumnIndex(Media.DATE_MODIFIED);
				final int takenIdx = cur.getColumnIndex(Media.DATE_TAKEN);
				final int descIdx = cur.getColumnIndex(Media.DESCRIPTION);
				final int durIdx = cur.getColumnIndex(Media.DURATION);
				final int langIdx = cur.getColumnIndex(Media.LANGUAGE);
				final int latIdx = cur.getColumnIndex(Media.LATITUDE);
				final int longIdx = cur.getColumnIndex(Media.LONGITUDE);
				final int locIdx = cur.getColumnIndex(Media.DATA);
				final int mimeIdx = cur.getColumnIndex(Media.MIME_TYPE);
				final int resIdx = cur.getColumnIndex(Media.RESOLUTION);
				final int sizeIdx = cur.getColumnIndex(Media.SIZE);
				final int tagsIdx = cur.getColumnIndex(Media.TAGS);
	
				while (cur.moveToNext()) {
					final SourceVideoImpl src = new SourceVideoImpl();
	
					try {
						src.setAlbum(cur.getString(albumIdx));
						src.setArtist(cur.getString(artistIdx));
						src.setTitle(cur.getString(titleIdx));
						src.setCategory(cur.getString(catIdx));
						src.setDateAdded(cur.getLong(addIdx));
						src.setDateModified(cur.getLong(modIdx));
						src.setDateTaken(cur.getLong(takenIdx));
						src.setDescription(cur.getString(descIdx));
						src.setDuration(cur.getInt(durIdx));
						src.setLanguage(cur.getString(langIdx));
						src.setLatitude(cur.getFloat(latIdx));
						src.setLongitude(cur.getFloat(longIdx));
						src.setLocation(cur.getString(locIdx));
						src.setMimeType(cur.getString(mimeIdx));
						src.setResolution(cur.getString(resIdx));
						src.setSize(cur.getInt(sizeIdx));
						src.setTags(cur.getString(tagsIdx));

						videos.add(src);
					}
					catch (Exception e) {
					}
				}
			}
			catch (Exception e) {
			}
			finally {
				cur.close();
			}
		}

		return videos;
	}
}
