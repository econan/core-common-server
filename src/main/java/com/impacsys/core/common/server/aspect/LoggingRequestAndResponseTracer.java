package com.impacsys.core.common.server.aspect;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.logging.tracer.AbstractLoggingTracer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Represents a logging tracer for request and response in a web application.
 * This class captures the request URL, HTTP method, client ID, and the time taken
 * to complete the request, along with the command and response entity.
 * 
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 */
@JsonPropertyOrder({ "requestUrl", "httpMethod", "clientId", "completedIn" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoggingRequestAndResponseTracer<C extends AbstractCommonCommand>
		extends AbstractLoggingTracer {

	private final String requestUrl;
	private final String httpMethod;
	private final String clientId;

	private final long completedIn;

	@JsonInclude(Include.NON_NULL)
	private final C command;

	@JsonInclude(Include.NON_NULL)
	@JsonProperty("response")
	private final Object entity;

	/**
	 *
	 * @param command
	 * @param entity
	 * @param request
	 */
	public LoggingRequestAndResponseTracer(
			@Nullable final C command,
			@Nullable final Object entity,
			@Nonnull final HttpServletRequest request) {

		this.command = command;
		this.entity = entity;
		/* TODO: What is the best way to get the exact created time? */
		this.completedIn =
				command == null
					? 0L :
						ZonedDateTime.now().toInstant().toEpochMilli() - command.getRequestDateTime();

		this.requestUrl = request.getRequestURI();
		this.httpMethod = request.getMethod();
		this.clientId = request.getRemoteAddr();
	}

	/**
	 * @return the completedIn
	 */
	public long getCompletedIn() {
		return this.completedIn;
	}

	/**
	 * @return the command
	 */
	public C getCommand() {
		return this.command;
	}

	/**
	 * @return the entity
	 */
	public Object getEntity() {
		return this.entity;
	}

	public String getRequestUrl() {
		return this.requestUrl;
	}

	public String getHttpMethod() {
		return this.httpMethod;
	}

	public String getClientId() {
		return this.clientId;
	}
}
