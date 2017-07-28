# connect-iot
A set of model driven engineering tools to engineer interoperable IoT and Web solutions 

## Brief Introduction
Two of the biggest challenges researches and developers of IoT systems face are the establishment of interoperability and the compliance with certain specifications. This is mainly due to the significantly high level of heterogeneity seen in the IoT sector. Typical examples are the use of different protocols (HTTP, MQTT, COAP), data formats (XML, JSON, etc) and communication technologies. To tame these challenges efficiently and accurately this project presents a **_Model-based Interoperability Testing Tool_**. The tool uses model-driven approaches to handle the issues mentioned above and ensure interopearbility and compliance are achieved even if the person using the tool is not an expert in this field of study. The tool offers the possibilities of creating, using and reusing _interoperability models_, also called _patterns_. Thus, the development complexity is considerably reduced by removing the burden of manually testing all interopearbility and specification requirements.

Read more at the following [article](https://link.springer.com/article/10.1007/s12243-015-0487-2).

## Install and Run

To run the editor tool, please execute the following:

```
mvn clean test
```