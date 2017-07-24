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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.w3c.dom.Node;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Architecture;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.Parameter;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.JSON;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.XML;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.Guard;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.InteroperabilityReport;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.MsgEvent;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.UnexpectedEventException;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.architecturemodel.InvalidPatternReferenceException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.ProtocolMessage;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.InvalidJSONPathException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.InvalidXPathException;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.PathEvaluationResult;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data.PathEvaluationResult.DataFormat;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.specification.XMLStateMachine;

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
    private transient MsgEvent savedEvent;

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
     * A node has a report field that is used to annotate information about
     * why a test succeeds or fails. This is particularly used to explain
     * how a test reached an end state success or fail conclusion.
     */
    private String InteroperabilityReport = null;

    /**
     * Get the report statement for this state node.
     * @return The report as a string.
     */
    @Override
    public String getReport() {
        return this.InteroperabilityReport;
    }

    /**
     * A state reached can identify a successful or failed condition. This
     * is a string rather than a boolean to be extensible beyond yes or no. For
     * example, partial success values.
     */
    private String InteroperabilitySuccess = null;

    /**
     * Return the success condition of this node. This is particularly used
     * in end states to finalise the outcome of a full test.
     * @return A "true" or "false" string to indicate success.
     */
    @Override
    public String getSuccess() {
        return this.InteroperabilitySuccess;
    }

    /**
     * a reference to the hash map of pattern data
     */
    private final transient Map<String, String> dataConstants;

    /**
     *
     * @param nodeName This is the string identifier labelling the state. It must
     * be at least 2 characters long.
     * @param type must be one of the stateType enumeration. Null values are
     * not allowed.
     * @param arc The architecture context
     * @param report The report specification of interoperability reporting for the node.
     * @param success The success of the testing in reaching this state.
     * @throws InvalidStateMachineException error initialising node
     */
    public StateNode(final String nodeName, final StateType type, final Architecture arc,
            String report, String success) throws InvalidStateMachineException {

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
            this.dataConstants = arc.getDataConstants();
        }
        else {
            this.dataConstants = null;
        }
        this.nextStates = new ArrayList();
        this.stateType = type;

                this.InteroperabilityReport = report;
        this.InteroperabilitySuccess = success;
            
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
    public final String executeTransition(final BlockingQueue<MsgEvent> input, final InteroperabilityReport outputReport)
            throws UnexpectedEventException {
        try {
            if (!(this.isTrigger() || this.isLoop())) {
                throw new UnexpectedEventException("Trying to execute a message trigger when the state"
                        + "is not a trigger");
            }
            final ProtocolMessage action = this.nextStates.get(0).getTrigger();

            final MsgEvent retValue = action.invokeMessage();
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
    public final String evaluateTransition(final MsgEvent input, final InteroperabilityReport outputReport)
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

    public final String evaluateConditionalTransition(final InteroperabilityReport outputReport, String currentState)
            throws UnexpectedEventException {
        // Find transitions with matching resource locations


        /**
         * Iterate through each potential event transition to find a matching
         * next state. If no matches then we have an interoperability fail.
         * Report in the exception.
         */
        final Iterator<Transition> transIt = this.nextStates.iterator();
        while (transIt.hasNext()) {
            final Transition evTrans = transIt.next();
            // The counter transition
            if (!evTrans.listGuards().isEmpty()) {
                if (evaluateCounterGuards(evTrans.listGuards().get(0), outputReport)) {
                    outputReport.println("Transition to state " + evTrans.readLabel() + " successful");
                        return evTrans.readLabel();
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
        final MsgEvent rEv = stateA.getStoredEvent();

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
     * get the pattern data based on the expression
     * @param expression the expression to parse
     * @return the pattern data
     * @throws InvalidPatternReferenceException 
     */
    private String getData(String expression)
            throws InvalidPatternReferenceException {
        expression = expression.replaceAll("\\$", "");
        final int indexElement = expression.indexOf('.') + 1;
        if (indexElement != XMLStateMachine.DATA_TAG.length() + 1) {
            throw new InvalidPatternReferenceException("Invalid data tag: " + expression.substring(0, indexElement));
        }
        final String dataName = expression.substring(indexElement);
        if (dataName != null) {
            final String dataValue = this.dataConstants.get(dataName);
            if (dataValue == null) {
                throw new InvalidPatternReferenceException("Data field does not exist");
            }
            return dataValue;
        }
        throw new InvalidPatternReferenceException("Data field does not exist");
    }
    
    /**
     * The guard failure is reported to the interoperability report.
     * @param chGuard The rule that has failed.
     * @param value The input to the failed rule.
     * @param report The output location to report the failure.
     */
    private void reportGuardFailure(final Guard chGuard, final Parameter value, final InteroperabilityReport report) {
        switch (chGuard.getType()) {
            case EQUALS:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue() + "', while it was supposed to be equal to the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case NOTEQUALS:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue() + "', while it was supposed to be not equal to the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case GREATERTHAN:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue() + "', while it was supposed to be greater than the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case LESSTHAN:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue() + "', while it was supposed to be less than the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            default:
                report.printtabline("Guard test failed!");
        }
    }

    /**
     * The guard failure(due to a content evaluation) is reported to the interoperability report.
     * @param chGuard The rule that has failed.
     * @param value The input to the failed rule.
     * @param report The output location to report the failure.
     */
    private void reportGuardFailure(final Guard chGuard, final PathEvaluationResult value, final InteroperabilityReport report) {
        switch (chGuard.getType()) {
            case EQUALS:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue().toString() + "', while it was supposed to be equal to the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case NOTEQUALS:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue().toString() + "', while it was supposed to be not equal to the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case GREATERTHAN:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue().toString() + "', while it was supposed to be greater than the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case LESSTHAN:
                report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' is '" + value.getValue().toString() + "', while it was supposed to be less than the guard value: '" + chGuard.getGuardCompare() + "'");
                break;
            case CONTAINS:
                if (value.getType() == DataFormat.XML){
                    List<Node> nodesList = (List<Node>) value.getValue();

                    if (nodesList.isEmpty()){
                        report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' doesn't contain any child fields");
                    }
                    else {
                        String msg = "Guard test failed: '" + chGuard.getGuardLabel() + "' contains child fields (";

                        for (int i=0; i<nodesList.size()-1; i++){
                            msg += "'" + nodesList.get(i).getNodeName() + "' ";
                        }

                        msg += "'" + nodesList.get(nodesList.size()-1).getNodeName() + "') but doesn't contain child field '" + chGuard.getGuardCompare() + "'";
                        report.printtabline(msg);
                    }
                }
                else if (value.getType() == DataFormat.JSON) {
                    List<String> childFields = (List<String>) value.getValue();
                    if (childFields.isEmpty()){
                        report.printtabline("Guard test failed: '" + chGuard.getGuardLabel() + "' doesn't contain any child fields");
                    }
                    else {
                        String msg = "Guard test failed: '" + chGuard.getGuardLabel() + "' contains child fields (";

                        for (int i=0; i < childFields.size()-1; i++){
                            msg += "'" + childFields.get(i) + "' ";
                        }

                        msg += "'" + childFields.get(childFields.size()-1) + "') but doesn't contain child field '" + chGuard.getGuardCompare() + "'";
                        report.printtabline(msg);
                    }
                }

                break;
            default:
                report.printtabline("Guard test failed!");
        }
    }

     /**
     * The guard failure(due to an exception) is reported to the interoperability report.
     * @param chGuard The rule that has failed.
     * @param value The input to the failed rule.
     * @param report The output location to report the failure.
     * @param ex The exception, which caused the guard to fail
     */
    private void reportGuardFailure(final InteroperabilityReport report, Exception ex){
        report.printtabline("Guard test failed due to the following error:");
        report.printtabline(ex.getMessage());
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
            final String xpathExp = chGuard.getGuardLabel().substring(8, chGuard.getGuardLabel().length() - 1);
            final Parameter value = conditions.get(CONTENTLABEL);
            final Parameter dataType = conditions.get("http.content-type");
            PathEvaluationResult evaluationResult;
            if (dataType.getValue().contains("xml")) {
                try {
                    evaluationResult = XML.xmlContains(value.getValue(), xpathExp, chGuard.getGuardCompare());
                    if (!evaluationResult.getResult()) {
                        reportGuardFailure(chGuard, evaluationResult, report);
                        return false;
                    }
                }
                catch (InvalidXPathException ex) {
                    reportGuardFailure(report, ex);
                    return false;
                }
            }
            else if (dataType.getValue().contains("json")) {
                try {
                    evaluationResult = JSON.containsJSON(value.getValue(), xpathExp, chGuard.getGuardCompare());
                    if (!evaluationResult.getResult()) {
                        reportGuardFailure(chGuard, evaluationResult, report);
                        return false;
                    }
                }
                catch (InvalidJSONPathException ex) {
                    reportGuardFailure(report, ex);
                    return false;
                }
            }
        }
        else {
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
    private int arrayContentEvaluation(final Guard chGuard, final InteroperabilityReport report) {

        if(chGuard.getGuardLabel().equalsIgnoreCase("Index")){
            return Integer.valueOf(chGuard.getGuardCompare());
        }

        final String xpathExp = chGuard.getGuardLabel().substring(8, chGuard.getGuardLabel().length() - 1);
//        final Parameter value = conditions.get(CONTENTLABEL);
//        final Parameter dataType = conditions.get("http.content-type");
//        if (dataType.getValue().contains("xml")) {
//            return XML.getArraySize(value.getValue(), xpathExp);
//        } else if (dataType.getValue().contains("json")) {
//            if (!JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare())) {
//                reportGuardFailure(chGuard, value, report);
//                return 0;
//            }
//        } else {
//            return -1;
//        }
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
        PathEvaluationResult pathResult;
        if (dataType.getValue().contains("xml")) {
            try {
                Object exprValue = null;
                if(null != chGuard.getType()) switch (chGuard.getType()) {
                    case NOTEQUALS:
                        pathResult = XML.xmlAssert(value.getValue(), xpathExp, chGuard.getGuardCompare());
                        if (pathResult.getResult()) {
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case EQUALS:
                        pathResult = XML.xmlAssert(value.getValue(), xpathExp, chGuard.getGuardCompare());
                        if (!pathResult.getResult()) {
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case GREATERTHAN:
                        pathResult = XML.xmlCompare(value.getValue(), xpathExp, chGuard.getGuardCompare(), Guard.ComparisonType.GREATERTHAN);
                        if(!pathResult.getResult()){
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case LESSTHAN:
                        pathResult = XML.xmlCompare(value.getValue(), xpathExp, chGuard.getGuardCompare(), Guard.ComparisonType.LESSTHAN);
                        if(!pathResult.getResult()){
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    default:
                        reportGuardFailure(report, new InvalidInputException("Unknown condition type"));
                        return false;
                }
            }
            catch (InvalidXPathException ex) {
                reportGuardFailure(report, ex);
                return false;
            }
        }
        else if (dataType.getValue().contains("json")) {
            try {
                if(null != chGuard.getType()) switch (chGuard.getType()) {
                    case NOTEQUALS:
                        pathResult = JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare());
                        if (pathResult.getResult()) {
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case EQUALS:
                        pathResult = JSON.assertJSON(value.getValue(), xpathExp, chGuard.getGuardCompare());
                        if (!pathResult.getResult()) {
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case GREATERTHAN:
                        pathResult = JSON.compareJSON(value.getValue(), xpathExp, chGuard.getGuardCompare(), Guard.ComparisonType.GREATERTHAN);
                        if(!pathResult.getResult()){
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    case LESSTHAN:
                        pathResult = JSON.compareJSON(value.getValue(), xpathExp, chGuard.getGuardCompare(), Guard.ComparisonType.LESSTHAN);
                        if(!pathResult.getResult()){
                            reportGuardFailure(chGuard, pathResult, report);
                            return false;
                        }   break;
                    default:
                        reportGuardFailure(report, new InvalidInputException("Unknown condition type"));
                        return false;
                }
            }
            catch (InvalidJSONPathException ex) {
                reportGuardFailure(report, ex);
                return false;
            }
        }
        else {
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
    private boolean evaluateCounterGuards(final Guard chGuard, final InteroperabilityReport report) {
        int comparitor = -1;
        if(chGuard.getGuardLabel().equalsIgnoreCase("Index")){
            comparitor = Integer.valueOf(chGuard.getGuardCompare());
        }
        else if (chGuard.getGuardLabel().contains("$$")) {
            comparitor = Integer.valueOf(getStateValue(chGuard.getGuardLabel()));
        }

        if (this.counter == comparitor) {
            report.printtabline("Guard test succeeded: " + chGuard.getGuardLabel() + " is " + comparitor);
            return true;
        }
        report.printtabline("Counter Sequence: Current" + this.counter + "; Target: " + comparitor);
        return false;
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

        report.printtabline("Evaluating " + checks.size() +  " guards");

        final Iterator<Guard> itCheck = checks.iterator();
        while (itCheck.hasNext()) {
            try {
                final Guard chGuard = itCheck.next();
                if (chGuard.getGuardCompare().contains("$$")) {
                    if (chGuard.getGuardCompare().contains(XMLStateMachine.DATA_TAG)){
                        try {
                            chGuard.setGuardCompare(getData(chGuard.getGuardCompare()));
                        }
                        catch (InvalidPatternReferenceException ex){
                            chGuard.setGuardCompare(null);
                        }
                    }
                    else {
                        chGuard.setGuardCompare(getStateValue(chGuard.getGuardCompare()));
                    }
                }
                
                if (chGuard.getType() == Guard.ComparisonType.CONTAINS) {
                    if (!guardContainsEvaluation(chGuard, conditions, report)) {
                        return false;
                    }
                }
                else if (chGuard.getGuardLabel().startsWith(CONTENTLABEL)) {
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

                switch (chGuard.getType()) {
                    case NOTEQUALS:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' is not equal to '" + chGuard.getGuardCompare() + "'");
                        break;
                    case EQUALS:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' is equal to '" + chGuard.getGuardCompare() + "'");
                        break;
                    case GREATERTHAN:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' is greater than '" + chGuard.getGuardCompare() + "'");
                        break;
                    case LESSTHAN:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' is less than '" + chGuard.getGuardCompare() + "'");
                        break;
                    case CONTAINS:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' contains child field '" + chGuard.getGuardCompare() + "'");
                        break;
                    default:
                        report.printtabline("Guard test succeeded: '" + chGuard.getGuardLabel() + "' is '" + chGuard.getGuardCompare() + "'");
                        break;
                }
            }
            catch (InvalidInputException ex) {
                ServiceLogger.LOG.error("Invalid guard test specification", ex);
                return false;
            }
        }
        return true;
    }

    @Override
    public final MsgEvent getStoredEvent() {
        return this.savedEvent;
    }
}
