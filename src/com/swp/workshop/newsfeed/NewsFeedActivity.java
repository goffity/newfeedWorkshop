package com.swp.workshop.newsfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFeedActivity extends ListActivity implements OnScrollListener {

	ArrayList<FeedItem> feedList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		feedList = getRSSFeed();

		setListAdapter(new myRSSApdater());

		getListView().setOnScrollListener(this);
	}

	private ArrayList<FeedItem> getRSSFeed() {
		try {
			URL url = new URL(
					"http://www.swpark.or.th/component/training/?view=courses&layout=list&format=feed");
			InputStream is = (InputStream) url.getContent();
			SAXParserFactory spf = SAXParserFactory.newInstance();

			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			RSSXmlHandler rssXmlHandler = new RSSXmlHandler();
			xr.setContentHandler(rssXmlHandler);
			// xr.parse(new InputSource(sourceUrl.openStream()));
			InputSource inputSource = new InputSource(is);
			xr.parse(inputSource);
			ArrayList<FeedItem> feedItemList = rssXmlHandler.getFeedItemList();
			return feedItemList;
		} catch (MalformedURLException e) {
			Log.e("RSSFeed", "ERROR", e);
			return null;
		} catch (IOException e) {
			Log.e("RSSFeed", "ERROR", e);
			return null;
		} catch (ParserConfigurationException e) {
			Log.e("RSSFeed", "ERROR", e);
			return null;
		} catch (SAXException e) {
			Log.e("RSSFeed", "ERROR", e);
			return null;
		}
	}

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	class myRSSApdater extends BaseAdapter {

		LayoutInflater inflater;

		public myRSSApdater() {
			super();
			inflater = getLayoutInflater();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return feedList.size() + 1;
			// return 10;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return feedList.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TextView textView = new TextView(getApplicationContext());
			// textView.setText((String) getItem(arg0));

			// View view = inflater.inflate(R.layout.feed_row, null);
			//
			// TextView tvTitle = (TextView) findViewById(R.id.tv_title);
			// TextView tvDate = (TextView) findViewById(R.id.tv_date);
			//
			// FeedItem feedItem = (FeedItem) getItem(arg0);
			//
			// if (feedItem != null) {
			// if (tvTitle == null) {
			// Log.d("newsFeed", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			// }
			// tvTitle.setText(feedItem.getTitle());
			// if (feedItem.getPubDate() != null) {
			// tvDate.setText(feedItem.getPubDate().toString());
			// }
			// }

			// View view = inflater.inflate(R.layout.feed_row, null);
			// TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
			// TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
			// FeedItem feedItem = (FeedItem) getItem(arg0);
			// tvTitle.setText(feedItem.getTitle());
			// tvDate.setText(feedItem.getPubDate().toString());
			// return view;

			if (arg0 == feedList.size()) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("Loading");
				return tv;
			}
			if (convertView instanceof TextView) {
				convertView = null;
			}
			View row = null;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				row = inflater.inflate(R.layout.feed_row, null);
				viewHolder = new ViewHolder();
				viewHolder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
				viewHolder.tvDate = (TextView) row.findViewById(R.id.tv_date);
				row.setTag(viewHolder);
			} else {
				row = convertView;
				viewHolder = (ViewHolder) row.getTag();
			}
			FeedItem feedItem = (FeedItem) getItem(arg0);

			viewHolder.tvTitle.setText(feedItem.getTitle());
			if (feedItem.getPubDate() == null) {
				viewHolder.tvDate.setText("N/A");
			} else {
				String dateString = dateFormat.format(feedItem.getPubDate());
				viewHolder.tvDate.setText(dateString);
			}
			if (arg0 % 2 == 0) {
				row.setBackgroundColor(Color.GRAY);
			} else {
				row.setBackgroundColor(Color.DKGRAY);
			}

			return row;

			// return view;
		}
	}

	class ViewHolder {
		public TextView tvTitle;
		public TextView tvDate;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FeedItem feedItem = (FeedItem) l.getAdapter().getItem(position);
		String url = feedItem.getLink();
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1) {
				ArrayList<FeedItem> newFeedItemList = getRSSFeed();
				feedList.addAll(newFeedItemList);
				((BaseAdapter) view.getAdapter()).notifyDataSetChanged();
			}
		}
	}

}