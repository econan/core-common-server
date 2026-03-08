package com.impacsys.core.common.server.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.ImmutableMap;
import com.impacsys.core.common.command.validation.CommonValidationConstants;
import com.impacsys.core.common.command.validation.exception.CommandValidationError;
import com.impacsys.core.common.command.validation.exception.CommandValidationException;
import com.impacsys.core.common.command.validation.exception.UnAuthorizedException;

/**
 * To test {@link CommonExceptionHandler}
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 */
public class CommonExceptionHandlerTest {

	private final CommonExceptionHandler handler = new CommonExceptionHandler();

	@Test
	public void test_handleBeanPropertyBindingResult() {
        // BindException 생성
        final BindException exception = mock(BindException.class);

        final FieldError fieldError = new FieldError("objectName", "fieldName1", "message1");

        when(exception.getFieldErrors()).thenReturn(List.of(fieldError));

        // handleBeanPropertyBindingResult 메소드 호출
        final ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> responseEntity =
                this.handler.handleBeanPropertyBindingResult(exception);

        // 반환된 ResponseEntity가 예상대로인지 확인
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(fieldError.getField(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorField());
        assertEquals(fieldError.getDefaultMessage(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorCode());
	}

	@Test
	public void test_handleCommandValidationException() {

        final CommandValidationException exception = mock(CommandValidationException.class);

        final CommandValidationError commandValidationError = new CommandValidationError("fieldName1", "message1");

        when(exception.getFieldErrors()).thenReturn(List.of(commandValidationError));

        final ResponseEntity<ImmutableMap<String, List<CommandValidationError>>> responseEntity =
                this.handler.handleCommandValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(commandValidationError.getErrorField(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorField());
        assertEquals(commandValidationError.getErrorCode(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorCode());
	}

	@Test
	public void test_handleHttpRequestMethodNotSupportedException() {

        final HttpRequestMethodNotSupportedException exception = mock(HttpRequestMethodNotSupportedException.class);

        final ResponseEntity<Void> responseEntity =
                this.handler.handleHttpRequestMethodNotSupportedException(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

    }

	@Test
	public void test_handleMethodArgumentNotValidException() {

        final MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        final BindingResult bindingResult = mock(BindingResult.class);

        final FieldError fieldError = new FieldError("objectName", "fieldName1", "message1");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getBindingResult().getFieldErrors()).thenReturn(List.of(fieldError));

        final ResponseEntity<ImmutableMap<String, List<CommandValidationError>>>
                responseEntity = this.handler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(fieldError.getField(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorField());
        assertEquals(fieldError.getDefaultMessage(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorCode());
	}

	@Test
	public void test_handleUnAuthorizedException() {

        final UnAuthorizedException exception = mock(UnAuthorizedException.class);

        final ResponseEntity<Void> responseEntity =
                this.handler.handleUnAuthorizedException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}

	@Test
	public void test_handleUnExpectedException() {

        final Exception exception = mock(Exception.class);

        final ResponseEntity<Void> responseEntity =
                this.handler.handleUnExpectedException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
	}

	@Test
	public void test_handleHttpMessageNotReadableException() {

        final HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        final InvalidFormatException cause = mock(InvalidFormatException.class);
        final InvalidFormatException.Reference reference =
                new InvalidFormatException.Reference(null, "fieldName1");

        when(exception.getCause()).thenReturn(cause);
        when(cause.getPath()).thenReturn(List.of(reference));

        when(exception.getCause()).thenReturn(cause);
        when(cause.getPath()).thenReturn(List.of(reference));

        final ResponseEntity<ImmutableMap<String, List<CommandValidationError>>>
                responseEntity = this.handler.handleHttpMessageNotReadableException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(reference.getFieldName(),
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorField());
        assertEquals(CommonValidationConstants.ErrorCode.ERROR_INVALID,
                responseEntity.getBody().get(CommonValidationConstants.FieldName.KEY_ERRORS).get(0).getErrorCode());
	}

    @Test
    public void test_handleHttpMessageNotInvalidFormatException() {

        final HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        final ResponseEntity<ImmutableMap<String, List<CommandValidationError>>>
                responseEntity = this.handler.handleHttpMessageNotReadableException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
