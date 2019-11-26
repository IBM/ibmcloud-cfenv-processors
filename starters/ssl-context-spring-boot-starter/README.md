## Test project for creating / adding SSL contexts. 
Various services require ssl connections, and make their trusted cert available as Base64 encoded data.

Although most of the connection can be configured and left to other starters, often there is no way to 
configure trusted certs for a service via a property, it is either assumed the user has handled this via
the system (or other appropriate) truststore, or that they will tailor the ssl configuration manually in the
app to configure any ssl context or ssl socket factory required.

This project is an attempt to provide an extensible way for services to support trusted ssl connections
via certificate information from application properties. 

This project looks for configuration as follows:

- sslcontext.enabled - true/false, to enable or disable the effects of this starter.
- sslcontext.contexts.{ctxid}.trustedcert - Base64 Encoded trust certificate, to be used for the context with id 'ctxid'.

These properties could be set in application.properties with content from the environment (say via configmaps in k8s or similar).
Or could be set via cfenv processors (when running in CF environments).
