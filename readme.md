This is a Restful microservice  for managing snippets of textual data with exipry age.

## Design Decision
I used a Model-Controller approach with an in-memory database(Conconrrent HashMap). Concurrent Hashmap is thread-safe for both read and update.

## Technology
Java with Javaspark micro service framework. Javaspark a very minimalistic code to enable endpoint.

The data is model as Snippet object while incoming data is treated as unstructured thereby defaulting to Map; The endpoints are designed using appropriate http verbs

## Second Use case
For time constraint, the password-based editing is chosen as extention since it does not require IO or further computation.

In production, No-SQL db like Redis/Mongo will work well to replace the in-memory db as well
as caching. Rate limit might also be considered.