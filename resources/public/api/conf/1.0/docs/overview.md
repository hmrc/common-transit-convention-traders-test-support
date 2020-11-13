This API allows testers to inject departure and arrival movement notifications as if they came from the Office of Departure and Office of Destination via the New Computerised Transit System (NCTS).

To review our progress and to see what you can test, take at look at our [Roadmap](/roadmaps/common-transit-convention-traders-roadmap).


For more information about how to develop your own client applications, including example clients for this API, 
see [Tutorials](/api-documentation/docs/tutorials).

## How to test your software

Follow these steps to get set up for testing.

1. Register for a [developer account](https://developer.service.hmrc.gov.uk/developer/registration).

2. Create an application.    

3. Subscribe to any API you might be working on. We suggest Create Test User API, CTC Traders API and CTC Traders Test Support API.     

4. Create a [Client ID and Client Secret](https://developer.service.hmrc.gov.uk/api-documentation/docs/authorisation/credentials).

5. Use the Test User API to get test ID, password and EORI enrolment.   

6. Apply the ID, password and EORI enrolment to: 
  * [Our postman scripts](https://github.com/hmrc/common-transit-convention-traders-postman). These were created by us in November 2020 and may not be viable.
  * Your own code. [See our mapping document](www.notreadyyet.com) for reference.   

7. Apply scripts to the endpoints below.    

### For further reference

**[Look at our XSD files](documentation/xsd-reference.html)** so you can validate your XML. 

**Check the NCTS API channel specifications**

Visit [NCTS Phase 4 Technical Interface Specifications (TIS)](https://www.gov.uk/government/publications/new-computerised-transit-system-technical-specifications) to see a list of messages, message content and sequence diagrams .

Note, this is NOT the specifications document for the new CTC Traders API. So, there will be discrepancies. For example, the CTC Traders API will not use an EDIFACT wrapper or SOAP.

