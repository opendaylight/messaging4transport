Messaging4Transport
========
Message Oriented Middleware Bindings for MD-SAL

### Description  ###

The OpenDaylight controller is based on an MD-SAL allows the modeling of data, RPCs, and notifications. Because of this model basis, adding new northbound bindings northbound bindings to the controller is simple, and everything modeled becomes exposed automatically. Currently the MD-SAL has restconf NB bindings, where more bindings such as AMQP and XMPP can easily be implemented and integrated. 

AMQP is an open standard application layer protocol for message-oriented middleware. This project adds AMQP bindings to the MD-SAL, which would automatically make all MD-SAL APIs available via that mechanism. AMQP bindings integration is built as an independent Karaf feature, that would expose the MD-SAL datatree, rpcs, and notifications via AMQP, when installed. Many implementations of AMQP exists. ActiveMQ is a popular open source messaging and Integration Patterns server, which offers an implementation of AMQP. Initial implementation of AMQP bindings are developed and tested with ActiveMQ-5.9.0.

### Scope  ###

* Developing Bindings for OpenDaylight MD-SAL to integrate with Message-Oriented Middleware.
* Design and Implementation of Advanced Message Queuing Protocol (AMQP)  bindings.
* Potential extension points for bindings for other protocols such as STOMP, MQTT, and OpenWire.

The project does not affect any other existing projects of OpenDaylight.
