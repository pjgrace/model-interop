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


package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.statemachine;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.EventCapture;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.RESTEvent;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.UnexpectedEventException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * The java data representation of a set of states that form a state machine.
 * This executable in response to discreet events.
 *
 * @author pjg
 */
public class StateMachine implements EventCapture {
    /**
     * References to two states that we need to operate the state
     * machine: the first state, and the current state.
     */

    /**
     * Each state machine can have only one current state.
     */
    private transient State currentState;

    /**
     * Each state machine has only one start state.
     */
    private transient State firstState;

    /**
     * Synchronised blocking queue for execution input. That is,
     * the only input to this machine comes via this queue. The RESTLET
     * framework captures REST operations and pushes them as events to
     * this queue.
     */
    private final transient BlockingQueue<RESTEvent> eventQueue;

    /**
     * Each state machine is a set of states. We use a hash map to
     * perform transitions. Label - toState. Query toState. Set
     * currentState to this state. etc...
     */
    private transient Map<String, State> states;

    /**
     * Each state machine when executed traces an output report.
     */
    private final transient InteroperabilityReport outputReport;

    /**
     * Construct a new state machine and create and interoperability report.
     */
    public StateMachine() {
        this.eventQueue = new ArrayBlockingQueue(50);
        outputReport = new InteroperabilityReport();
    }

    /**
     * Create a new state machine with the interoperability report output
     * already provided.
     * @param rep The interoperability report reference.
     */
    public StateMachine(final InteroperabilityReport rep) {
        this.eventQueue = new ArrayBlockingQueue(50);
        outputReport = rep;
    }

    /**
     * Construct a new state machine with a first state and the
     * remaining set of states.
     *
     * @param firstSt The label of the first state.
     * @param stateSet The set of states to create a machine from.
     */
    public final void inputContent(final String firstSt, final Map<String, State> stateSet) {
        this.states = new HashMap();
        for (State s : stateSet.values()) {
            addState(s);
        }
        this.firstState = getState(firstSt);
    }


    /**
     * Assign a state as the current executable position in the state machine.
     * @param state The current position state.
     */
    public final void setCurrentState(final State state) {
        this.currentState = state;
    }

    /**
     * Return the current executable position in the state machine.
     * @return state The current position state.
     */
    public final State getCurrentState() {
        return this.currentState;
    }

    /**
     * Get the start state. Used for beginning the execution phase.
     * @return The fixed start state reference.
     */
    public final State getStartState() {
        return firstState;
    }

    /**
     * Add a new state to the state machine.
     * @param newState The state object to add.
     */
    public final void addState(final State newState) {
	this.states.put(newState.getLabel().toLowerCase(), newState);
    }

    /**
     * Event interface implemenation. When a discrete event is detected it is
     * sent here to move the state machine execution.
     * @param restEvent The new event detected in the distributed system e.g. a
     * rest message.
     */
    @Override
    public final void pushEvent(final RESTEvent restEvent) {
        try {
            this.eventQueue.put(restEvent);
        } catch (InterruptedException ex) {
            ServiceLogger.LOG.debug("Event queue interupted", ex);
        }
    }

    /**
     * Report an exception. Potentially this method could be added into
     * the execution phase, so the state machine can deal with exceptions.
     * @param excep The exception observed.
     */
    @Override
    public final void logException(final Exception excep) {
        outputReport.println("Interoperability Error: " + excep.getMessage());
    }

    /**
     * Start the trace and begin outputting the event tests that correspond
     * to the state machine checks.
     * @return The string version of the output report.
     */
    public final String start()	{
	currentState = this.firstState;
        if (currentState == null) {
            outputReport.println("Invalid pattern -> no valid start state");
            return outputReport.outputReport();
        }

        outputReport.println("Test started - run the application");
        outputReport.println("----------------------------------");
        outputReport.println("Starting trace at Node:" + currentState.getLabel());

        while (!currentState.isEndNode()) {
            try {
                if (currentState.isTrigger()) {
                    currentState = getState(currentState.executeTransition(this.eventQueue, outputReport));
                }
                else if (currentState.isLoop()) {
                    String tState = currentState.evaluateConditionalTransition(outputReport, currentState.getLabel());
                    if(tState.equalsIgnoreCase(currentState.getLabel())) {
                        currentState.counter(1);
                        currentState = getState(currentState.executeTransition(this.eventQueue, outputReport));
                    }
                    else {
                        currentState = getState(tState);
                    }
                }
                else {
                    currentState = getState(currentState.evaluateTransition(this.eventQueue.take(), outputReport));
                    if (currentState == null) {
                        ServiceLogger.LOG.error("Invalid state machine - could not find next state");
                        return outputReport.outputReport();
                    }
                    outputReport.println("Transition Success - move to state:" + currentState.getLabel());
                }
            } catch (UnexpectedEventException ex) {
               logException(ex);
               return outputReport.outputReport();
            } catch (InterruptedException ex) {
                ServiceLogger.LOG.error("Error processing events", ex);
            }
        }
        outputReport.println("End node reached --> Application interoperates correctly");
        return outputReport.outputReport();
    }

    /**
     * Given the label id, retrieve a state from the state machine.
     * @param label The id of the state - the label.
     * @return The state object.
     */
    public final State getState(final String label) {
        return this.states.get(label.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Create a string representation of the state machine object.
     * @return The string description.
     */
    @Override
    public final String toString() {
	String sep = "";
	final StringBuilder sBuilder = new StringBuilder();
	for (String key : states.keySet()) {
	    sBuilder.append(sep)
		.append(states.get(key).toString());
	    sep = "\n";
	}
	return sBuilder.toString();
    }
}