/*
 * Copyright (c) 2015 Youssuf ElKalay / BuildScientist. 
 * All Rights Reserved.
 * This software is released under the Apache license 2.0
 * This file has been modified by the copyright holder.
 */
package com.buildscientist;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.reficio.ws.client.core.SoapClient;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import wiremock.org.mortbay.jetty.HttpStatus;

import com.buildscientist.utils.WireMockUtils;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.junit.WireMockRule;



/**
 * @author Youssuf ElKalay
 * Basic Wiremock test of the TempConvert SOAP based web service
 * http://www.w3schools.com/webservices/tempconvert.asmx
 */
public class TempConvertTest {

	private final static int WIREMOCK_LISTENER_PORT = WireMockUtils.getAvailablePort();
	private final static String WIREMOCK_BASE_URI = "http://localhost:" + WIREMOCK_LISTENER_PORT;
	private List<Request> wiremockRequests = new ArrayList<Request>();
	private final static Logger log = LoggerFactory.getLogger(TempConvertTest.class);

	
	@Before
	public void setUp() throws Exception {
		WireMock.resetAllRequests();
		WireMockUtils.addMockRequestListener(wiremockRule, wiremockRequests);
	}

	@After
	public void tearDown() throws Exception {
		wiremockRequests.clear();
	}

	@Rule
	public WireMockRule wiremockRule = new WireMockRule(WIREMOCK_LISTENER_PORT);
	
	@Test
	public void mockFarenheightToCelcius() throws IOException, SAXException, XpathException {
		String conversionEndpointURI = "/tempconvert.asmx?op=FahrenheitToCelsius";		
		String requestXPathQuery = "//soap12:Envelope | //soap12:Body | //FahrenheitToCelsius | //Fahrenheit";
		String responseEnvelope = IOUtils.toString(getClass().getResourceAsStream("/farenheighttocelcius/sampleresponse.xml"),"UTF-8");
		String requestEnvelope = IOUtils.toString(getClass().getResourceAsStream("/farenheighttocelcius/samplerequest.xml"),"UTF-8");
				
		/**
		 * Mock SOAP endpoint for a SOAP web service that matches a response body with the following SOAP envelope:
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		 * 		<soap:Body> 
		 * 			<FahrenheitToCelsiusResponse>
		 * 			<FahrenheitToCelsiusResult>100</FahrenheitToCelsiusResult> </FahrenheitToCelsiusResponse>
		 * 		</soap:Body> 
		 * </soap:Envelope>
		 **/
		
				
		wiremockRule.stubFor(post(urlEqualTo(conversionEndpointURI)).withHeader("Content-Type", equalTo("application/soap+xml"))
				.withRequestBody(matchingXPath(requestXPathQuery))
				.willReturn(aResponse().withStatus(HttpStatus.ORDINAL_200_OK).withBody(responseEnvelope)).withHeader("Content-Type", equalTo("application/soap+xml")));
		
		SoapClient client = SoapClient.builder().endpointUri(WIREMOCK_BASE_URI + conversionEndpointURI).build();
		client.post(requestEnvelope);
			
		wiremockRule.verify(postRequestedFor(urlEqualTo(conversionEndpointURI)).withRequestBody(matchingXPath(requestXPathQuery)));
		
	}
	
	@Test
	public void mockCelciusToFarenheight() throws IOException {
		String conversionEndpointURI = "/tempconvert.asmx?op=CelsiusToFarenheight";	
		String requestXPathQuery = "//soapenv:Envelope | //soapenv:Body | //CelsiusToFahrenheit | //CelsiusToFahrenheit";
		String responseEnvelope = IOUtils.toString(getClass().getResourceAsStream("/celsiustofarenheight/sampleresponse.xml"),"UTF-8");
		String requestEnvelope = IOUtils.toString(getClass().getResourceAsStream("/celsiustofarenheight/samplerequest.xml"),"UTF-8");
		
		/**
		 * Mock SOAP endpoint for a SOAP web service that matches a response body with the following SOAP envelope:
		 * <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		 * 	<soap:Body>
		 * 		<CelsiusToFahrenheitResponse xmlns="http://www.w3schools.com/webservices/">
		 * 			<CelsiusToFahrenheitResult>0</CelsiusToFahrenheitResult> 
		 * 		</CelsiusToFahrenheitResponse> 
		 * 	</soap:Body>
		 * </soap:Envelope>
		 **/
		
		wiremockRule.stubFor(post(urlEqualTo(conversionEndpointURI)).withHeader("Content-Type", equalTo("application/soap+xml")).withRequestBody(
				matchingXPath(requestXPathQuery)).willReturn(
				aResponse().withStatus(HttpStatus.ORDINAL_200_OK).withBody(responseEnvelope)).withHeader("Content-Type", equalTo("application/soap+xml")));
		
		SoapClient client = SoapClient.builder().endpointUri(WIREMOCK_BASE_URI + conversionEndpointURI).build();
		client.post(requestEnvelope);
		
		wiremockRule.verify(postRequestedFor(urlEqualTo(conversionEndpointURI)).withRequestBody(matchingXPath(requestXPathQuery)));

	}
	
}
