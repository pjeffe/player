package com.mixzing.util;

import java.util.ArrayList;

import android.os.Bundle;

/**
 * A class simpler than the WebBackForwardHistory that we can use to manage web view histories ourselves. Note
 * that the built in history has a fatal flaw that it cannot be set explicitly, and therefore the webview cannot be shared
 * across different view perspectives without becoming confused (i.e. google results mixed up with wikipedia results. This only
 * supports moving back, which is all we ever want to do with it in the simple web view.
 * @author guy
 *
 */
public class UrlHistory extends Object {
	public class UrlHistoryItem {
		public String url;
		public String content;
	}
	private String name;
	private ArrayList<UrlHistoryItem> list = new ArrayList<UrlHistoryItem>();
	private static final String URL_KEY = "UrlHistory-";
	private static final String CONTENT_KEY = "ContentHistory-";
	
	public UrlHistory(String name) {
		this.name = name;
	}
	
	public void clearHistory() {
		list.clear();
	}
	
	public synchronized UrlHistoryItem goBack () {
		UrlHistoryItem rval = null;
		final int index = list.size() - 1;
		if (index > 0) {
			rval = list.get(index - 1);
		}
		if (index > -1) {
			list.remove(index);
		}
		return rval;
	}
	
	public synchronized UrlHistoryItem getCurrent() {
		final int index = list.size() - 1;
		if (index >= 0)
			return  list.get(index);
		return null;
	}
	
	public synchronized void addOrUpdate (String url, String content) {
		UrlHistoryItem current = getCurrent();
		if (current == null || !current.url.equals(url)) {
			UrlHistoryItem item = new UrlHistoryItem();
			item.url = url;
			item.content = content;
			list.add(item);
		} else {
			current.content = content;
		}
	}
	
	public boolean canGoBack() {
		return list.size() > 1;
	}
	
	public void saveToBundle (Bundle b) {
		if (list.size() == 0)
			return;
		ArrayList<String> urlList = new ArrayList<String>(list.size());
		ArrayList<String> contentList = new ArrayList<String>(list.size());
		for (UrlHistoryItem item : list) {
			urlList.add(item.url);
			contentList.add(item.content);
		}
		b.putStringArrayList(URL_KEY + name, urlList);
		b.putStringArrayList(CONTENT_KEY + name, contentList);
	}
	
	public void restoreFromBundle (Bundle b) {
		final String urlKey = URL_KEY + name;
		final String contentKey = CONTENT_KEY + name;
		if (b.containsKey(urlKey) && b.containsKey(contentKey)) {
			ArrayList<String> urlList = b.getStringArrayList(urlKey);
			ArrayList<String> contentList = b.getStringArrayList(contentKey);
			final int size = urlList.size();
			if (contentList.size() == size) {
				list = new ArrayList<UrlHistoryItem>(urlList.size()); 
				for (int index = 0; index < size; index++) {
					addOrUpdate(urlList.get(index), contentList.get(index));
				}
			}
		}
	}
}
