package com.mixzing.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.net.Uri;

import com.mixzing.log.Logger;
import com.mixzing.util.Web.Response;

public class XMLDocument {
	private static Logger log = Logger.getRootLogger();

	public static Document getDocument(InputStream is) {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setExpandEntityReferences(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(is);
		}
		catch (Exception e) {
			log.warn("XMLDocument.getDocument: ", e);
		}
		return doc;
	}

	public static Document getDocument(String uri, int timeout, int retries) {
		Document doc = null;
		InputStream is = null;
		try {
			Response resp = Web.getWebContent(Uri.parse(uri), timeout, retries);
			if (resp.status == HttpStatus.SC_OK) {
				is = new GZIPInputStream(new ByteArrayInputStream(resp.content));
				doc = getDocument(is);
			}
			else {
				if (Logger.IS_DEBUG_ENABLED)
					log.debug(String.format("XMLDocument.getDocument: status %d getting '%s'", resp.status, uri));
			}
		}
		catch (Exception e) {
			if (Logger.IS_DEBUG_ENABLED)
				log.debug(String.format("XMLDocument.getDocument: error getting '%s': ", uri), e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					log.error("XMLDocument.getDocument: error closing stream: ", e);
				}
			}
		}
		return doc;
	}

	// get all first-level child elements, restricted to those with the given name if non-null
	public static ArrayList<Element> getChildElements(Node parent, String name) {
		ArrayList<Element> elems = new ArrayList<Element>();
		NodeList nl = parent.getChildNodes();
		int num = nl.getLength();
		for (int i = 0; i < num; ++i) {
			Node child = nl.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && (name == null || child.getNodeName().equals(name))) {
				elems.add((Element)child);
			}
		}
		return elems;
	}

	// get the first child element with the given name
	public static Element getChildElement(Node parent, String name) {
		Element elem = null;
		NodeList nl = parent.getChildNodes();
		int num = nl.getLength();
		for (int i = 0; i < num; ++i) {
			Node child = nl.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals(name)) {
				elem = (Element)child;
			}
		}
		return elem;
	}

	public static String getContent(Element elem, short nodeType) {
		StringBuilder sb = new StringBuilder();
		NodeList nl = elem.getChildNodes();
		if (nl != null) {
			int num = nl.getLength();
			for (int i = 0; i < num; ++i) {
				Node child = nl.item(i);
				if (child.getNodeType() == nodeType) {
					sb.append(child.getNodeValue());
				}
			}
		}
		return sb.toString();
	}

	public static String getTextContent(Element elem) {
		return getContent(elem, Node.TEXT_NODE);
	}

	public static String getCdataContent(Element elem) {
		return getContent(elem, Node.CDATA_SECTION_NODE);
	}

//	private void printNodes(NodeList nodes, String prefix) {
//		if (nodes != null) {
//			int len = nodes.getLength();
//			for (int i = 0; i < len; ++i) {
//				Node node = nodes.item(i);
//				printNode(node, prefix);
//			}
//		}
//	}
//
//	private void printNode(Node node, String prefix) {
//		short type = node.getNodeType();
//		log.debug(String.format("%snode type = %s, value = %s", prefix, node.getNodeName(), node.getNodeValue()));
//		if (type == Node.ELEMENT_NODE) {
//			NamedNodeMap attrs = node.getAttributes();
//			int alen = attrs.getLength();
//			for (int j = 0; j < alen; ++j) {
//				Node anode = attrs.item(j);
//				printNode(anode, prefix + "  ");
//			}
//		}
//		printNodes(node.getChildNodes(), prefix + "  ");
//	}
}
