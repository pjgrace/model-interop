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


import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Parameter;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.JSON;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.XML;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.Guard;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.RESTEvent;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.RESTMessage;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.UnexpectedEventException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;

/**
 * State in a state machine.
 *
 * @author pjg
 */
public class StateNode implements State {

    /**
     * State node label. Matches the label in the state machine diagram of the
     * circle node.
     */
    private final transient String name;

    /**
     * The values describing the state type, start, trigger, etc.
     * Can be start and trigger, or start and normal.
     */
    private final transient StateType stateType;

    /**
     * Each node has a set of one or more nodes that it points to. An end
     * node is the exception and the end node must have zero entries.
     */
    private final transient List<Transition> nextStates;

    /**
     * After a transition has occurred, the event that this state receives
     * will be saved for future reference.
     */
    private transient RESTEvent savedEvent;

    /**
     * The state machine that this state node belongs to.
     */
    private transient StateMachine stateMachine;

    /**
     * Counter. If this is a loop node
     */
    private int counter = 0;
    /**
     * Constant to the content label of a rest event.
     */
    private static final String CONTENTLABEL = "content";

    /**
     *
     * @param nodeName This is the string identifier labelling the state. It must
     * be at least 2 characters long.
     * @param type must be one of the stateType enumeration. Null values are
     * not allowed.
     * @param arc The architecture context
     * @throws InvalidStateMachineException error initialising node
     */
    public StateNode(final String nodeName, final StateType type, final Architecture arc)
        throws InvalidStateMachineException {

        if ((nodeName == null) || (type == null)) {
                throw new InvalidStateMachineException("State " + nodeName + " contains"
                        + "null values for input");
        }
        if (nodeName.length() < 2) {
            throw new InvalidStateMachineException("Invalid label - must be"
                    + "at least 2 characters long");
        }
        // Initialise the object values
	this.name = nodeName;
        if (arc != null) {
            this.stateMachine = arc.getStateMachine();
        }
        this.nextStates = new ArrayList();
        this.stateType = type;
    }

    /**
     * Update this state's counter value.
     * @param change The value to update the counter by e.g. increment = 1, dec
     * = -1.
     */
    @Override
    public void counter(int change){
        this.counter = this.counter + change;
    }

    @Override
    public int getCounter() {
        return this.counter;
    }

    /**
     * Read the state label. Within a state machine, labels are
     * unique i.e. no two states can have the same label.
     * @return A string with the state label.
     */
    @Override
    public final String getLabel() {
	return this.name;
    }

    /**
     * Return true if this is an end node state; otherwise return false.
     * @return boolean value indicating end node status
     */
    @Override
    public final boolean isEndNode() {
        return stateType.equals(StateType.END);
    }

    /**
     * Return true if this is a start node state; otherwise return false.
     * @return boolean value indicating start node status
     */
    @Override
    public final boolean isStartNode() {
	return (stateType.equals(StateType.START) || stateType.equals(StateType.TRIGGERSTART));
    }

    /**
     * Return true if this is a trigger node state; otherwise return false.
     * @return boolean value indicating trigger node status
     */
    @Override
    public final boolean isTrigger() {
        return (stateType.equals(StateType.TRIGGER) || stateType.equals(StateType.TRIGGERSTART));
    }

    /**
     * Return true if this is a trigger node state; otherwise return false.
     * @return boolean value indicating trigger node status
     */
    @Override
    public final boolean isLoop() {
        return stateType.equals(StateType.LOOP);
    }

     /**
     * Adds a transition between two states in the state machine. If
 both states do not exist then an InvalidTransitionException is thrown.
     * @param trans the transition of guards that must evaluate to true
     * if the transition is to be taken.
     * @throws InvalidTransitionException Adding invalid transition.
     */
    @Override
    public final void addTransition(final Transition trans)
            throws InvalidTransitionException {
	this.nextStates.add(trans);
    }


    /**
     * List the set of transitions possible from this state.
     * @return The list of transitions
     */
    @Override
    public final List<Transition> getTransitions() {
        return this.nextStates;
    };

    /**
     * Execute transition - which can only be applied to a trigger transition
     * and not a guarded transition.
     *
     * @param input The state machine's message queue - when we recieve a response
     * from the trigger message this will be pushed onto this queue
     * @param outputReport The running output report describing the execution of the
     * tests.
     * @return The label of the next state to transition to.
     * @throws UnexpectedEventException An event not described by the expected state machine
     * behaviour has happened to indicate and interoperability problem.
     */
    @Override
    public final String executeTransition(final BlockingQueue<RESTEvent> input, final InteroperabilityReport outputReport)
            throws UnexpectedEventException {
        try {
            if (!(this.isTrigger() || this.isLoop())) {
                throw new UnexpectedEventException("Trying to execute a message trigger when the state"
                        + "is not a trigger");
            }
            final RESTMessage action = this.nextStates.get(0).getTrigger();

            final RESTEvent retValue = action.invokeMessage();
            input.put(retValue);

            outputReport.println("Invoked action - moving to state: " + this.nextStates.get(0).readLabel());

            // A Trigger state can only have one transition
            return this.nextStates.get(0).readLabel();
        } catch (InterruptedException ex) {
            ServiceLogger.LOG.error("Cannot execute transition", ex);
            throw new UnexpectedEventException("Cannot access locked state machine", ex);
        }
    };

    /**
     * Evaluate a new event (a rest operation) against the set of transitions
     * at this state. If there is a complete match then the next state to
     * transition to is returned. If not - we have an error event (i.e.
     * an interoperability error) and hence we throw an unexpected event error.
     *
     * @param input The details of the occured event - a rest operations with
     * data and parameters to compare against the condition.
     * @param outputReport The output stream to return the evaluation result.
     *
     * @return the state to move to based upon the event
     * @throws UnexpectedEventException event error - no transition matches the event.
     */
    @Override
    public final String evaluateTransition(final RESTEvent input, final InteroperabilityReport outputReport)
            throws UnexpectedEventException {
        // Find transitions with matching resource locations

        this.savedEvent = input;

        /**
         * Iterate through each potential event transition to find a matching
         * next state. If no matches then we have an interoperability fail.
         * Report in the exception.
         */
        final Iterator<Transition> transIt = this.nextStates.iterator();
        while (transIt.hasNext()) {
            final Transition evTrans = transIt.next();
            if (!evTrans.listGuards().isEmpty()) {
                if (evaluateGuards(evTrans.listGuards(), input.getParameterMap(), outputReport)) {
                    outputReport.println("Transition to state " + evTrans.readLabel() + " successful");
                        return evTrans.readLabel();
                }
            }
        }
        outputReport.println("Fail: no transition possible");
        throw new UnexpectedEventException("Fail: no transition possible");
    };

    public final String evaluateConditionalTransition(final RESTEvent input, final InteroperabilityReport outputReport, String currentState)
            throws UnexpectedEventException {
        // Find transitions with matching resource locations

        if(input != null) {
            this.savedEvent = input;

            /**
             * Iterate through each potential event transition to find a matching
             * next state. If no matches then we have an interoperability fail.
             * Report in the exception.
             */
            final Iterator<Transition> transIt = this.nextStates.iterator();
            while (transIt.hasNext()) {
                final Transition evTrans = transIt.next();
                if (!evTrans.listGuards().isEmpty()) {
                    if (evaluateGuards(evTrans.listGuards(), this.savedEvent.getParameterMap(), outputReport)) {
                        outputReport.println("Transition to state " + evTrans.readLabel() + " successful");
                            return evTrans.readLabel();
                    }
                }
            }
        } else {
            final Iterator<Transition> transIt = this.nextStates.iterator();
            while (transIt.hasNext()) {
                final Transition evTrans = transIt.next();
                if (!evTrans.listGuards().isEmpty()) {
                    if (evaluateGuards(evTrans.listGuards(), new HashMap<String, Parameter>(), outputReport)) {
                        outputReport.println("Transition to state " + evTrans.readLabel() + " successful");
                            return evTrans.readLabel();
                    }
                }
            }
        }

        return currentState;
    };

    /**
     * Based upon the expression input as a parameter get the corresponding
     * value from the state machine stored event in the trace.
     *
     * The first part of the expression is the state label in the machine.
     * The second part of the expression if either "content or headers"
     * The final part of the expression is the field label value to read.
     *
     * @param exp The state$$eventpart[headers or content]$$field
     * @return The value of the evaluated expression.
     */
    private String getStateValue(final String exp) {

        final String[] exprSplit = exp.replaceAll("\\$", "").split("\\|");
        if (exprSplit.length != 3) {
            return null;
        }
        final State stateA = this.stateMachine.getState(exprSplit[0]);
        final RESTEvent rEv = stateA.getStoredEvent();

        if (exprSplit[1].equalsIgnoreCase(CONTENTLABEL)) {
            final String content = rEv.getDataBody().getData();
            if (rEv.getDataBody().getType().contains("xml")) {
                return XML.readValue(content, exprSplit[2]);
            } else {
                return JSON.readValue("$."+content, exprSplit[2]);
            }
        } else if (exprSplit[1].equalsIgnoreCase("headers")) {
            return rEv.getParameterMap().get(exprSplit[2]).getValue();
        }
        return null;
    }

    /**
     * The guard failure is reported to the interoperability report.
     * @param chGuard The rule that has failed.
     * @param value The input to the failed rule.
     * @param report The output location to report the failure.
     */
    private void reportGuardFailure(final Guard chGuard, final Parameter value, final InteroperabilityReport report) {
        report.printtabline("Guard test failed: " + chGuard.getGuardLabel() + " is " + value.getValue() + " which !=" + chGuard.getGuardCompare());
    }

    /**
     * Evaluate a guard based on the contains operation.
     * @param chGuard The guard to evaluate
     * @param conditions The set of events
     * @param report The output report stream
     * @return True if the guard is true.
     */
    private boolean guardContainsEvaluation(final Guard chGuard, final Map<String, Parameter> conditions,
            final InteroperabilityReport report) {

        if (chGuard.getGuardLabel().startsWith(CONTENTLABEL)) {
            final String xpathExp = chGuard.getGuardCompare();
            final Parameter value = conditions.get(CONTENTLABEL);
            final Parameter dataType = conditions.get("http.content-type");
            if (dataType.getValue().contains("xml")) {
                if (!XML.xmlAssert(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            } else if (dataType.getValue().contains("json")) {
                final Object document = Configuration.defaultConfiguration().jsonProvider().parse(value.getValue());
                final String grdComp = JsonPath.read(document, chGuard.getGuardCompare());
                if (grdComp == null) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            }
        } else {
            try {
                final Object compareVal = conditions;
                if (!chGuard.evaluate(compareVal)) {
                    report.printtabline("Guard test failed: " + chGuard.getGuardLabel() + " doesn't contain " + chGuard.getGuardCompare());
                    return false;
                }
            } catch (InvalidInputException ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * This evaluates an index of array
     * @param chGuard
     * @param conditions
     * @param report
     * @return
     */
    private int arrayContentEvaluation(final Guard chGuard, final Map<String, Parameter> conditions,
            final InteroperabilityReport report) {

        if(chGuard.getGuardLabel().equalsIgnoreCase("Index")){
            return Integer.valueOf(chGuard.getGuardCompare());
        }

        final String xpathExp = chGuard.getGuardLabel().substring(8, chGuard.getGuardLabel().length() - 1);
        final Parameter value = conditions.get(CONTENTLABEL);
        final Parameter dataType = conditions.get("http.content-type");
        if (dataType.getValue().contains("xml")) {
            return XML.getArraySize(value.getValue(), xpathExp);
        } else if (dataType.getValue().contains("json")) {
            if (!JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                reportGuardFailure(chGuard, value, report);
                return 0;
            }
        } else {
            return -1;
        }
        return -1;
    }

    /**
     *
     * Evaluate the guard content.
     * @param chGuard The guard to evaluate with.
     * @param conditions The list of conditions that occurred from the event.
     * @param report The output stream to output the data.
     * @return True if all guards evaluate against the conditions.
     */
    private boolean contentEvaluation(final Guard chGuard, final Map<String, Parameter> conditions,
            final InteroperabilityReport report) {
        final String xpathExp = chGuard.getGuardLabel().substring(8, chGuard.getGuardLabel().length() - 1);
        final Parameter value = conditions.get(CONTENTLABEL);
        final Parameter dataType = conditions.get("http.content-type");
        if (dataType.getValue().contains("xml")) {
            if(chGuard.getType() == Guard.ComparisonType.NOTEQUALS) {
                if (XML.xmlAssert(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            }
            else if (chGuard.getType() == Guard.ComparisonType.EQUALS){
                if (!XML.xmlAssert(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            }
        } else if (dataType.getValue().contains("json")) {
            if(chGuard.getType() == Guard.ComparisonType.NOTEQUALS) {
                if (JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            }
            else if (chGuard.getType() == Guard.ComparisonType.EQUALS){
                if (!JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
                    reportGuardFailure(chGuard, value, report);
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Evaluate the guards on a transition of this state.
     * @param checks The list of guards to evaluate with.
     * @param conditions The list of conditions that occurred from the event.
     * @param report The output stream to output the data.
     * @return True if all guards evaluate against the conditions.
     */
    private boolean evaluateGuards(final List<Guard> checks,
            final Map<String, Parameter> conditions, final InteroperabilityReport report) {

        final Iterator<Guard> itCheck = checks.iterator();
        while (itCheck.hasNext()) {
            try {
                final Guard chGuard = itCheck.next();
                if (chGuard.getGuardCompare().contains("$$")) {
                    chGuard.setGuardCompare(getStateValue(chGuard.getGuardCompare()));
                }
                if (chGuard.getType() == Guard.ComparisonType.COUNTER) {
                    int arraySize = arrayContentEvaluation(chGuard, conditions, report);

                    if (this.counter == arraySize) {
                        return true;
                    }
                    report.printtabline("Counter Sequence: Current" + this.counter + "; Target: " + arraySize);
                    return false;
                }
                else if (chGuard.getType() == Guard.ComparisonType.CONTAINS) {
                    if (!guardContainsEvaluation(chGuard, conditions, report)) {
                        return false;
                    }
                } else if (chGuard.getGuardLabel().startsWith(CONTENTLABEL)) {
                    if (!contentEvaluation(chGuard, conditions, report)) {
                        return false;
                    }
                }
                else {
                    final Parameter value = conditions.get(chGuard.getGuardLabel());
                    if (value == null) {
                        report.printtabline("Guard test failed: " + chGuard.getGuardLabel() + " is not part of message");
                        return false;
                    }
                    final Object compareVal = value.getValue();
                    if (!chGuard.evaluate(compareVal)) {
                        reportGuardFailure(chGuard, value, report);
                        return false;
                    }
                }
                report.printtabline("Guard test succeeded: " + chGuard.getGuardLabel() + " is " + chGuard.getGuardCompare());
            } catch (InvalidInputException ex) {
                ServiceLogger.LOG.error("Invalid guard test specification", ex);
                return false;
            }
        }
        return true;
    }

    @Override
    public final RESTEvent getStoredEvent() {
        return this.savedEvent;
    }

}