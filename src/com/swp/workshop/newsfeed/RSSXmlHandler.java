package com.swp.workshop.newsfeed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class RSSXmlHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue;
	ArrayList<FeedItem> feedItemList = new ArrayList<FeedItem>();
	FeedItem currentFeedItem;

	SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		currentElement = true;
		if (localName.equals("item")) {
			currentFeedItem = new FeedItem();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		currentElement = false;
		if (currentFeedItem != null) {
			if (localName.equals("title")) {
				currentFeedItem.setTitle(currentValue);
			} else if (localName.equals("link")) {
				currentFeedItem.setLink(currentValue);
			} else if (localName.equals("pubDate")) {
				String dateString = currentValue;
				try {
					Date date = dateFormat.parse(dateString);
					currentFeedItem.setPubDate(date);
				} catch (ParseException e) {
					Log.e("RSSFeed", "ERROR", e);
				}
			} else if (localName.equals("item")) {
				feedItemList.add(currentFeedItem);
			}
		}
		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}
	}

	public ArrayList<FeedItem> getFeedItemList() {
		return feedItemList;
	}
}
