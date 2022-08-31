Here is the list error codes that we will keep updating.
We use standard HTTP status codes to show whether an API request has succeeded or not: 

**POST**

400 BadRequest: If JSON message is invalid

401 Unauthorized: If client passes invalid auth credentials

403 Forbidden: If supplied auth token doesn't contain valid enrolment

404 Not Found: If no object with specified ID found in database

415 Unsupported Media Type: If the client specified an invalid ``Content-Type`` header

500 Internal Server Error: If exception in code occurs

501 Not Implemented: If user attempts to ``POST`` a message and the message type isn't currently supported




Errors specific to each API are shown in the Endpoints section, under Response. 
See our [reference guide](/api-documentation/docs/reference-guide#errors) for more on errors.
