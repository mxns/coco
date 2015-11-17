package quadratisch.util;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

public class SwingUtil {

	public static JTable createTable(AbstractTableModel pTableModel,
			final String[] pHeaderToolTips) {
		JTable tTable = new JTable(pTableModel) {
			private static final long serialVersionUID = 0L;

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 0L;

					public String getToolTipText(MouseEvent e) {
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();
						return pHeaderToolTips[realIndex];
					}
				};
			}
		};
		return tTable;
	}

	public static JTable createTable(final String[] pHeaderToolTips) {
		JTable tTable = new JTable() {
			private static final long serialVersionUID = 0L;

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 0L;

					public String getToolTipText(MouseEvent e) {
						java.awt.Point p = e.getPoint();
						int index = columnModel.getColumnIndexAtX(p.x);
						int realIndex = columnModel.getColumn(index)
								.getModelIndex();
						return pHeaderToolTips[realIndex];
					}
				};
			}
		};
		return tTable;
	}

}
