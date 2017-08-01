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
Pattern data is global data for which you assign IDs so that you can easily access it troughout the whole model. This data is assigned to the start node in the graph. In our case, we have a **Triggerstart** node. Hence, we will assign the pattern data to the node we created in the previous step. Click on the icon of the trigger start node we created. On the left, you see a form to add new node attributes (pattern data) and a table with all the pattern data this node has. We haven't added any pattern data, yet, so the table should be empty.  
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
I want to test that the responce is coming from the **Fixer** API. In the guard description type **HTTP.from**. We want the value to be api.fixer.io . However, instead of manually typing the address, we will use the shortcut format **component.id.address**. This gives us the address of the component with the given ID. Hence, for value type **component.fixer.address**. Now set the guard function to **equal** and then click the **Add guard** button.  
![Adding a guard rule][screenshot-14]  
I also want to test the status code of the responce and the message type. Hence, add two more guard rules:  
first, with guard description **HTTP.code**, guard value **200** (we want only successful responces) and guard function **equal**,  
second, with guard description **HTTP.msg**, guard value **REPLY** and guard function **equal** again.  
![Adding more guards][screenshot-15]
In fact, we can also test the content of the returned responce using XPath (for XML content) and JSONPath (for JSON content). For our example, lets test that the JSON responce contains a key _rates_ (which should have the conversion rates for value) and that the value of the USD is greater than 0.  
When testing content we use the following format: **content[XPath/JSONPath]**.  
So for our first rule we will type **content[$]** for guard description (this is the JSONPath to take the whole content), for guard function we choose **contains** and for guard value we type **rates**.  
For the second rule we type **content[$.rates.USD]** for guard description (the JSONPath to the USD value), for guard function we choose **greaterthan** and for guard value we type **0**.  
Now we are done with this step.  
![Adding more guards][screenshot-16]

[start_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event_end.png "Start node"
[triggerstart_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event_triggerstart.png "Triggerstart node"
[normal_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/event.png "Normal node"
[trigger_node]: https://iglab.it-innovation.soton.ac.uk/iot/connect-iot/raw/master/src/main/resources/images/link.png "Trigger node"
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