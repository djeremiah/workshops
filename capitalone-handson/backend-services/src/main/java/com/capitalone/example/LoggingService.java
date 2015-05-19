package com.capitalone.example;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("log")
public class LoggingService {

	private static final Logger LOG = LoggerFactory
			.getLogger("example.logging-service");

	@POST
	public void logMessage(final String message) {
		LOG.info(message);
	}
}
