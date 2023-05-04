
# identity-management-service-stubs

Stub for Identity Management Service

## Authentication
An Authorization header must be specified to call the /identity endpoints in
this service. This should specify a clientId and secret in this format:
- Authorization: Basic base64Encoded(clientId:secret)

The clientId and secret values at time of writing are:
- clientId: idms-stub-client-id
- secret: idms-stub-secret 

The Authorization header to specify based on these values is:
- Authorization: Basic aWRtcy1zdHViLWNsaWVudC1pZDppZG1zLXN0dWItc2VjcmV0
