# ModelInterop
ModelInterop is a model-based interoperability testing tool. That is, it provides
a set of model driven engineering tools to perform both interface and interoperability 
testing of Internet of Thigs and Web products and platforms. The tool can be used 
to test that systems comply with different standard specifications e.g. OneM2M, or
API specifications. The tool can also be used to monitor and test that two
independently developed systems interoperate correctly with one another.

## A Brief Introduction
Two of the biggest challenges researches and developers of IoT systems face are:
* the establishment of interoperability between IoT systems;
* and the compliance of systems and products with current technical specifications and standards.

This is mainly due to the significantly high level of heterogeneity seen in the IoT 
sector. Typical examples are the use of different protocols (HTTP, MQTT, COAP), 
data formats (XML, JSON, etc) and communication technologies. To tame these 
challenges efficiently and accurately this project provides a **_Model-based Interoperability Testing Tool_**. 
The tool uses model-driven approaches to handle the issues mentioned above and 
ensure interoperability and compliance are achieved even if the person using the 
tool is not an expert in the areas of IoT interoperability and testing. 

The tool offers the possibilities of creating, using and reusing _interoperability models_. 
Thus, the development complexity is considerably reduced by removing the burden of manually testing all interoperability and specification requirements.

Read more at the following [article](https://link.springer.com/article/10.1007/s12243-015-0487-2).

## Install and Run

The tool is made available as a maven project. In order to build the tool, Java 1.8 or higher must be installed on your machine.

To install the tool. Simply download the zip file for the git repository, extract and go to the root folder location. Then type at the command prompt:

```
mvn  install
```

To run the tool at any time, type at the command prompt:

```
mvn  test
```

## User Guides

The following are a list of further documents in order to use the tool to develop different types of interoperability tests.
* [How to create a compliance test with the tool](docs/userguide.md)
* [How to create an interoperability test with the tool](docs/interop.md) (Tutorial in development)
* [COAP testing](docs/coap.md) (Tutorial in development)

