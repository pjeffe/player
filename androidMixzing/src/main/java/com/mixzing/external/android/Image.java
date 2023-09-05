package com.mixzing.external.android;

import org.w3c.dom.Element;

import android.os.Parcel;
import android.os.Parcelable;

import com.mixzing.util.Util;
import com.mixzing.util.XMLDocument;


public class Image implements Parcelable {
	private int width;
	private int height;
	private String url;


	public Image(int width, int height, String url) {
		init(width, height, url);
	}

	private void init(int width, int height, String url) {
		this.width = width;
		this.height = height;
		this.url = url;
	}

	public Image(Element imageElem) {
		int width = -1;
		int height = -1;
		String url = null;
		for (Element elem : XMLDocument.getChildElements(imageElem, null)) {
			String name = elem.getTagName();
			if (name.equals("width")) {
				width = Util.getInt(XMLDocument.getTextContent(elem));
			}
			else if (name.equals("height")) {
				height = Util.getInt(XMLDocument.getTextContent(elem));
			}
			else if (name.equals("url")) {
				url = XMLDocument.getTextContent(elem);
			}
		}
		init(width, height, url);
	}

	public Image(Parcel parcel) {
		width = parcel.readInt();
		height = parcel.readInt();
		url = parcel.readString();
	}

	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(width);
		parcel.writeInt(height);
		parcel.writeString(url);
	}

	public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
		public Image createFromParcel(Parcel source) {
			return new Image(source);
		}

		public Image[] newArray(int size) {
			return new Image[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
