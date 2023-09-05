package com.mixzing.external.android;

import org.w3c.dom.Element;

import com.mixzing.util.Util;
import com.mixzing.util.XMLDocument;


public class MPXInfo {
	public String artist;
	public String album;
	public String title;
	public String genre;
	public int year;
	public long duration;
	public int track_number;

	public MPXInfo(Element root) {
		for (Element elem : XMLDocument.getChildElements(root, null)) {
			String elname = elem.getTagName();
			if (elname.equals("artist")) {
				artist = XMLDocument.getCdataContent(elem);
			}
			else if (elname.equals("album")) {
				album = XMLDocument.getCdataContent(elem);
			}
			else if (elname.equals("title")) {
				title = XMLDocument.getCdataContent(elem);
			}
			else if (elname.equals("genre")) {
				genre = XMLDocument.getCdataContent(elem);
			}
			else if (elname.equals("year")) {
				year = Util.getInt(XMLDocument.getTextContent(elem));
			}
			else if (elname.equals("duration")) {
				duration = Util.getLong(XMLDocument.getTextContent(elem));
			}
			else if (elname.equals("track_number")) {
				track_number = Util.getInt(XMLDocument.getTextContent(elem));
			}
		}
	}
}
