package com.impacsys.core.common.server.exception.handler;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.impacsys.core.common.command.validation.CommonValidationConstants;
import com.impacsys.core.common.command.validation.exception.CommandValidationError;
import com.impacsys.core.common.command.validation.exception.CommandValidationException;
import com.impacsys.core.common.command.validation.exception.UnAuthorizedException;
import com.impacsys.core.common.logging.ImpacsysCommonLogger;

import jakarta.annotation.Nonnull;
import jakarta.validation.ValidationException;

/**
 * To handle expected exceptions to expose to the clients.
 *
 * For unit test {@link CommonExceptionHandlerTest}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
@ControllerAdvice
public class CommonExceptionHandler {

	private static final Logger LOGGER = ImpacsysCommonLogger.getInstance(CommonExceptionHandler.class);

	/**
	 * To handle a <code>CommandValidationException</code>.
	 */
	@ExceptionHandler(CommandValidationException.class)
	public ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> handleCommandValidationException(
			@Nonnull final CommandValidationException e) {

		return new ResponseEntity<>(ImmutableMap.of(CommonValidationConstants.FieldName.KEY_ERRORS, e.getFieldErrors()),
				HttpStatus.BAD_REQUEST);

	}

	/**
	 * To handle a <code>BindException</code>.
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> handleBeanPropertyBindingResult(
			@Nonnull final BindException e) {

		final List<CommandValidationError> fieldErrors = Lists.newArrayList();
		e.getFieldErrors().stream().forEach(error ->
				fieldErrors.add(
						new CommandValidationError(error.getField(), error.getDefaultMessage())));

		return new ResponseEntity<>(ImmutableMap.of(CommonValidationConstants.FieldName.KEY_ERRORS, fieldErrors),
				HttpStatus.BAD_REQUEST);

	}

	/**
	 * To handle common validation exceptions.
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> handleValidationException(
			@Nonnull final ValidationException e) {

		return new ResponseEntity<>(ImmutableMap.of(CommonValidationConstants.FieldName.KEY_ERRORS,
				((CommandValidationException) e.getCause()).getFieldErrors()), HttpStatus.BAD_REQUEST);

	}

	/**
	 * To handle a <code>MethodArgumentNotValidException</code> which is sent by
	 * spring @Valided annotation
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> handleMethodArgumentNotValidException(
			@Nonnull final MethodArgumentNotValidException e) {

		final List<CommandValidationError> fieldErrors = Lists.newArrayList();
		e.getBindingResult().getFieldErrors().stream().forEach(error ->
				fieldErrors.add(
						new CommandValidationError(error.getField(), error.getDefaultMessage())));

		return new ResponseEntity<>(ImmutableMap.of(CommonValidationConstants.FieldName.KEY_ERRORS, fieldErrors),
				HttpStatus.BAD_REQUEST);

	}

	/**
	 * To handle request parameter value that can not deserialize
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> handleHttpMessageNotReadableException(
			@Nonnull final HttpMessageNotReadableException e) {

		if (e.getCause() instanceof InvalidFormatException) {

			final List<CommandValidationError> fieldErrors = Lists.newArrayList();

			final InvalidFormatException cause = (InvalidFormatException) e.getCause();
			cause.getPath().forEach(reference ->
					fieldErrors.add(new CommandValidationError(
							reference.getFieldName(),
							CommonValidationConstants.ErrorCode.ERROR_INVALID)));

			return new ResponseEntity<>(ImmutableMap.of(CommonValidationConstants.FieldName.KEY_ERRORS, fieldErrors),
					HttpStatus.BAD_REQUEST);

		}

		LOGGER.error(e.getMessage(), e);

		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * To handle HttpRequestMethodNotSupportedException which is not catching anywhere.
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(
			@Nonnull final HttpRequestMethodNotSupportedException e) {

		LOGGER.error(e.getMessage(), e);

		return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);

	}

	/**
	 * To handle exception which is not catching anywhere.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Void> handleUnExpectedException(@Nonnull final Exception e) {

		LOGGER.error(e.getMessage(), e);

		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}

	/**
	 * To handle common validation exceptions.
	 */
	@ExceptionHandler(UnAuthorizedException.class)
	public ResponseEntity<Void> handleUnAuthorizedException(
			@Nonnull final UnAuthorizedException e) {

		LOGGER.error(e.getMessage(), e);

		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

	}

	/**
	 * To handle NoResourceFoundException
	 */
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Void> handleNoResourceFoundException(
			@Nonnull final NoResourceFoundException e) {

		LOGGER.error(e.getMessage(), e);

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}
}
