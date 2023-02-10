
Use the CTC Traders Test Support API to inject departure and arrival movement notifications as if they have come from a customs office of departure or destination through the New Computerised Transit System (NCTS).

You should read:

- [Roadmap](/roadmaps/common-transit-convention-traders-roadmap/) to review our progress and to learn about what you can test
- [Tutorials](/api-documentation/docs/tutorials) to learn how to develop your own client applications, including example clients, for this API

### Changelog

You can find the changelog in the [common-transit-convention-traders-test-support](https://github.com/hmrc/common-transit-convention-traders-test-support/wiki/CTC-Traders-Test-Support-API-v2.0-changelog) GitHub wiki.

### How to get set up for testing

Follow these steps:

1. Register for a [developer account](/developer/registration).

2. Create an application.

3. Subscribe to any API you might be working on. We suggest the [Create Test User API](/api-documentation/docs/api/service/api-platform-test-user/1.0), the [CTC Traders API](/api-documentation/docs/api/service/common-transit-convention-traders/1.0) and this API.

4. Create a [Client ID and Client Secret](/api-documentation/docs/authorisation/credentials).

5. Use the [Create Test User API](/api-documentation/docs/api/service/api-platform-test-user/1.0) to get a user ID, password, EORI enrolment and other test details.

<!--For more information about how to get set up for testing, see [CTC Traders API Testing guide](/guides/ctc-traders-phase5-testing-guide).-->

### How to use this API

This API is not connected to the NCTS core, so you can use it for any test scenario you choose.

It’s up to you to trigger the response messages in the order you require. Valid response message types are listed below.

Follow these steps:

1. Use the [CTC Traders API](/api-documentation/docs/api/service/common-transit-convention-traders/2.0) to send messages from a trader to the NCTS.

2. Use this API to inject a response. See the table below for a list of supported messages that you can inject.

3. Use the CTC Traders API to retrieve a list of your messages for this movement. This will include both your trader and your injected NCTS messages.

### Messages you can inject now

<table>
	<tr>
		<th>Title</th>
		<th>Description</th>
		<th>Instruction</th>
	</tr>
<!--
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/2.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an Arrival Notification rejection message (IE008)</a></td>
   <td>Inject a message from the office of destination rejecting the traders Arrival Notification (IE007)</td>
         <td>Specify "IE008" for the <code>messageType</code> field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/2.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an unloading permission message (IE043)</a></td>
   <td>Inject an unloading permission message to the trader at destination</td>
   <td>Specify "IE043" for the <code>messageType</code> field
    </tr>
    <tr>
    <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/2.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject a write-off notification (IE045)</a></td>
   <td>Inject a message from the office at departure that the transit movement has been discharged</td>
   <td>Specify "IE045" for the <code>messageType</code> field
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/2.0#_inject-a-fake-ncts-arrival-message_post_accordion">Inject an unloading remarks rejection message (IE058)</a></td>
   <td>Inject a message from the office at destination that there are errors in the trader's unloading remarks message (IE044)</td>
   <td>Specify "IE058" for the <code>messageType</code> field
   </tr>
-->
    <tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie004-amendment-acceptance">Inject an amendment acceptance message (IE004)</a></td>
	<td>Inject an amendment acceptance message from the office of departure (IE004)</td>
	<td>Specify "IE004" for the <code>messageType</code> field
	</tr>
    <tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie009-invalidation-decision">Inject an invalidation decision message (IE009)</a></td>
	<td>Inject an invalidation decision message to be sent to the holder of the transit procedure (IE009)</td>
	<td>Specify "IE009" for the <code>messageType</code> field
	</tr>
	<tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie928-positive-acknowledge">Inject declaration received message (IE928)</a></td>
	<td>Inject a positive acknowledgement of a departure declaration message (IE015)</td>
	<td>Specify "IE928" for the <code>messageType</code> field
	</tr>
    <tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie025-goods-release-notification">Inject a goods release notification (IE025)</a></td>
   <td>Inject a message from the office at destination that the trader can release the goods</td>
   <td>Specify "IE025" for the <code>messageType</code>field</td>
   </tr>
	<tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie028-mrn-allocated">Inject an MRN allocated message (IE028)</a></td>
	<td>Inject a message from the office at departure allocating a Movement Reference Number (MRN)</td>
	<td>Specify "IE028" for the <code>messageType</code> field
	</tr>
   <tr>
	<td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie029-release-for-transit">Inject a goods released for transit message (IE029)</a></td>
   <td>Inject a message from the office at departure to say the goods are released for transit</td>
   <td>Specify "IE029" for the <code>messageType</code> field
   </tr>
<!--
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/common-transit-convention-traders-test-support/2.0#_inject-a-fake-ncts-departure-message_post_accordion">Inject a declaration rejection message (IE016)</a></td>
   <td>Inject a message from the office at departure rejecting a declaration data message (IE015)</td>
   <td>Specify "IE016" for the <code>messageType</code> field
   </tr>
-->
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie060-control-decision-notification">Inject a control decision notification (IE060)</a></td>
   <td>Inject a message from the office at departure to tell the trader they wish to carry out a control of the goods</td>
   <td>Specify "IE060" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie043-unloading-permission">Inject an unloading permission (IE043)</a></td>
   <td>Inject an unloading permission message to inform the trader that they are authorised to unload the goods at the authorised place</td>
   <td>Specify "IE043" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie056-rejection-from-office-of-departure">Inject a rejection from office of departure (IE056)</a></td>
   <td>Inject a rejection from office of departure message to inform the trader that the office of departure has rejected the message</td>
   <td>Specify "IE056" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie057-rejection-from-office-of-destination">Inject a rejection from office of destination (IE057)</a></td>
   <td>Inject a rejection from office of destination message to inform the trader that the office of destination has rejected the message</td>
   <td>Specify "IE057" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie140-request-on-non-arrived-movement">Inject a request on non-arrived movement (IE140)</a></td>
   <td>Inject a non-arrived movement message to the trader indicating that there is insufficient information to discharge the transit</td>
   <td>Specify "IE140" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie035-recovery-notification">Inject a request on recovery notification (IE035)</a></td>
   <td>Inject a recovery notification from office of departure to inform the trader about recovery</td>
   <td>Specify "IE035" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie051-no-release-for-transit">Inject a no release for transit message (IE051)</a></td>
   <td>IInject a message from the office at departure that the movement cannot be released for transit</td>
   <td>Specify "IE051" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie055-guarantee-not-valid">Inject a guarantee not valid message (IE055)</a></td>
   <td>Inject a message from the office of departure to the trader at departure that their guarantee is not valid</td>
   <td>Specify "IE055" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie045-write-off-notification">Inject a Write-Off notification message (IE045)</a></td>
   <td>Inject a message from the office at departure that the transit movement has been discharged</td>
   <td>Specify "IE045" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie019-discrepancies">Inject a Discrepancies message (IE019)</a></td>
   <td>Inject a message from the office at departure that the transit movement has major discrepancies</td>
   <td>Specify "IE019" for the <code>messageType</code>field</td>
   </tr>
   <tr>
   <td><a href="https://developer.service.hmrc.gov.uk/guides/ctc-traders-phase5-tis/documentation/messagetypes.html#ie182-forwarded-incident-notification-to-ed">Inject a Forwarded Incident Notification To ED message (IE182)</a></td>
   <td>Inject a message from the office at departure that the transit movement require investigation by a custom officer</td>
   <td>Specify "IE182" for the <code>messageType</code>field</td>
   </tr>

</table>

### Reference materials

<!--### CURL commands

Use these CURL commands to simulate your application’s actions and messages, plus the actions and messages that would come back from NCTS.

<details>
   <summary><strong>See IE015 CURL command</strong></summary>

<pre>
curl --location --request POST &apos;https://test-api.service.hmrc.gov.uk/customs/transits/movements/departures&apos; \
--header &apos;Authorization: Bearer &lt;Enter your Bearer Token&gt;&apos; \
--header &apos;Content-Type: application/xml&apos; \
--data-raw \
&quot;
&lt;CC015B&gt;
    &lt;SynIdeMES1&gt;UNOC&lt;/SynIdeMES1&gt;
    &lt;SynVerNumMES2&gt;3&lt;/SynVerNumMES2&gt;
    &lt;MesRecMES6&gt;NCTS&lt;/MesRecMES6&gt;
    &lt;DatOfPreMES9&gt;20201117&lt;/DatOfPreMES9&gt;
    &lt;TimOfPreMES10&gt;0935&lt;/TimOfPreMES10&gt;
    &lt;IntConRefMES11&gt;25973103497074&lt;/IntConRefMES11&gt;
    &lt;AppRefMES14&gt;NCTS&lt;/AppRefMES14&gt;
    &lt;MesIdeMES19&gt;1&lt;/MesIdeMES19&gt;
    &lt;MesTypMES20&gt;GB015B&lt;/MesTypMES20&gt;
    &lt;HEAHEA&gt;
        &lt;RefNumHEA4&gt;TRATESTDEC112011170935&lt;/RefNumHEA4&gt;
        &lt;TypOfDecHEA24&gt;T1&lt;/TypOfDecHEA24&gt;
        &lt;CouOfDesCodHEA30&gt;IT&lt;/CouOfDesCodHEA30&gt;
        &lt;AutLocOfGooCodHEA41&gt;954131533-GB60DEP&lt;/AutLocOfGooCodHEA41&gt;
        &lt;CouOfDisCodHEA55&gt;GB&lt;/CouOfDisCodHEA55&gt;
        &lt;TraModAtBorHEA76&gt;3&lt;/TraModAtBorHEA76&gt;
        &lt;IdeOfMeaOfTraCroHEA85&gt;NC15 REG&lt;/IdeOfMeaOfTraCroHEA85&gt;
        &lt;NatOfMeaOfTraCroHEA87&gt;GB&lt;/NatOfMeaOfTraCroHEA87&gt;
        &lt;ConIndHEA96&gt;0&lt;/ConIndHEA96&gt;
        &lt;NCTSAccDocHEA601LNG&gt;EN&lt;/NCTSAccDocHEA601LNG&gt;
        &lt;TotNumOfIteHEA305&gt;1&lt;/TotNumOfIteHEA305&gt;
        &lt;TotNumOfPacHEA306&gt;10&lt;/TotNumOfPacHEA306&gt;
        &lt;TotGroMasHEA307&gt;1000&lt;/TotGroMasHEA307&gt;
        &lt;DecDatHEA383&gt;20201117&lt;/DecDatHEA383&gt;
        &lt;DecPlaHEA394&gt;Dover&lt;/DecPlaHEA394&gt;
        &lt;SpeCirIndHEA1&gt;C&lt;/SpeCirIndHEA1&gt;
        &lt;ComRefNumHEA&gt;HQDOV018&lt;/ComRefNumHEA&gt;
        &lt;SecHEA358&gt;1&lt;/SecHEA358&gt;
        &lt;CodPlUnHEA357&gt;MONOPOLI001&lt;/CodPlUnHEA357&gt;
    &lt;/HEAHEA&gt;
    &lt;TRAPRIPC1&gt;
        &lt;NamPC17&gt;NCTS UK TEST LAB HMCE&lt;/NamPC17&gt;
        &lt;StrAndNumPC122&gt;11TH FLOOR, ALEX HOUSE, VICTORIA AV&lt;/StrAndNumPC122&gt;
        &lt;PosCodPC123&gt;SS99 1AA&lt;/PosCodPC123&gt;
        &lt;CitPC124&gt;SOUTHEND-ON-SEA, ESSEX&lt;/CitPC124&gt;
        &lt;CouPC125&gt;GB&lt;/CouPC125&gt;
        &lt;TINPC159&gt;GB954131533000&lt;/TINPC159&gt;
    &lt;/TRAPRIPC1&gt;
    &lt;TRACONCO1&gt;
        &lt;NamCO17&gt;NCTS UK TEST LAB HMCE&lt;/NamCO17&gt;
        &lt;StrAndNumCO122&gt;11TH FLOOR, ALEX HOUSE, VICTORIA AV&lt;/StrAndNumCO122&gt;
        &lt;PosCodCO123&gt;SS99 1AA&lt;/PosCodCO123&gt;
        &lt;CitCO124&gt;SOUTHEND-ON-SEA, ESSEX&lt;/CitCO124&gt;
        &lt;CouCO125&gt;GB&lt;/CouCO125&gt;
        &lt;TINCO159&gt;GB954131533000&lt;/TINCO159&gt;
    &lt;/TRACONCO1&gt;
    &lt;TRACONCE1&gt;
        &lt;NamCE17&gt;NCTS UK TEST LAB HMCE&lt;/NamCE17&gt;
        &lt;StrAndNumCE122&gt;ITALIAN OFFICE&lt;/StrAndNumCE122&gt;
        &lt;PosCodCE123&gt;IT99 1IT&lt;/PosCodCE123&gt;
        &lt;CitCE124&gt;MILAN&lt;/CitCE124&gt;
        &lt;CouCE125&gt;IT&lt;/CouCE125&gt;
        &lt;TINCE159&gt;IT11ITALIANC11&lt;/TINCE159&gt;
    &lt;/TRACONCE1&gt;
    &lt;CUSOFFDEPEPT&gt;
        &lt;RefNumEPT1&gt;GB000060&lt;/RefNumEPT1&gt;
    &lt;/CUSOFFDEPEPT&gt;
    &lt;CUSOFFTRARNS&gt;
        &lt;RefNumRNS1&gt;FR001260&lt;/RefNumRNS1&gt;
        &lt;ArrTimTRACUS085&gt;202011190935&lt;/ArrTimTRACUS085&gt;
    &lt;/CUSOFFTRARNS&gt;
    &lt;CUSOFFDESEST&gt;
        &lt;RefNumEST1&gt;IT018105&lt;/RefNumEST1&gt;
    &lt;/CUSOFFDESEST&gt;
    &lt;CONRESERS&gt;
        &lt;ConResCodERS16&gt;A3&lt;/ConResCodERS16&gt;
        &lt;DatLimERS69&gt;20201125&lt;/DatLimERS69&gt;
    &lt;/CONRESERS&gt;
    &lt;SEAINFSLI&gt;
        &lt;SeaNumSLI2&gt;1&lt;/SeaNumSLI2&gt;
        &lt;SEAIDSID&gt;
            &lt;SeaIdeSID1&gt;NCTS001&lt;/SeaIdeSID1&gt;
        &lt;/SEAIDSID&gt;
    &lt;/SEAINFSLI&gt;
    &lt;GUAGUA&gt;
        &lt;GuaTypGUA1&gt;1&lt;/GuaTypGUA1&gt;
        &lt;GUAREFREF&gt;
            &lt;GuaRefNumGRNREF1&gt;09GB00000100000M0&lt;/GuaRefNumGRNREF1&gt;
            &lt;AccCodREF6&gt;AC01&lt;/AccCodREF6&gt;
        &lt;/GUAREFREF&gt;
    &lt;/GUAGUA&gt;
    &lt;GOOITEGDS&gt;
        &lt;IteNumGDS7&gt;1&lt;/IteNumGDS7&gt;
        &lt;GooDesGDS23&gt;Daffodils&lt;/GooDesGDS23&gt;
        &lt;GooDesGDS23LNG&gt;EN&lt;/GooDesGDS23LNG&gt;
        &lt;GroMasGDS46&gt;1000&lt;/GroMasGDS46&gt;
        &lt;NetMasGDS48&gt;950&lt;/NetMasGDS48&gt;
        &lt;PACGS2&gt;
            &lt;MarNumOfPacGS21&gt;AB234&lt;/MarNumOfPacGS21&gt;
            &lt;KinOfPacGS23&gt;BX&lt;/KinOfPacGS23&gt;
            &lt;NumOfPacGS24&gt;10&lt;/NumOfPacGS24&gt;
        &lt;/PACGS2&gt;
        &lt;TRACORSECGOO021&gt;
            &lt;TINTRACORSECGOO028&gt;GB954131533000&lt;/TINTRACORSECGOO028&gt;
        &lt;/TRACORSECGOO021&gt;
        &lt;TRACONSECGOO013&gt;
            &lt;TINTRACONSECGOO020&gt;IT17THEBOSS42&lt;/TINTRACONSECGOO020&gt;
        &lt;/TRACONSECGOO013&gt;
    &lt;/GOOITEGDS&gt;
    &lt;ITI&gt;
        &lt;CouOfRouCodITI1&gt;GB&lt;/CouOfRouCodITI1&gt;
    &lt;/ITI&gt;
    &lt;CARTRA100&gt;
        &lt;TINCARTRA254&gt;GB954131533000&lt;/TINCARTRA254&gt;
    &lt;/CARTRA100&gt;
&lt;/CC015B&gt;
&quot;
</pre>
</details>


<details>
  <summary><strong>See IE016 CURL command</strong></summary>

<pre>
curl --location --request POST &apos;https://test-api.service.hmrc.gov.uk/test/customs/transits/movements/departures/{deptId}/messages&apos; \
--header &apos;Content-Type: application/json&apos; \
--header &apos;Authorization: Bearer &lt;Enter your Bearer Token&gt;&apos; \
--data-binary @- << EOF
{
	"message": {
		"messageType": "IE016"
	}
}
EOF
</pre>
</details>


<details>
   <summary><strong>See Get Dept Id CURL command</strong></summary>

<pre>
curl --location --request GET &apos;https://test-api.service.hmrc.gov.uk/customs/transits/movements/departures/{deptId}&apos; \
--header &apos;Accept: application/vnd.hmrc.1.0+json&apos; \
--header &apos;Authorization: Bearer &lt;Enter your Bearer Token&gt;&apos;
</pre>
</details>

### Postman test scripts

These [scripts](https://github.com/hmrc/common-transit-convention-traders-postman) were created in November 2020. They will not be monitored or updated.
-->

### Our XSD files

You can check your XML files against our [XSD](https://github.com/hmrc/transit-movements-validator/tree/main/conf/xsd) files.

### NCTS Phase 5 Technical Interface Specification

The [specification](/guides/ctc-traders-phase5-tis) provides a list of messages, message details, and process flow diagrams.  
