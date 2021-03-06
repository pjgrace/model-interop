/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2017
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
// Created By : Nikolay Stanchev
//
/////////////////////////////////////////////////////////////////////////
//
//  License : GNU Lesser General Public License, version 3
//
/////////////////////////////////////////////////////////////////////////

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.RESTComponent;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.ObjectDeepCloner;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BasicGraphEditor;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.BehaviourGraphComponent;

/**
 * A panel to display the behaviour and the system graph when running a test
 *
 * Project acknowledgements - developed in FIESTA (http://www.fiesta-iot.eu)
 *
 * @author Nikolay Stanchev
 */
public class ExecutionPanel extends JPanel {

    /**
     * a reference to the editor
     */
    private final transient BasicGraphEditor editor;

    /**
     * a reference to the behaviour component
     */
    private transient mxGraphComponent behaviourComponent;

    /**
     * a reference to the system component
     */
    private transient mxGraphComponent systemComponent;

    /**
     * reference to the panel containing the two graphs
     */
    private transient final JPanel graphs;

    /**
     * reference to the table with the interfaces and their respective ports
     */
    private transient final JTable portsTable;

    /**
     * constructor for the panel, initialises the GUI components
     * @param editor the editor reference
     */
    public ExecutionPanel(BasicGraphEditor editor){
        super(new BorderLayout());

        this.editor = editor;

        graphs = new JPanel(new GridLayout(1, 1));
        graphs.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        behaviourComponent = new BehaviourGraphComponent(editor.getBehaviourGraph().getGraph());
        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));

        graphs.add(behaviourComponent);

//        systemComponent = new SystemGraphComponent(editor.getSystemGraph().getGraph());
//        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
//        systemComponent.setEnabled(false);
//        graphs.add(systemComponent);

        // Creates a split pane to label the graph in the testing panel
        final JLabel gLabel = new JLabel(" Execution View");
        final Font font = gLabel.getFont();
        final Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
        gLabel.setFont(boldFont);
        final JSplitPane outer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gLabel, graphs);
        outer.setDividerSize(0);
        add(outer, BorderLayout.CENTER);

        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Interface:", "Proxy running on port:"};
        Object[][] data = {};
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        portsTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }

            @Override
            public String getToolTipText(MouseEvent e){
                String tooltip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tooltip = getValueAt(rowIndex, colIndex).toString();
                } catch (Exception ex) {
                    // no tool tip in case of exception
                }

                return tooltip;
            }
        };
        portsTable.setPreferredScrollableViewportSize(new Dimension(portsTable.getPreferredSize().width, portsTable.getRowHeight()*8));
        portsTable.setFillsViewportHeight(true);

        // add a KeyListener to the table, which will copy the original address but with the proxy port
        portsTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "Copy");
        portsTable.getActionMap().put("Copy", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                int rowIndex = portsTable.getSelectedRow();

                try {
                    // replace the port of the interface url with the port url
                    String urlStr = portsTable.getValueAt(rowIndex, 0).toString();
                    String proxyPort = (String) portsTable.getValueAt(rowIndex, 1);
                    URL url = new URL(urlStr);
                    url = new URL(url.getProtocol(), url.getHost(), Integer.parseInt(proxyPort), url.getFile());

                    // copy the url
                    StringSelection stringSel = new StringSelection(url.toString());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSel, null);

                    // display a message for successcul copy of the url
                    JOptionPane.showMessageDialog(graphs, "Successfully copied the url of the interface. "
                            + "Keep in mind that the port number is the port of the proxy for this interface.\n" + url.toString(),
                            "Successful copy", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    // no copy if an exception is thrown
                }
            }
        });
        tablePanel.add(new JScrollPane(portsTable), BorderLayout.CENTER);

        add(tablePanel, BorderLayout.SOUTH);
    }

    /**
     * called when starting a new test, refreshes the graph components and the table of interfaces
     * @param restComponents a map linking component IDs to rest component objects
     */
    public void refreshGraph(Map<String, RESTComponent> restComponents){
        mxGraph behaviourGraph = new mxGraph((mxIGraphModel) ObjectDeepCloner.deepCopy(editor.getBehaviourGraph().getGraph().getModel()),
                editor.getBehaviourGraph().getGraph().getStylesheet());
//        mxGraph systemGraph = new mxGraph((mxIGraphModel) ObjectDeepCloner.deepCopy(editor.getSystemGraph().getGraph().getModel()),
//                editor.getSystemGraph().getGraph().getStylesheet());
        behaviourComponent = new BehaviourGraphComponent(behaviourGraph);
//        systemComponent = new SystemGraphComponent(systemGraph);

        graphs.removeAll();

        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);

//        systemComponent.setEnabled(false);
//        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
//        graphs.add(systemComponent);

        graphs.revalidate();
        graphs.repaint();

        List<List<String>> data = new ArrayList<>();
        restComponents.values().forEach((component) -> {
            component.getInterfaces().forEach((restInterface) -> {
                data.add(new ArrayList<>(Arrays.asList(restInterface.getURL(), Integer.toString(restInterface.getPort()))));
            });
        });

        refreshTable(data);
    }

    /**
     * refresh the table of interfaces and port numbers
     * @param data the new data to insert into the table
     */
    private void refreshTable(List<List<String>> data){
        refreshTable();
        data.forEach((row) -> {
            ((DefaultTableModel) portsTable.getModel()).addRow(row.toArray());
        });
    }

    /**
     * refresh the table without inserting new data into it
     */
    private void refreshTable(){
        DefaultTableModel model = (DefaultTableModel) portsTable.getModel();
        model.setRowCount(0);
    }

    /**
     * resets the Execution panel, by reseting the graph components
     */
    public void resetGraph(){
        behaviourComponent = new BehaviourGraphComponent(editor.getBehaviourGraph().getGraph());
//        systemComponent = new SystemGraphComponent(editor.getSystemGraph().getGraph());

        graphs.removeAll();

        behaviourComponent.setEnabled(false);
        behaviourComponent.setBorder(BorderFactory.createLineBorder(new Color(128, 0, 0)));
        graphs.add(behaviourComponent);

//        systemComponent.setEnabled(false);
//        systemComponent.setBorder(BorderFactory.createLineBorder(new Color(0, 66, 128)));
//        graphs.add(systemComponent);

        graphs.revalidate();
        graphs.repaint();

        // reset the table
        refreshTable();
    }

    /**
     * a method to set the current test state by changing the selection in the graph
     * @param labelID the labelID of the current state
     */
    public void setTestState(String labelID){
        mxGraph graph = behaviourComponent.getGraph();
        String guiID = editor.getDataModel().getNodeByLabel(labelID).getUIIdentifier();
        mxCell toSelect = (mxCell) ((mxGraphModel) graph.getModel()).getCell(guiID);
        graph.getSelectionModel().setCell(toSelect);
    }
}
