package com.example.gameq_android.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	
	public static void addItem(String id, String deviceName, String statusText, int status) {
		ITEMS.clear();
		DummyItem item = new DummyItem("id", deviceName, statusText, status);
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String deviceName;
		public String statusText;
		public int status;

		public DummyItem(String id, String deviceName, String statusText, int status) {
			this.id = id;
			this.status = status;
			this.statusText = statusText;
			this.deviceName = deviceName;
		}

		@Override
		public String toString() {
			return deviceName + statusText;
		}
	}
}
