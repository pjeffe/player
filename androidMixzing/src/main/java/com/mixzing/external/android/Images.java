package com.mixzing.external.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mixzing.log.Logger;

import android.os.Parcel;

public class Images {
	private static final Logger log = Logger.getRootLogger();
	public Image smallImage;
	public Image largeImage;

	public Images(Image smallImage, Image largeImage) {
		this.smallImage = smallImage;
		this.largeImage = largeImage;
	}

	public Images(Parcel parcel) {
		smallImage = new Image(parcel);
		largeImage = new Image(parcel);
	}

	public void writeToParcel(Parcel parcel, int flags) {
		smallImage.writeToParcel(parcel, flags);
		largeImage.writeToParcel(parcel, flags);
	}

	public Image getSmallImage() {
		return smallImage;
	}

	public Image getLargeImage() {
		return largeImage;
	}

	public String getSmallURL() {
		return smallImage == null ? null : smallImage.getUrl();
	}

	public String getLargeURL() {
		return largeImage == null ? null : largeImage.getUrl();
	}

	// returns the smallest and largest of the images in the given array
	//
	public static Images parseImages(JSONArray json) {
		Images images = null;
		if (json != null) {
			try {
				final int len = json.length();
				if (len != 0) {
					final Image smallImage;
					final Image largeImage;
					if (len == 1) {
						final JSONObject image = json.getJSONObject(0);
						final String url = image.getString("url");
						final int width = image.getInt("size");
						smallImage = largeImage = new Image(width, width, url);
					}
					else {
						int min = Integer.MAX_VALUE;
						int max = -1;
						String small = null;
						String large = null;
						for (int i = 0; i < len; ++i) {
							final JSONObject image = json.getJSONObject(i);
							final int size = image.getInt("size");
							final String url = image.getString("url");
							if (size == 0) {
								if (small == null) {
									small = url;
									min = 0;
								}
								else {
									large = url;
									max = 0;
								}
							}
							else {
								if (size < min) {
									min = size;
									small = url;
								}
								if (size > max) {
									max = size;
									large = url;
								}
							}
						}
						smallImage = new Image(min, min, small);
						largeImage = new Image(max, max, large);
					}
					images = new Images(smallImage, largeImage);
				}
			}
			catch (JSONException e) {
				log.error("Images.parseImages: malformed json: " + json + ":", e);
			}
			catch (Exception e) {
				log.error("Images.parseImages: " + json + ":", e);
			}
		}
		return images;
	}
}