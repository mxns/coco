package valpen.util;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

public class PrefsUtil {

	public static void saveTable(Class<?> pClass, final JTable pTable,
			String pTableId) {
		final Preferences prefs = Preferences.userNodeForPackage(pClass);
		final String colOrdPrefId = pTableId + ".cprefs";
		final StringBuffer colOrd = new StringBuffer();
		TableColumn[] result = new TableColumn[pTable.getColumnCount()];
		Enumeration<TableColumn> cols = pTable.getColumnModel().getColumns();
		for (int i = 0; cols.hasMoreElements(); i++) {
			result[i] = cols.nextElement();
		}
		for (int c = 0; c < result.length; c++) {
			result[c] = pTable.getColumnModel().getColumn(c);
		}
		for (int i = 0; i < result.length; i++) {
			TableColumn c = result[i];
			colOrd.append(c.getModelIndex()).append(",");
			colOrd.append(i).append(",");
			colOrd.append(c.getPreferredWidth()).append(";");
		}
		prefs.put(colOrdPrefId, colOrd.toString());
	}

	public static void restoreTable(Class<?> pClass, final JTable pTable,
			String pTableId) {
		Preferences prefs = Preferences.userNodeForPackage(pClass);
		String prefId = pTableId + ".cprefs";
		String prefDef = null;
		String prefVal = prefs.get(prefId, prefDef);
		if (prefVal == null) {
			return;
		}
		String[] prefsStr = prefVal.split(";");
		final List<TableColumnPref> list = new ArrayList<TableColumnPref>();
		for (String pref : prefsStr) {
			try {
				String[] s = pref.split(",");
				if (s.length != 3) {
					continue;
				}
				final int modelIndex = Integer.parseInt(s[0]);
				final int viewIndex = Integer.parseInt(s[1]);
				final int width = Integer.parseInt(s[2]);
				list.add(new TableColumnPref(modelIndex, viewIndex, width));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Collections.sort(list, new Comparator<TableColumnPref>() {

			@Override
			public int compare(TableColumnPref arg0, TableColumnPref arg1) {
				if (arg0.mViewIndex < arg1.mViewIndex) {
					return -1;
				} else if (arg0.mViewIndex == arg1.mViewIndex) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Enumeration<TableColumn> e = pTable.getColumnModel()
						.getColumns();
				Map<Integer, TableColumn> colMap = new HashMap<Integer, TableColumn>();
				List<TableColumn> colList = new ArrayList<TableColumn>();
				while (e.hasMoreElements()) {
					TableColumn c = e.nextElement();
					colList.add(c);
					colMap.put(c.getModelIndex(), c);
				}
				for (TableColumn c : colList) {
					pTable.getColumnModel().removeColumn(c);
				}
				for (TableColumnPref pref : list) {
					TableColumn c = colMap.get(pref.mModelIndex);
					if (c == null) {
						continue;
					}
					pTable.getColumnModel().addColumn(c);
					colList.remove(c);
					c.setPreferredWidth(pref.mWidth);
				}
				for (TableColumn c : colList) {
					pTable.getColumnModel().addColumn(c);
				}
			}
		});
	}

	private static class TableColumnPref {
		int mModelIndex;
		int mViewIndex;
		int mWidth;

		TableColumnPref(int pModelIndex, int pViewIndex, int pWidth) {
			mModelIndex = pModelIndex;
			mViewIndex = pViewIndex;
			mWidth = pWidth;
		}
	}

	public static void saveFrame(Class<?> pClass, JFrame pFrame, String pFrameId) {
		Preferences prefs = Preferences.userNodeForPackage(pClass);
		final String locPrefId = pFrameId + ".location";
		final String szPrefId = pFrameId + ".size";
		String location = pFrame.getLocation().x + ":" + pFrame.getLocation().y;
		String size = pFrame.getSize().width + ":" + pFrame.getSize().height;
		prefs.put(locPrefId, location);
		prefs.put(szPrefId, size);
	}

	public static void restoreFrame(Class<?> pClass, final JFrame pFrame,
			String pFrameId) {
		Preferences prefs = Preferences.userNodeForPackage(pClass);
		final String locPrefId = pFrameId + ".location";
		final String szPrefId = pFrameId + ".size";
		String defLoc = "0:0";
		String defSz = "500:500";
		String[] locStr = prefs.get(locPrefId, defLoc).split(":");
		String[] szStr = prefs.get(szPrefId, defSz).split(":");
		final int szWidth = Integer.parseInt(szStr[0]);
		final int szHeight = Integer.parseInt(szStr[1]);
		final int locX = Integer.parseInt(locStr[0]);
		final int locY = Integer.parseInt(locStr[1]);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				pFrame.setLocation(new Point(locX, locY));
				pFrame.setSize(new Dimension(szWidth, szHeight));
			}
		});
	}

}
