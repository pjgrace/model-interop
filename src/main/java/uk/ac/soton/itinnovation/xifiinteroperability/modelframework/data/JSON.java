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

package uk.ac.soton.itinnovation.xifiinteroperability.modelframework.data;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.io.IOException;
import java.util.Locale;
import uk.ac.soton.itinnovation.xifiinteroperability.ServiceLogger;
import uk.ac.soton.itinnovation.xifiinteroperability.modelframework.Guard;

/**
 * Methods for evaluating JSON Data. Utility class.
 * @author pjg
 */
public final class JSON {

    /**
     * Utility class. Private constructor.
     */
    private JSON() {
        // empty implementation.
    }

    /**
     * Assert that a JSON document reference (jsonpath expr) evaluates to
     * a given value.
     * @param jsondoc The document to check.
     * @param reference The JSON path expression.
     * @param value The required value.
     * @return PathEvaluationResult with the boolean result and the value of the JSONPath expression
     * @throws InvalidJSONPathException Thrown in case of an invalid JSONPath in a guard.
     */
    public static PathEvaluationResult assertJSON(final String jsondoc,
                        final String reference, final Object value) throws InvalidJSONPathException {
        try {
            final String xprVal = readValue(jsondoc.toLowerCase(Locale.ENGLISH), reference.toLowerCase(Locale.ENGLISH));
            final String jsonVal = ((String) value).toLowerCase(Locale.ENGLISH);
            return new PathEvaluationResult(jsonVal.equalsIgnoreCase(xprVal), xprVal);
//            JsonAssert.with(jsondoc.toLowerCase(Locale.ENGLISH)).assertThat(reference, Matchers.equalTo(jsonVal));
        }
        catch (PathNotFoundException ex2) {
            final String jsonVal = ((String) value).toLowerCase(Locale.ENGLISH);
            if(jsonVal.equalsIgnoreCase("null")){
                return new PathEvaluationResult(true, "null");
            }
            throw new InvalidJSONPathException("JSONPath '" + reference + "' is invalid or does not exist.");
        }
        catch (Exception ex) {
            throw new InvalidJSONPathException("JSONPath '" + reference + "' is invalid or does not exist.");
        }
    }

    /**
     * Compare a JSON document reference (jsonpath expr) against a particular value.
     * LESSTHAN or GREATERTHAN comparisons only, use assertJSON for EQUAL and NOTEQUAL comparisons
     * 
     * @param jsondoc The document to check.
     * @param reference The JSON path expression.
     * @param value The required value.
     * @param comparisonType The type of the comparison, GREATERTHAN or LESSTHAN
     * @return PathEvaluationResult with the boolean result and the value of the JSONPath expression
     * @throws InvalidJSONPathException Thrown in case of an invalid JSONPath in a guard.
     */
    public static PathEvaluationResult compareJSON(final String jsondoc,
                        final String reference, final Object value, final Guard.ComparisonType comparisonType) throws InvalidJSONPathException {
        try {
            final String xprVal = readValue(jsondoc.toLowerCase(Locale.ENGLISH), reference.toLowerCase(Locale.ENGLISH));
            final String jsonVal = ((String) value).toLowerCase(Locale.ENGLISH);
            if (comparisonType == Guard.ComparisonType.GREATERTHAN){
                try{
                    double a = new Double(xprVal);
                    double b = new Double(jsonVal);
                    return new PathEvaluationResult(a > b, xprVal);
                } catch(Exception ex) {
                    return new PathEvaluationResult(false, xprVal);
                }
            }
            else if (comparisonType == Guard.ComparisonType.LESSTHAN){
                try{
                    double a = new Double(xprVal);
                    double b = new Double(jsonVal);
                    return new PathEvaluationResult(a < b, xprVal);
                } catch(Exception ex) {
                    return new PathEvaluationResult(false, xprVal);
                }
            }
            return new PathEvaluationResult(false, null);
        }
        catch (PathNotFoundException ex2) {
            throw new InvalidJSONPathException("JSONPath '" + reference + "' is invalid or does not exist.");
        }
        catch (Exception ex) {
            throw new InvalidJSONPathException("JSONPath '" + reference + "' is invalid or does not exist.");
        }
    }
    
    /**
     * Validate a JSON document against a schema.
     * @param jsonDoc The full json document content as a string.
     * @param schemaIn The full json schema as a string.
     * @return true if the document validates.
     */
    public static boolean validateJSON(final String jsonDoc, final String schemaIn) {

        try {
            final JsonNode fstabSchema = JsonLoader.fromResource(schemaIn);

            final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

            final JsonSchema schema = factory.getJsonSchema(fstabSchema);

            schema.validate(fstabSchema);
            return true;
        } catch (ProcessingException ex) {
            ServiceLogger.LOG.error("json input does not comply with schema", ex);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Error reading json data", ex);
        }
        return false;
    }

    /**
     * Read a JSON value from a doc based on a JSON Path expression.
     * @param jsondoc The json content.
     * @param pathexpr The json path expression.
     * @return The data value as a string (Can be typed later).
     */
    public static String readValue(final String jsondoc, final String pathexpr) {
        if (pathexpr.equalsIgnoreCase("*")) {
            return jsondoc;
        }
        return JsonPath.read(jsondoc, pathexpr).toString();
    }

    /**
     * Write a given value into a json doc at the given location by the
     * json path expression.
     * @param jsondoc The json document.
     * @param param The path expression.
     * @param val The value to write.
     * @return The newly edited json document.
     */
    public static String writeValue(final String jsondoc, final String param, final String val) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final JsonNode jsn = objectMapper.readTree(jsondoc);

            ((ObjectNode) jsn).put(param, val);
            return objectMapper.writeValueAsString(jsn);
        } catch (IOException ex) {
            ServiceLogger.LOG.error("Couldn't write JSO value: " + ex.getMessage());
            return null;
        }
    }
}
