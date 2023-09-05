package com.mixzing.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.mixzing.android.AndroidUtil;
import com.mixzing.android.SdCardHandler;
import com.mixzing.log.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class DeletedPlaylists {
	private static final Logger log = Logger.getRootLogger();
	private SharedPreferences prefs;
	private Editor editor;
	private boolean autoCommit;

	private static final String PREFS = "delpls";


	/**
	 * @param context
	 * @param autoCommit True if the data should be committed after every put, otherwise you need to call commit explicitly
	 */
	public DeletedPlaylists(Context context, boolean autoCommit) {
		this.autoCommit = autoCommit;
		prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}

	/**
	 * Saves the playlist under the given name, replacing any existing one by that name.
	 * 
	 * @param name The playlist name
	 * @param members An ordered list of the pathnames of the playlist members
	 */
	public void put(String name, List<String> members) {
		// add the playlist name to the list for the current volume
		final int cardId = SdCardHandler.getCardId();
		final String cardKey = Integer.toString(cardId);
		List<String> names = getList(cardKey);
		if (names == null || !names.contains(name)) {
			if (names == null) {
				names = new ArrayList<String>(1);
			}
			names.add(name);
			putList(cardKey, names);
		}

		// add or replace the members of the playlist
		final String nameKey = AndroidUtil.getCardSpecificPrefKey(name, cardId);
		putList(nameKey, members);
	}

	/**
	 * Get the names of the deleted playlists for the current volume.
	 * 
	 * @return The list of deleted playlist names or null if none
	 */
	public List<String> getNames() {
		return getList(Integer.toString(SdCardHandler.getCardId()));
	}

	/**
	 * Get the pathames of the members of the given deleted playlist.
	 * 
	 * @param name The name of the playlist
	 * @return The list of pathnames of the playlist's members or null if name isn't a deleted playlist
	 */
	public List<String> getMembers(String name) {
		return getList(AndroidUtil.getCardSpecificPrefKey(name));
	}

	/**
	 * Delete the playlist.
	 * @param name The name of the playlist.
	 */
	public void delete(String name) {
		// delete the playlist name from the list for the current volume
		final int cardId = SdCardHandler.getCardId();
		final String cardKey = Integer.toString(cardId);
		final List<String> names = getList(cardKey);
		if (names.remove(name)) {
			putList(cardKey, names);
		}

		// delete the playlist members entry
		final String nameKey = AndroidUtil.getCardSpecificPrefKey(name, cardId);
		try {
			editor.remove(nameKey);
			if (autoCommit) {
				editor.commit();
			}
		}
		catch (Exception e) {
			log.error("DeletedPlaylist.delete:", e);
		}
	}

	private List<String> getList(String key) {
		// get a JSON-encoded string array
		List<String> list = null;
		try {
			final String pref = prefs.getString(key, null);
			if (pref != null) {
				final JSONArray json = new JSONArray(pref);
				final int len = json.length();
				list = new ArrayList<String>(len);
				for (int i = 0; i < len; ++i) {
					list.add(json.getString(i));
				}
			}
		}
		catch (Exception e) {
			log.error("DeletedPlaylist.getList:", e);
		}
		return list;
	}

	private void putList(String key, List<String> list) {
		// save the list of strings as a JSON array
		try {
			final JSONArray json = new JSONArray(list);
			editor.putString(key, json.toString());
			if (autoCommit) {
				editor.commit();
			}
		}
		catch (Exception e) {
			log.error("DeletedPlaylist.putList:", e);
		}
	}

	/**
	 * Commit any pending changes.
	 */
	public void commit() {
		try {
			editor.commit();
		}
		catch (Exception e) {
			log.error("DeletedPlaylist.commit:", e);
		}
	}
}
