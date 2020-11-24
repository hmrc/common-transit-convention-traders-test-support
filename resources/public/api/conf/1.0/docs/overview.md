This API allows testers to inject departure and arrival movement notifications as if they have come from the office of departure and the office of destination by the New Computerised Transit System (NCTS).

To review our progress and to see what you can test, take a look at our [Roadmap](/roadmaps/common-transit-convention-traders-roadmap).


For more information about how to develop your own client applications, including example clients for this API, 
see [Tutorials](/api-documentation/docs/tutorials).

<br></br>

## How to get set up for testing

Follow these steps to get set up for testing.

1. Register for a [developer account](https://developer.service.hmrc.gov.uk/developer/registration).

2. Create an application.    

3. Subscribe to any API you might be working on. We suggest [Create Test User API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/api-platform-test-user/1.0) along with [Common Transit Convention Traders API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders/1.0) and Common Transit Convention Traders Test Support API (which is on this page).     

4. Create a [Client ID and Client Secret](https://developer.service.hmrc.gov.uk/api-documentation/docs/authorisation/credentials).

5. Use the [Create Test User API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/api-platform-test-user/1.0) to get a user ID, password, EORI enrolment and other test details. 

<br></br>

## How to use the CTC Trader Test Support API

You can use this API for any test scenario you choose. It is not connected to the NCTS core. It’s up to you to trigger the response messages in the order you require.  Valid response message types are listed below.

1.  Send messages from a trader to the NCTS using the [CTC Traders API](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders/1.0). This could be IE014, IE015, IE007 or IE044. See [message we support](https://developer.service.hmrc.gov.uk/guides/common-transit-convention-traders-service-guide/documentation/supported-messages.html) for more details. 

2. Inject a response using the Common Transit Traders Test Support API. See below. 

3. GET a list of your messages for this movement from the CTC Traders API. This will include both your trader and your injected NCTS messages.

<br></br>

## Messages you can inject now

<table>
   <tbody>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an Arrival Notification rejection message (IE008)</a></td>
   <td>Inject a message from the office of destination rejecting the traders Arrival Notification (IE007)</td>
         <td>Specify "IE008" for the <code>messageType</code> field</td>
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an unloading permission message (IE043)</a></td>
   <td>Inject an unloading permission message to the trader at destination</td>
   <td>Specify "IE043" for the <code>messageType</code> field
    </tr>
    <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject a write-off notification (IE045)</a></td>
   <td>Inject a message from the office at departure that the transit movement has been discharged</td>
   <td>Specify "IE045" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an unloading remarks rejection message (IE058)</a></td>
   <td>Inject a message from the office at destination that there are errors in the trader's unloading remarks message (IE044)</td>
   <td>Specify "IE058" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject declaration received message (IE928) (IE028)</a></td>
   <td>Inject a positive acknowledgement of a departure declaration message (IE015)</td>
   <td>Specify "IE928" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject an MRN allocated message (IE028)</a></td>
   <td>Inject a message from the office at departure allocating a Movement Reference Number (MRN)</td>
   <td>Specify "IE028" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a goods released for transit message (IE029)</a></td>
   <td>Inject a message from the office at departure to say the goods are released for transit</td>
   <td>Specify "IE029" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a declaration rejection message (IE016)</a></td>
   <td>Inject a message from the office at departure rejecting a declaration data message (IE015)</td>
   <td>Specify "IE016" for the <code>messageType</code> field
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a no release for transit message (IE051)</a></td>
   <td>Inject a message from the office at departure that the movement cannot be released for transit</td>
   <td>Specify "IE051" for the <code>messageType</code>field</td>
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a guarantee not valid message (IE055)</a></td>
   <td>Inject a message from the office of departure to the trader at departure that their guarantee is not valid</td>
   <td>Specify "IE055" for the <code>messageType</code>field</td>
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a control decision notification (IE060)</a></td>
   <td>Inject a message from the office at departure to tell the trader they wish to carry out a control of the goods</td>
   <td>Specify "IE060" for the <code>messageType</code>field</td>
   </tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/1.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject a good release notification (IE025)</a></td>
   <td>Inject a message from the office at destination that the trader can release the goods</td>
   <td>Specify "IE025" for the <code>messageType</code>field</td>
   </tr>
   </tbody>
   </table>

<br></br>
          
## Reference materials

### CURL commands

Use these CURL commands to simulate your application’s actions and messages, plus the actions and messages that would come from the NCTS.


<details>
   <summary><strong>See IE015 CURL command</strong></summary>

```
curl --location --request POST 'https://test-api.service.hmrc.gov.uk/customs/transits/movements/departures' \
--header 'Authorization: Bearer <enter your Bearer Token>' \
--header 'Content-Type: application/xml' \
--data-raw '<CC015B>
    <SynIdeMES1>UNOC</SynIdeMES1>
    <SynVerNumMES2>3</SynVerNumMES2>
    <MesRecMES6>NCTS</MesRecMES6>
    <DatOfPreMES9>20201117</DatOfPreMES9>
    <TimOfPreMES10>0935</TimOfPreMES10>
    <IntConRefMES11>25973103497074</IntConRefMES11>
    <AppRefMES14>NCTS</AppRefMES14>
    <MesIdeMES19>1</MesIdeMES19>
    <MesTypMES20>GB015B</MesTypMES20>
    <HEAHEA>
        <RefNumHEA4>TRATESTDEC112011170935</RefNumHEA4>
        <TypOfDecHEA24>T1</TypOfDecHEA24>
        <CouOfDesCodHEA30>IT</CouOfDesCodHEA30>
        <AutLocOfGooCodHEA41>954131533-GB60DEP</AutLocOfGooCodHEA41>
        <CouOfDisCodHEA55>GB</CouOfDisCodHEA55>
        <TraModAtBorHEA76>3</TraModAtBorHEA76>
        <IdeOfMeaOfTraCroHEA85>NC15 REG</IdeOfMeaOfTraCroHEA85>
        <NatOfMeaOfTraCroHEA87>GB</NatOfMeaOfTraCroHEA87>
        <ConIndHEA96>0</ConIndHEA96>
        <NCTSAccDocHEA601LNG>EN</NCTSAccDocHEA601LNG>
        <TotNumOfIteHEA305>1</TotNumOfIteHEA305>
        <TotNumOfPacHEA306>10</TotNumOfPacHEA306>
        <TotGroMasHEA307>1000</TotGroMasHEA307>
        <DecDatHEA383>20201117</DecDatHEA383>
        <DecPlaHEA394>Dover</DecPlaHEA394>
        <SpeCirIndHEA1>C</SpeCirIndHEA1>
        <ComRefNumHEA>HQDOV018</ComRefNumHEA>
        <SecHEA358>1</SecHEA358>
        <CodPlUnHEA357>MONOPOLI001</CodPlUnHEA357>
    </HEAHEA>
    <TRAPRIPC1>
        <NamPC17>NCTS UK TEST LAB HMCE</NamPC17>
        <StrAndNumPC122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumPC122>
        <PosCodPC123>SS99 1AA</PosCodPC123>
        <CitPC124>SOUTHEND-ON-SEA, ESSEX</CitPC124>
        <CouPC125>GB</CouPC125>
        <TINPC159>GB954131533000</TINPC159>
    </TRAPRIPC1>
    <TRACONCO1>
        <NamCO17>NCTS UK TEST LAB HMCE</NamCO17>
        <StrAndNumCO122>11TH FLOOR, ALEX HOUSE, VICTORIA AV</StrAndNumCO122>
        <PosCodCO123>SS99 1AA</PosCodCO123>
        <CitCO124>SOUTHEND-ON-SEA, ESSEX</CitCO124>
        <CouCO125>GB</CouCO125>
        <TINCO159>GB954131533000</TINCO159>
    </TRACONCO1>
    <TRACONCE1>
        <NamCE17>NCTS UK TEST LAB HMCE</NamCE17>
        <StrAndNumCE122>ITALIAN OFFICE</StrAndNumCE122>
        <PosCodCE123>IT99 1IT</PosCodCE123>
        <CitCE124>MILAN</CitCE124>
        <CouCE125>IT</CouCE125>
        <TINCE159>IT11ITALIANC11</TINCE159>
    </TRACONCE1>
    <CUSOFFDEPEPT>
        <RefNumEPT1>GB000060</RefNumEPT1>
    </CUSOFFDEPEPT>
    <CUSOFFTRARNS>
        <RefNumRNS1>FR001260</RefNumRNS1>
        <ArrTimTRACUS085>202011190935</ArrTimTRACUS085>
    </CUSOFFTRARNS>
    <CUSOFFDESEST>
        <RefNumEST1>IT018105</RefNumEST1>
    </CUSOFFDESEST>
    <CONRESERS>
         <ConResCodERS16>A3</ConResCodERS16>
         <DatLimERS69>20201125</DatLimERS69>
    </CONRESERS>
    <SEAINFSLI>
        <SeaNumSLI2>1</SeaNumSLI2>
        <SEAIDSID>
            <SeaIdeSID1>NCTS001</SeaIdeSID1>
        </SEAIDSID>
    </SEAINFSLI>
    <GUAGUA>
        <GuaTypGUA1>1</GuaTypGUA1>
        <GUAREFREF>
            <GuaRefNumGRNREF1>09GB00000100000M0</GuaRefNumGRNREF1>
            <AccCodREF6>AC01</AccCodREF6>
        </GUAREFREF>
    </GUAGUA>
    <GOOITEGDS>
        <IteNumGDS7>1</IteNumGDS7>
        <GooDesGDS23>Daffodils</GooDesGDS23>
        <GooDesGDS23LNG>EN</GooDesGDS23LNG>
        <GroMasGDS46>1000</GroMasGDS46>
        <NetMasGDS48>950</NetMasGDS48>
        <PACGS2>
            <MarNumOfPacGS21>AB234</MarNumOfPacGS21>
            <KinOfPacGS23>BX</KinOfPacGS23>
            <NumOfPacGS24>10</NumOfPacGS24>
        </PACGS2>
        <TRACORSECGOO021>
            <TINTRACORSECGOO028>GB954131533000</TINTRACORSECGOO028>
        </TRACORSECGOO021>
        <TRACONSECGOO013>
            <TINTRACONSECGOO020>IT17THEBOSS42</TINTRACONSECGOO020>
        </TRACONSECGOO013>
    </GOOITEGDS>
    <ITI>
        <CouOfRouCodITI1>GB</CouOfRouCodITI1>
    </ITI>
    <CARTRA100>
        <TINCARTRA254>GB954131533000</TINCARTRA254>
    </CARTRA100>
</CC015B>'
```
</details>


<details>
  <summary><strong>See IE016 CURL command</strong></summary>
   
```
curl --location --request POST 'https://test-api.service.hmrc.gov.uk/test/customs/transits/movements/departures/{deptId}/messages' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <enter your Bearer Token>' \
--data-raw '{
     "message": {
         "messageType": "IE016"
     }
 }'
 ```
</details>


<details>
   <summary><strong>See Get Dept Id CURL command</strong></summary>

```
curl --location --request GET 'https://test-api.service.hmrc.gov.uk/customs/transits/movements/departures/{deptId}' \
--header 'Accept: application/vnd.hmrc.1.0+json' \
--header 'Authorization: Bearer <enter your Bearer Token>'
```
</details>

### [Our Postman test scripts](https://github.com/hmrc/common-transit-convention-traders-postman) 

These were created by us in November 2020. These will not be monitored or updated.


### [Our XSD files](https://developer.service.hmrc.gov.uk/guides/common-transit-convention-traders-service-guide/documentation/xsd-reference.html) 

Use these to validate your XML. Please note, there are some known omissions. These are all captured in the mapping document. Also NumOfLoaLisHEA304 has been included in error in IE015 (CC015B), IE029 (CC0029B), IE043 (CC0043A) and IE051 (CC051), but you can ignore this. 

### [Mapping document](/docs/xml-2-edifact-mapping-updated12112020.pdf)

This has plain English header descriptions, XML and EDIFACT code. 

### [Trader Test Pack for GB](https://assets.publishing.service.gov.uk/government/uploads/system/uploads/attachment_data/file/937032/NCTS_4_GB.pdf) 

This contains test scenarios that you can use. 
      
### [NCTS API channel specifications](https://www.gov.uk/government/publications/new-computerised-transit-system-technical-specifications) 

See a list of messages, message content and sequence diagrams.  
> Note, this is NOT the specifications document for the new CTC Traders API. For example, the CTC Traders API will not use an EDIFACT wrapper or SOAP. A new version will be issued early in 2021.

