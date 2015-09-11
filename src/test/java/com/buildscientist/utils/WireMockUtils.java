/*
 * Copyright (c) 2015 Youssuf ElKalay / BuildScientist. 
 * All Rights Reserved.
 * This software is released under the Apache license 2.0
 * This file has been modified by the copyright holder.
 */
package com.buildscientist.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

/**
 * @author Youssuf ElKalay
 *
 */
public class WireMockUtils {
	
	public static void addMockRequestListener(final WireMockRule rule, final List<Request> wiremockRequests) {
		//Handy WireMock listener that stores each request received
				rule.addMockServiceRequestListener(new RequestListener() {
				     @Override
				     public void requestReceived(Request request, Response response) {
				    	 wiremockRequests.add(LoggedRequest.createFrom(request));
				     }
				});
	}
	
	public static int getAvailablePort() {
		ServerSocket serverside = null;
		int port = 0;
		try {
			serverside = new ServerSocket(0);
			port = serverside.getLocalPort();
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				serverside.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return port;
	}

}
