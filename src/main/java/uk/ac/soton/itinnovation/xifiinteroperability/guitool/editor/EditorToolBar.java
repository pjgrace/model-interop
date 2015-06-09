/*
Copyright (c) 2001-2014, JGraph Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the JGraph nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL JGRAPH BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor;

import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.ExecuteAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.GraphAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.NewAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.OpenAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions.SaveAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.VerifyAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.EditorActions.XMLAction;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.FileActions;
import uk.ac.soton.itinnovation.xifiinteroperability.guitool.editor.actions.PopUpMenuActions.HistoryAction;

/**
 * The editor toolbar at the top of the GUI. Simply a set of buttons
 * with a corresponding action in the EditorActions class. Eg. Open, Save,
 * Undo, etc.
 * @author pjg
 */
public class EditorToolBar extends JToolBar {

	/**
	 * Construct a new instance of the editor toolbar.
         * @param editor The basic graph editor that the toolbar is added to.
         * @param orientation The orientation of the toolbar (horizontal)
	 */
	public EditorToolBar(final BasicGraphEditor editor, final int orientation) {
		super(orientation);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(3, 3, 3, 3), getBorder()));
		setFloatable(false);

		add(editor.bind("New", new NewAction(editor),
				"/images/new.gif"));
		add(editor.bind("Open", new OpenAction(editor),
				"/images/open.gif"));
		add(editor.bind("Save", new SaveAction(editor),
				"/images/save.gif"));

                		addSeparator();

                add(editor.bind("Import", new FileActions.ImportAction(editor),
				"/images/import-icon.png"));

                addSeparator();
		add(editor.bind("Delete", /*mxGraphActions.getDeleteAction()*/ new EditorActions.Delete(editor),
				"/images/delete.gif"));

		addSeparator();

		add(editor.bind("Undo", new HistoryAction(true, editor),
				"/images/undo.gif"));
		add(editor.bind("Redo", new HistoryAction(false, editor),
				"/images/redo.gif"));

		addSeparator();

                add(editor.bind("XML", new XMLAction(editor),
				"/images/xml.png"));

                add(editor.bind("Graph", new GraphAction(editor),
				"/images/graph.png"));


                addSeparator();

                add(editor.bind("Verify", new VerifyAction(),
				"/images/validate.png"));

                addSeparator();


		add(editor.bind("Run", new ExecuteAction(),
				"/images/test.png"));

	}
}

