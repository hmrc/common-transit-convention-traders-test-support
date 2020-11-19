This API allows testers to inject departure and arrival movement notifications as if they have come from the office of departure and the office of destination by the New Computerised Transit System (NCTS).

To review our progress and to see what you can test, take at look at our [Roadmap](/roadmaps/common-transit-convention-traders-roadmap).


For more information about how to develop your own client applications, including example clients for this API, 
see [Tutorials](/api-documentation/docs/tutorials).

## How to get set up for testing

Follow these steps to get set up for testing.

1. Register for a [developer account](https://developer.service.hmrc.gov.uk/developer/registration).

2. Create an application.    

3. Subscribe to any API you might be working on. We suggest [Create Test User API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/api-platform-test-user/1.0) along with [Common Transit Convention Traders API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders/1.0) and Common Transit Convention Traders Test Support API (which is on this page).     

4. Create a [Client ID and Client Secret](https://developer.service.hmrc.gov.uk/api-documentation/docs/authorisation/credentials).

5. Use the [Create Test User API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/api-platform-test-user/1.0) to get a user ID, password, EORI enrolment and other test details. 


## How to use the CTC Trader Test Support API

You can can use this API for any test scenario you choose. It is not connected to the NCTS core. It’s up to you to trigger the response messages in the order you require.  Valid response message types are listed below.

1.  Send message from a trader to the NCTS using the the [CTC Traders API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders/1.0). This could be IE014, IE015, IE007 or IE044. See [message we support](https://developer.service.hmrc.gov.uk/guides/common-transit-convention-traders-service-guide/documentation/supported-messages.html) for more details. 

2. Inject an NCTS response using the Common Transit Traders Test Support API.

3. GET messages for this movement from the CTC Traders API. This will include both the trader and the NCTS messages.



## Response messages you can inject

| **Title** | **Description** |**Instruction**
|------|-------------|----|
|**[Inject an Arrival Notification rejection message (IE008)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion)**|Inject a message from the office of destination rejecting the traders Arrival Notification (IE007) | Specify "IE008" for the `messageType` field |
|**[Inject an unloading permission message (IE043)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion)**| Inject an unloading permission message to the trader at destination | Specify "IE043" for the `messageType` field |
|**[Inject a write-off notification (IE045)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure that the transit movement has been discharged. |Specify "IE045" for the `messageType` field. |
|**[Inject an unloading remarks rejection message (IE058)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion)**| Inject a message from the office at destination that there are errors in the trader's unloading remarks message (IE044) | Specify "IE058" for the `messageType` field |
|**[Inject declaration received message (IE928)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a positive acknowledgement of a departure declaration message (IE015) | Specify "IE928" for the `messageType` field |
|**[Inject an MRN allocated message (IE028)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure allocating a Movement Reference Number (MRN)| Specify "IE028" for the `messageType` field |
|**[Inject a goods released for transit message (IE029)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure to say the goods are released for transit | Specify "IE029" for the `messageType` field |
|**[Inject a cancellation decision message (IE009)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure in reference to a cancellation request (IE014)| Specify "IE009" for the `messageType` field |
|**[Inject a Declaration rejection message (IE016)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure rejecting a Declaration data message (IE015)| Specify "IE016" for the `messageType` field |
|**[Inject a no release for transit message (IE051)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure that the movement cannot be released for transit | Specify "IE051" for the `messageType` field |
|**[Inject a guarantee not valid message (IE055)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office of departure to the trader at departure that their guarantee is not valid | Specify "IE055" for the `messageType` field |
|**[Inject a control decision notification (IE060)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion)**| Inject a message from the office at departure to tell the trader they wish to carry out a control of the goods | Specify "IE060" for the `messageType` field |
|**[Inject a good release notification (IE025)](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion)**| Inject a message from the office at destination that the trader can release the goods | Specify "IE025" for the `messageType` field |

## Reference materials

**[Our Postman test scripts](https://github.com/hmrc/common-transit-convention-traders-postman)**. These were created by us in November 2020. These will not be monitored or updated.

**[Our XSD files](https://developer.service.hmrc.gov.uk/guides/common-transit-convention-traders-service-guide/documentation/xsd-reference.html)** so you can validate your XML. Please note, there are some known errors and omissions. These are all captured in the mapping document.

**[Mapping document](/docs/xml-2-edifact-mapping-updated12112020.pdf)**. This has plain English header descriptions, XML and EDIFACT code. 

**[Trader Test Pack](https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/782472/NCTS_8_trader_test_pack_version_6.3.2.pdf)**. This contains test scenarios that you can use. 
      
**[NCTS API channel specifications](https://www.gov.uk/government/publications/new-computerised-transit-system-technical-specifications)** to see a list of messages, message content and sequence diagrams.  
> Note, this is NOT the specifications document for the new CTC Traders API. For example, the CTC Traders API will not use an EDIFACT wrapper or SOAP. An new version will be issued early in 2021.

