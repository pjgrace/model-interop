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

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor;

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.ComponentForm;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.EmptyForm;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.GuardForm;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.MessageForm;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.NodeForm;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTable;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.data.forms.EndForm;

/**
 * The Attribute panel where different panels are switched between to input
 * and view attributes attached to different elements of the pattern.
 * @author pjg
 */
public class AttributePanel {

    /**
     * There are five types of panels:
     * 1) A guard table - for a guard transition
     * 2) A message form - for creating message content
     * 3) A node table - for data constants associated with a node
     * 4) A Component table - proxy property generation
     * 5) A client table - data about a client in the application.
     */
    private final transient MessageForm mForm;

    /**
     * Get the message component element. This is the form in the left
     * hand panel for entering message details.
     * @return The reference to the UI component.
     */
    public final MessageForm getMessageComponent() {
        return mForm;
    }

    /**
     * Reference to the Component Form UI component.
     */
    private final transient ComponentForm cForm;

    /**
     * Get the Component component element. This is the form in the left
     * hand panel for entering proxy component details.
     * @return The reference to the UI component.
     */
    public final ComponentForm getComponentForm() {
        return cForm;
    }

    /**
     * Reference to the Guard Form UI component.
     */
    private final transient GuardForm gForm;

    /**
     * Get the guard component element. This is the form in the left
     * hand panel for entering guard details.
     * @return The reference to the UI component.
     */
    public final GuardForm getGuardForm() {
        return gForm;
    }

    /**
     * Reference to the Node Form UI component.
     */
    private final transient NodeForm nForm;

    /**
     * Get the Node component element. This is the form in the left
     * hand panel for entering node details.
     * @return The reference to the UI component.
     */
    public final NodeForm getNodeForm() {
        return nForm;
    }

    /**
     * Reference to the Node Form UI component.
     */
    private final transient EndForm eForm;

    /**
     * Get the Node component element. This is the form in the left
     * hand panel for entering node details.
     * @return The reference to the UI component.
     */
    public final EndForm getEndForm() {
        return eForm;
    }

    /**
     * Constant panel refs.
     */

    /**
     * The attribute panel for guards.
     */
    private static final String GUARDPANEL = "guard";

    /**
     * The attribute panel for start nodes.
     */
    private static final String NODEPANEL = "start";

    /**
     * The attribute panel for Rest interface components.
     */
    private static final String COMPONENTPANEL = "component";

    /**
     * The attribute panel for HTTP message input.
     */
    private static final String MESSAGEPANEL = "message";

    /**
     * The attribute panel for end nodes.
     */
    private static final String ENDPANEL = "end";

    /**
     * The attribute panel for normal nodes.
     */
    private static final String NORMALPANEL = "normal";

    /**
     * The attribute panel for trigger nodes.
     */
    private static final String TRIGGERPANEL = "trigger";


    /**
     * Change the look and feel.
     * @param input The table to change the feel of.
     */
    public static void setTableConsistentLookAndFeel(final JTable input) {
         // Configure some of JTable's paramters
            input.setShowHorizontalLines(true);

            // Change the selection colour
            input.setSelectionForeground(Color.white);
            input.setSelectionBackground(Color.LIGHT_GRAY);
    }

    /**
     * Create a new instance of the attribute panel within the editor context.
     * @param parent The parent panel this is hosted in.
     * @param editor The editor this is hosted in.
     */
    public AttributePanel(final JPanel parent, final BasicGraphEditor editor) {
        // HTTP Method
        final String[] httpMethods = {"GET", "PUT", "POST", "DELETE"};
        final String[] dataTypes = {"XML", "JSON", "OTHER"};

        mForm = new MessageForm(httpMethods, dataTypes, editor);
        cForm = new ComponentForm();
        gForm = new GuardForm(editor);
        nForm = new NodeForm();
        eForm = new EndForm();

        //Create the panel that contains the "cards".
//        parent.add(new EmptyForm(), ENDPANEL);
        parent.add(gForm, GUARDPANEL);
        parent.add(nForm, NODEPANEL);
        parent.add(cForm, COMPONENTPANEL);
        parent.add(mForm, MESSAGEPANEL);
        parent.add(eForm, ENDPANEL);
        parent.add(new EmptyForm(), NORMALPANEL);
        parent.add(new EmptyForm(), TRIGGERPANEL);

    }

}
