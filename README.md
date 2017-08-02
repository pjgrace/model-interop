# connect-iot  
A set of model driven engineering tools to engineer interoperable IoT and Web solutions 

## Brief Introduction
Two of the biggest challenges researches and developers of IoT systems face are the establishment of interoperability and the compliance with certain specifications. This is mainly due to the significantly high level of heterogeneity seen in the IoT sector. Typical examples are the use of different protocols (HTTP, MQTT, COAP), data formats (XML, JSON, etc) and communication technologies. To tame these challenges efficiently and accurately this project presents a **_Model-based Interoperability Testing Tool_**. The tool uses model-driven approaches to handle the issues mentioned above and ensure interoperability and compliance are achieved even if the person using the tool is not an expert in this field of study. The tool offers the possibilities of creating, using and reusing _interoperability models_, also called _patterns_. Thus, the development complexity is considerably reduced by removing the burden of manually testing all interoperability and specification requirements.

Read more at the following [article](https://link.springer.com/article/10.1007/s12243-015-0487-2).

## Install and Run

To run the editor tool, please execute the following:

```
mvn clean test
```

## Basic tutorial on using the tool

### Interoperability patterns
The interoperability patterns a.k.a models are simply xml data with all the information required to build the state machine and the states graph for the test.
* **_I have an interoperability pattern I want to test against_** - if you have a pre-made model you want to test against, you can load the xml file of the model using the toolbar of the tool and everything will be generated from the loaded information. Here is the sequence to follow in the toolbar.  
**File** -> **Open File** -> _choose XML file_  
Pre-made interoperability patterns can be found on the [Fiesta IoT](http://fiesta-iot.eu/) website.

* **_I want to create an interoperability pattern and test against it or let others test against it_** - if you want to create your own interoperability pattern and test against it or let others test against it, you can use the drag-and-drop method of the tool to create the state diagram (a directed graph with nodes representing states and edges representing transitions) and fill in the details you want to test.

This tutorial is going to concentrate mainly on the latter since the former could be interpreted as a subcase of the second case.

### Creating interoperability patterns
So, in this tutorial I am going to show you how to use the tool by creating a simple interoperability model for the [Fixer](http://fixer.io/) API. In the end, you will have a model, which you can use to check whether a custom API you or someone else created is interoperable with the **Fixer** API.

#### Quick overview of the API
The Fixer API is a free API for acquiring current and previous exchange rates published by the European Central Bank. The data format the API uses is JSON. Using the API is very easy. You can visit the [website](http://fixer.io/) and check their great explanations on how the API works. I chose this API as an example since it doesn't require any credentials or security tokens to be used, which makes it perfect for a simple tutorial.

#### Creating the state diagram
A state diagram is basically a directed graph. The nodes in this graph represent behaviour states while edges represent behaviour transitions between states. A state can be interpreted as a state of a distributed application (not an individual service) waiting to observe an event. A transition represents a change in state based upon an observed event matching a set of rules regarding the required behaviour.

* **Creating a start state**  
A start state is the state from where the pattern evaluation starts. Every valid pattern should contain exactly one start state. There are two types of start nodes that can be used in the graph: **Start** node and **Triggerstart** node. The latter is a special case of the former. The difference is that a **Triggerstart** node triggers an event as soon as the pattern evaluation begins, while when using a **Start** node it should be followed by a **Trigger** node, which triggers an event. The **Trigger** node will be explained later in this tutorial.  
These are the two icons being used for those nodes:  
![Start node][start_node] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ![Triggerstart node][triggerstart_node]  
The left icon represents a **Start** node while the right one is a **Triggerstart** node. For the **Fixer** pattern, I am going to use a **Triggerstart** node.   
On the top left of the tool, you can see a bunch of icons under the behaviour tab:
![Start screenshot][screenshot-1]
Drag and drop the **Triggerstart** icon to the panel under the _Interoperability Behaviour Model_ label and you are done with this step.
![Drag screenshot][screenshot-2]


* **Adding global pattern data**  
Pattern data is global data for which you assign IDs so that you can easily access it throughout the whole model. This data is assigned to the start node in the graph. In our case, we have a **Triggerstart** node. Hence, we will assign the pattern data to the node we created in the previous step. Click on the icon of the trigger start node we created. On the left, you see a form to add new node attributes (pattern data) and a table with all the pattern data this node has. We haven't added any pattern data, yet, so the table should be empty.  
![Pattern data screenshot][screenshot-3]  
A model can be valid even if it doesn't contain any pattern data, but for the sake of the tutorial I am going to add data with ID _base_ and value _GBP_. This will be the base currency we are going to use for the **Fixer** API. Use the form to add the pattern data and you are done with this step.
![Add pattern data screenshot][screenshot-4]


* **Adding a normal state**  
Normal states are just event-observing states with no special function. Usually, a **Normal** node follows after a **Trigger** or **Triggerstart** node, so that the triggered event can be captured and evaluated against a set of rules (guards).  
The icon being used for a **Normal** node is:  
![Normal node][normal_node]  
Again, from the icons on the top-left of the tool, drag and drop the **Normal** icon to the panel under the _Interoperability Behaviour Model_ label and you are done with this step.
![Dragging normal node screenshot][screenshot-5]


* **Adding your first transition**  
Next step is to add a transition between the **Triggerstart** node and the **Normal** node. Keep in mind that **Triggerstart** and **Start** nodes cannot be the target of a transition. So our transition will have the **Triggerstart** node as a source and the **Normal** node as a target. Click on the **Triggerstart** node and drag the mouse to the **Normal** node. This will create an edge between the two nodes.  
![Adding transition][screenshot-6]


* **Adding a system component**  
Before continuing with the state diagram we should add a system component to our model, which will link to the **Fixer** API. Again on the top-left of the tool, where the state nodes icons are located click on the **Deployment** tab. You must see two icons: one representing an interface and one representing a client.  
![Deployment tab][screenshot-7]  
Choose the interface icon, drag-and-drop it to the panel under the _Deployment Model_ label and you are done with this step.  
![Adding component][screenshot-8]  


* **Adding data about a system component**  
On the left, you see two forms: one for updating component's information and one for adding URL interfaces. First, for the sake of the example, rename the component identifier to _fixer_. Keep in mind that components' identifiers must be unique. Then for component's address update to **_api.fixer.io_**. This is the end point of the fixer API. By clicking **Update** you will see that the component's label is renamed to _fixer_. Now, add the URL interfaces that you want to use. Each URL interface must have a unique ID. For this example, add URL https://api.fixer.io:443/latest with ID _rest1_ and URL https://api.fixer.io:443/2000-01-03 with ID _rest2_. Note the specified port number for the URLs.
![Adding data about a component][screenshot-9]


* **Filling the transition's information**  
Back to the transition now, click on it. You should see a lot of form fields on the left for filling information about the transition.  
![Transition's information][screenshot-10]  
This is, basically, the information of the request you are sending to the chosen URL interface. First, let's choose a URL interface. From the URL pointer dropdown choose the URL interface ID you want to use. For this transition we will use the first one - **_component.fixer.rest1_**, which, as you can see below, points to the link https://api.fixer.io:443/latest. We configured that in the component's data.  
Then comes the resource path. For this example, we want to use _GBP_ as a base for the currency conversion. Hence, we should use this resource path - **?base=GBP**. This is the moment to use the global pattern data we set in the beginning. The format for using pattern data is the following - **$$patterndata.id$$** where _id_ is the ID of the pattern data we want to use. Hence, for resource path, we will write **?base=$$patterndata.base$$**.  
The pattern data format can also be generated by clicking right button on the resource path field and then choosing the _Insert pattern data_ option.  
For the request method, we will use **GET**, since we are retrieving information.  
The data type, as explained above, is **JSON**.  
We skip the message content because we do not need to pass any content for this API request.  
However, we should add a header for the content type. Using the form at the bottom, add a header **Content-Type** with value **application/json**.  
To make sure everything is updated, click the **Update Message** button at the bottom and you are done with this step.
![Adding transition's information][screenshot-11]  


* **Adding a trigger state**  
Trigger states are similar to Triggerstart states with the difference being that Trigger states can be a target of a transition and that you can have as many Trigger states as you want in your pattern. Let's add a Trigger state, which will be used to trigger another rest event.  
The icon being used for a **Trigger** node is:  
![Trigger node][trigger_node]  
On the top-left of the tool, switch back to the **Behaviour** tab. From the icons drag and drop the **Trigger** icon to the panel under the _Interoperability Behaviour Model_ label and you are done with this step.  
![Adding a trigger node][screenshot-12]  


* **Adding a guard transition**  
Now let's link the **Normal** node to the **Trigger** node. Thus, a **Guard** transition will be created. This is, basically, a transition, which evaluates the returned response from the last triggered event against a certain set of rules called guards. Click on the newly created transition. On the left, you should see the form for adding guards for the new transition.  
![Adding a guard transition][screenshot-13]  
The helpers contain information about the guard description and the guard value. Let's add a few guards for this transition.  
First I am going to set some rules for the HTTP headers.  
I want to test that the response is coming from the **Fixer** API. In the guard description type **HTTP.from**. We want the value to be api.fixer.io . However, instead of manually typing the address, we will use the shortcut format **component.id.address**. This gives us the address of the component with the given ID. Hence, for value type **component.fixer.address**. Now set the guard function to **equal** and then click the **Add guard** button.  
![Adding a guard rule][screenshot-14]  
I also want to test the status code of the response and the message type. Hence, add two more guard rules:  
first, with guard description **HTTP.code**, guard value **200** (we want only successful responses) and guard function **equal**,  
second, with guard description **HTTP.msg**, guard value **REPLY** and guard function **equal** again.  
![Adding more guards][screenshot-15]
In fact, we can also test the content of the returned response using XPath (for XML content) and JSONPath (for JSON content). For our example, lets test that the JSON response contains a key _rates_ (which should have the conversion rates for value) and that the value of the USD is greater than 0.  
When testing content we use the following format: **content[XPath/JSONPath]**.  
So for our first rule we will type **content[$]** for guard description (this is the JSONPath to take the whole content), for guard function we choose **contains** and for guard value we type **rates**.  
For the second rule we type **content[$.rates.USD]** for guard description (the JSONPath to the USD value), for guard function we choose **greaterthan** and for guard value we type **0**.  
Now we are done with this step.  
![Adding more guards][screenshot-16]


* **Adding a loop state**  
Loop states are states, which allow the repetition of a given event. This could be useful if you want, for example, to check if an API doesn't crash after a few consecutive calls.  
The icon being used for a **Loop** node is:  
![Loop node][loop_node]  
From the icons on the top left, drag and drop the **Loop** icon to the panel under the _Interoperability Behaviour Model_ label and you are done with this step.  
![Adding a loop node][screenshot-17]


* **Adding another transition**  
Now let's add a transition between the **Trigger** node and the new **Loop** node. Click on the transition and you should see the form for a **Message** transition - the one that triggers an event.  
![Adding another transition][screenshot-18]  
We would use pretty much the same details to fill this transition with the difference that this time we will use our second component URL interface (the one with id _rest2_).  
Hence, for a URL pointer choose the second URL interface. You will see that it's pointing to link https://api.fixer.io:443/2000-01-03.  
For resource path, use the following **?base=$$patterndata.base$$&symbols=[USD,EUR]** , which will return conversions only for USD and EUR.  
Method is still **GET**.  
The data type used is **JSON** again.  
Add the **Content-Type** header again and set it to **application/json**.  
Now we are done with this step.  
![Adding transition data][screenshot-19]  


* **Adding another normal state**  
Once more, we need a normal state to which to link the loop state. From the icons on the top left, drag and drop the **Normal** icon to the panel under the _Interoperability Behaviour Model_ label. Keep in mind that since you already have a node with label **normal**, you will be asked to choose a different label for the new node.  
![Adding another normal state][screenshot-20]  


* **Linking the loop state to the normal state**  
Now make a transition from the loop state to the new normal state. Since this is a loop state, the transition will be a message transition again. In this case, for the new transition, we have to fill the same data we filled for our last transition.  
Resource path - **?base=$$patterndata.base$$&symbols=[USD,EUR]**  
Method - **GET**  
Data type - **JSON**  
Headers - **Content-Type** with value **application/json**  
![Adding another transition][Screenshot-21]  


* **Linking back to the loop state**  
Since we use a loop state we have to link the normal state back to the loop state, too. This would be a guard transition, which will evaluate the response data against the set of rules. We should see the form for adding guard rules again.  
![Linking back to the loop state][screenshot-22]  


* **Adding new guard rules**  
Now let's add some guards for our new guard transition. I will add the same rules we used for the HTTP headers in our last guard transition.  
**HTTP.from** - **equal** - **component.fixer.address**  
**HTTP.code** - **equal** - **200**  
**HTTP.msg** - **equal** - **REPLY**  
For content rules, let's test again that the content contains a key _rates_, but also test that the values for both EUR and USD are less than 2.  
First, for guard description type **content[$]**, choose **contains** for guard function and for guard value type **rates**. Click **Add guard**.  
Then, for guard description type **content[$.rates.USD]**, choose **lessthan** for guard function and for guard value type **2**. Click **Add guard**.  
Finally, for guard description type **content[$.rates.EUR]**, choose **lessthan** for guard function and for guard value type **2**. Click **Add guard**.  
Now we are done with this step.  
![Adding new guards][screenshot-23]  


* **Adding an end state**  
End states are used to point where the interoperability test should end. You can have as many end states as you want. This is useful, since you can specify end states with different guard rules. For example, one of the end state would be if the content is in XML format and the other if the content is in JSON format.  
The icon being used for an **End** node is:  
![End node][end_node]  
From the icons on the top left, drag and drop the **End** icon to the panel under the _Interoperability Behaviour Model_ label and you are done with this step.  
![Adding end node][screenshot-24]  


* **Filling end state's data**  
When clicking on the end node, you should see a form on the left.  
The **success** dropdown lets you choose whether this end state should be treated as success or not. Set this to **true**.  
The **test report** is just data that you want to give if this end state is reached - an example is the reason this end state should be treated as success or not.  
These attributes are useful if you have more than one end state. For instance, you have an end state, which is reached only if authorization for some API failed and another if authorization was successful. Then you can set the success attribute for the failing end state to **false** and explain in the test report that the reason is **authorization failure** using JSON format, for example.  
For our case, just set the **success** attribute to **true** and leave the test report empty.  
![Adding end node data][screenshot-25]  


* **Breaking from the loop**  
In order to break from the loop we need to create a guard transition, which will include a **counter** guard function. So we create a transition from the **Loop** node to the **End** node. By clicking on it, we see the form for adding guards again.  
![Adding the last transition][screenshot-26]  
Here, we add only one guard rule, which is to break the loop after a number of iterations.  
For the guard description type **Index**, choose **counter** for guard function and for guard value type **5**. Then click **Add guard** and we are done with this step.  
![Adding counter guard][screenshot-27]  

#### Saving your interoperability model
The next step is to save our new pattern into an xml file. From the icons under the toolbar, click on the save icon. Then choose a location to save your model to.  
![Saving your pattern][screenshot-42]

#### Executing the interoperability test
So far we managed to create an interoperability pattern, which can be used for interoperability and compliance testing for the [**Fixer**](http://fixer.io) API. Now let's use this pattern and run the test.  
First, we need to verify that our pattern is correct. From the icons on the top of the tool (under the toolbar) click the green tick icon.  
![Pattern verification][screenshot-28]  
You should see a message whether your pattern is verified as correct. For our example, we should get a valid pattern message. If you get an error message, please go through the tutorial above and find out what's wrong.  
![Pattern verification message][screenshot-29]  
Now, in order to run the test, click the test icon next to the verification icon.  
![Run test icon][screenshot-30]  
You should see a dialog to choose the running mode: **execution mode** is the mode, which runs the test directly, while **step-by-step mode** waits the user to click on the arrow icon in order to continue to the next step.  
![Choosing running mode][screenshot-31]  
For now choose execution mode. You should see the **Test report panel** and a lot of text output. This is basically the interoperability report generating a message for all covered steps in the test and all evaluated guards. You should see the last message being **_End node reached --> Interoperability Testing Complete_**, which means that the **Fixer** API is compliant with the model we created and the test is successful.  
![Running the test][screenshot-32]  
Before showing you how to execute the test in step-by-step mode, I am going to introduce you to another feature of the tool - using previous states data. It is possible to use data returned in previous states as a guard value for another state.  
Click on the graph icon, which returns you to the graph view of the model  
![Graph view icon][screenshot-33]  
Now click on the guard transition from node with label **state** to node with label **loop**. You should see the form for adding guards on the left along with all the guards we added to this transition in one of the previous steps.  
![Guards][screenshot-34]  
Now, we are going to add a guard, which includes data from a previous state. For instance, we want to check if the value of the EUR returned by this response is less than the value of the NOK returned by the response of the first event captured in node **normal**.  
![captured data][screenshot-35]  
For guard description type **content[$.rates.EUR]** and choose **lessthan** for guard function. For guard value use the format for previous states data, which is **$$state_label|{content or headers}|XPath or JSONPath or header_id$$**. This could be generated for you by clicking right button on the guard value text field and choosing the _Insert previous states data_ option. For our example the state_label is **normal** (the previous data is captured there), we choose content since we want to extract information from the content of the response and the JSONPath would be **rates.NOK** (note that here we skip the **$.** part in the JSONPath)  
Thus, for guard value we type **$$normal|content|rates.NOK$$**. Now, click **Add guard**.  
![added guard][screenshot-36]  
Back to executing the test now. Click test icon again, but this time choose **step-by-step** mode.  
![step-by-step mode][screenshot-37]  
You should see the **Test report panel** again with a few output. Now, you have two optios, either stop the test by clicking the cross icon next to the run test icon or you can go to the next step in the test by clickng the right arrow icon.  
![step-by-step mode][screenshot-38]  
By clicking the right arrow icon enough times, you will get to the final state of the test, which should again be a successful message.  
![finish test][screenshot-39]  


#### Viewing previous test reports  
When running the test, you see the test report panel, which displays the report of the test you are currently running. However, there are cases when you want to view previously generated reports. In order to do that click the previous reports icon, which is the last one from the bunch of icons under the toolbar.  
![previous reports][screenshot-40]  
You should then see the previous reports panel, which shows all generated test reports by the tool. Currently, I have 2 reports since we executed the test twice - in execution mode and in step-by-step mode. Each report is displayed by clicking on its tab. The name of the tab shows the time the report was generated.  
![previous reports tabs][screenshot-41]  
By clicking on the save icon, you can save your report into a text file. By default the name of the text file will be in the format - **report-{time it was generated}.txt**. For example, **_report-08h59m03s.txt_**.  
If you've followed the tutorial and saved your pattern into an xml file, the report will be saved in the same folder as the pattern. Otherwise, you will have to choose the location to save the report.  
![saving a report][screenshot-43]  
If there is a file with the same name in the saving directory, you will be asked if you want to overwrite this file.  
![overwriting file][screenshot-44]  

### Other features

So far, you've explored the tool by creating an interoperability pattern, executing interoperability tests and viewing test reports. Now, I am going to guide you through some addition feautures of the tool.  

#### XML view of the pattern
The tool allows you to view the data of your model in xml format (the xml file that will be generated when you save your pattern will contain this data) and even partially modify it. Click on the XML view icon and then you should see the XML view panel.  
![XML view][screenshot-45]  
On the top you should see the **Enable pattern editing** button and the **Editing legend**, which I am going to explain in a moment. First, click on the **Enable pattern editing** button.  
![Editing XML pattern][screenshot-46]  
Now, you should see two new buttons: **Disable pattern editing** and **Update changes**. All changes made in the XML view will only be updated on the actual pattern if you click on **Update changes**. You should also see that there are highlighted XML tags with different colors.  
![Editing XML mode][screenshot-47]  
As explained in the legend, clicking on an light-orange tag will let you replace the text value. For instance, let's click on the **GBP** value and try to change it to something else.  
![Editing XML mode][screenshot-48]  
You should see a dialog asking you to type the new value. For this example, let's type **EUR** and then click **OK**.  
![Editing pattern data][screenshot-49]  
Now, the value should be changed to **EUR**.  
![Changed pattern data][screenshot-50]  
Click on the **Update changes** button so that the change is validated and updated. Now let's try to append data. Click on **Enable pattern editing** again. As explained in the legend, clicking on purple tag appends data. Let's click on the **patterndata** tag. You will be asked to confirm your choice, click **Yes**.
![Adding pattern data][screenshot-51]  
You should now see a dialog for filling the ID of the new pattern data.  
![id for pattern data][screenshot-52]  
For ID type **testID**. Then you will be asked to enter value for the new pattern data. Type **testValue** and click **OK**. You should now see that the new pattern data is appended in the XML.  
![appended pattern data][screenshot-53]  
Update the change again by clicking on **Update changes**. Now go back to the graph view and click on the **triggerstart** node. You should see that the pattern data is updated.  
![updated data][screenshot-54]  
For the sake of the example, let's now delete the newly added pattern data through XML editing. Go back to teh XML view, **Enable pattern editing** and click on the second **data** tag. It is red, which, as explained in the legend, means that it will be deleted on click.  
![delete data][screenshot-55]  
You will be asked to confirm that you want to delete the pattern data with ID **testID**. Click **Yes**. Then click on **Update changes** again. The second pattern data block will be removed.  
![deleted data][screenshot-56]  
    
**Keep in mind** that whenever you do a replace edition - you replace the text value of something - you should update the changes and then continue editing the XML view if appropriate.

#### XPath expression generator

There are many cases in which it is not easy to track and derive the full XPath for some tag in XML data. The XPath expression generator will do that for you. You just have to load an XML file and then click on the tag, for which you want to generate the XPath expression.  
To open the tool, go to the **Tools** option from the toolbar and choose **XPath expression generator**.  
![xpath generator][screenshot-57]  
You will have to choose your XML file. I chose the XML file of the pattern we created in the previous steps.  
![xpath generator][screenshot-58]  
You can click on the **Highlight tags** button for easier navigation. Now click on some tag, text or attribute. I will click on the url address for the **rest1** URL interface.  
![generating xpath][screenshot-59]  
You should now see the generated XPath expression and in a confirmation dialog asking whether you want to copy the XPath.  
![generating xpath][screenshot-60]  

#### JSONPath expression generator

[start_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event_end.png "Start node"
[triggerstart_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event_triggerstart.png "Triggerstart node"
[normal_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event.png "Normal node"
[trigger_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/link.png "Trigger node"
[loop_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/loop.png "Loop node"
[end_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/terminate.png "End node"
[screenshot-1]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-1.png "Start screenshot"
[screenshot-2]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-2.png "Drag screenshot"
[screenshot-3]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-3.png "Pattern data screenshot"
[screenshot-4]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-4.png "Add pattern data screenshot"
[screenshot-5]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-5.png "Dragging normal node screenshot"
[screenshot-6]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-6.png "Adding transition"
[screenshot-7]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-7.png "Deployment tab"
[screenshot-8]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-8.png "Adding component"
[screenshot-9]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-9.png "Adding data about a component"
[screenshot-10]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-10.png "Transition information"
[screenshot-11]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-11.png "Adding transition information"
[screenshot-12]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-12.png "Adding a trigger node"
[screenshot-13]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-13.png "Adding a guard trans"
[screenshot-14]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-14.png "Adding guard rule"
[screenshot-15]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-15.png "Adding more guards"
[screenshot-16]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-16.png "Adding more guards"
[screenshot-17]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-17.png "Adding a loop node"
[screenshot-18]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-18.png "Adding another transition"
[screenshot-19]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-19.png "Adding transition data"
[screenshot-20]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-20.png "Adding a normal node"
[screenshot-21]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-21.png "Adding another transition"
[screenshot-22]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-22.png "Linking back to the loop state"
[screenshot-23]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-23.png "Adding new guard rules"
[screenshot-24]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-24.png "Adding an end node"
[screenshot-25]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-25.png "Adding end node data"
[screenshot-26]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-26.png "Adding the last transition"
[screenshot-27]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-27.png "Adding counter guard"
[screenshot-28]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-28.png "Pattern verification"
[screenshot-29]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-29.png "Pattern verification message"
[screenshot-30]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-30.png "Run test icon"
[screenshot-31]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-31.png "Choosing running mode"
[screenshot-32]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-32.png "Running the test"
[screenshot-33]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-33.png "Graph view icon"
[screenshot-34]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-34.png "Guards"
[screenshot-35]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-35.png "captured data"
[screenshot-36]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-36.png "captured data"
[screenshot-37]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-37.png "step-by-step mode"
[screenshot-38]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-38.png "step-by-step mode"
[screenshot-39]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-39.png "finish test"
[screenshot-40]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-40.png "previous reports"
[screenshot-41]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-41.png "previous reports"
[screenshot-42]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-42.png "saving model"
[screenshot-43]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-43.png "saving a previous report"
[screenshot-44]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-44.png "overwriting file"
[screenshot-45]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-45.png "XML view"
[screenshot-46]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-46.png "XML view"
[screenshot-47]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-47.png "XML editing"
[screenshot-48]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-48.png "XML editing"
[screenshot-49]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-49.png "XML editing"
[screenshot-50]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-50.png "edited pattern data"
[screenshot-51]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-51.png "adding pattern data"
[screenshot-52]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-52.png "id for pattern data"
[screenshot-53]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-53.png "appended pattern data"
[screenshot-54]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-54.png "updated pattern data"
[screenshot-55]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-55.png "delete pattern data"
[screenshot-56]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-56.png "deleted pattern data"
[screenshot-57]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-57.png "xpath generator"
[screenshot-58]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-58.png "xpath generator"
[screenshot-59]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-59.png "xpath generator"
[screenshot-60]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/screenshot-60.png "xpath generator"