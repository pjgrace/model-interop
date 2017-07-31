/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2015
//
// Copyright in this library belongs to the University of Southampton
// University Road, Highfield, Southampton, UK, SO17 1BJ
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
// Created By : Paul Grace
// Created for Project : XIFI (http://www.fi-xifi.eu)
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////


package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms;

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Function.FunctionType;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.Guard;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.GuardData;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;

/**
 * The Guard transition attribute table is the list of rules that
 * are currently attached to the transition.
 * Hence, the table consists of rows of guards. The columns relate to
 * the three data elements of a guard:
 * 1) The function to evaluate (equals, notequals, contains, ...)
 * 2) The parameter name
 * 3) The value that the parameter guard must match
 *
 * The table is displayed as part of the GuardForm object.
 * @author pjg
 */
public class GuardTransitionAttributeTable extends AbstractTableModel {

    /**
     * The table uses a comboBox to input the function.
     */
    private final transient JComboBox comboBox = new JComboBox();

    /**
     * Get the reference to the combobox of the guard inputs.
     * @return The combobox inputs.
     */
    public final JComboBox getGuardCombo() {
        return comboBox;
    }

    /**
     * The table column header names.
     */
    private final transient String[] columnNames = {"Parameter", "Function", "Value"};

    /**
     * The displayed data rows.
     */
    private transient List<GuardData> data = new ArrayList();

    /**
     * Create a new table of guards.
     */
    public GuardTransitionAttributeTable() {
        super();
        // Initialise the input values for the combo box
        comboBox.setModel(new DefaultComboBoxModel(FunctionType.values()));
    }

    @Override
    public final int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public final int getRowCount() {
        return data.size();
    }

    @Override
    public final String getColumnName(final int col) {
        return columnNames[col];
    }

    @Override
    public final Object getValueAt(final int rowVal, final int colVal) {
        final GuardData row = data.get(rowVal);
        switch (colVal) {
            case 0: return row.getGuardData();
            case 1: return row.getFuntionType();
            case 2: return row.getGuardValue();
            default:
                return null;
        }
    }

    @Override
    public final Class getColumnClass(final int colVal) {
        return getValueAt(0, colVal).getClass();
    }


    @Override
    public final boolean isCellEditable(final int row, final int col) {
       return true;
    }


    @Override
    public final void setValueAt(final Object value, int rowVal, final int colVal) {
        if (rowVal >= data.size()){
            rowVal = data.size() - 1;
        }
        final GuardData row = data.get(rowVal);
        switch (colVal) {
            case 0: row.setGuardData((String) value);
                break;
            case 1: row.setFunctionType((FunctionType) value);
                break;
            case 2: row.setGuardValue((String) value);
        }
        fireTableCellUpdated(rowVal, colVal);
    }

    /**
     * Clear the table of data i.e. clear the view.
     */
    public final void clearData() {
        data.clear();
        data = new ArrayList();
        fireTableDataChanged();
    }

    /**
     * Add a new guard to the table view.
     * @param newrow The new guard to display.
     */
    public final void addRowData(final GuardData newrow) {
        data.add(newrow);
        fireTableDataChanged();
    }

    /**
     * Remove a guard from the table view.
     * @param row The guard to remove.
     */
    public final void removeRowData(final int row) {
        data.remove(row);
        fireTableRowsDeleted(row,row);
    }

    /**
     * Reset the table with a new set of guard data i.e. refresh the table
     * view of a given data set.
     * @param nGuard The data set to display in the table.
     */
    public final void setData(final Guard nGuard) {
        for (GuardData gd : nGuard.getData()) {
            addRowData(gd);
        }
    }
}