package com.data;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetTable extends AbstractTableModel {

    private final List<String> columnNames = new ArrayList<>();
    private final List<List<Object>> data = new ArrayList<>();

    public ResultSetTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // Track which columns to include
        List<Integer> includedColumns = new ArrayList<>();

        // Load column names
        for (int i = 1; i <= columnCount; i++) {
            String colName = meta.getColumnName(i);
            if (!colName.equalsIgnoreCase("pass_hash")) {
                columnNames.add(colName);
                includedColumns.add(i);
            }
        }

        // Load all rows
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }
}
