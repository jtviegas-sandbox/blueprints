package com.tgedr.labs.microservices.blueprint;

import com.tgedr.labs.microservices.blueprint.common.exceptions.ExceptionResponse;
import datadog.trace.api.CorrelationIdentifier;
import lombok.extern.slf4j.Slf4j;
import com.tgedr.labs.microservices.blueprint.common.exceptions.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;


@ControllerAdvice
@Slf4j
public class CustomExceptionHandlerConfig extends ResponseEntityExceptionHandler {

	private static final String LOGID_FORMAT = "trace: %s | span: %s";
	private static final String MSG_FORMAT = "%s | path: %s | msg: %s";

	@ExceptionHandler
	ResponseEntity<ExceptionResponse> handleControllerException(HttpServletRequest request, Throwable ex) {
		log.trace("[handleControllerException|in] ({},{})", request, ex);
		HttpStatus status = getStatus(request, ex);
		log.error("[handleControllerException]", ex);
		log.trace("[handleControllerException|out] => status: {}", status);
		return new ResponseEntity<ExceptionResponse>(
				ExceptionResponse.createLogReferringExceptionResponse(status.value(), logId(ex)), status);
	}

	private String logId(Throwable ex) {
		log.trace("[logId|in] ({})", ex);
		UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
		String path = builder.buildAndExpand().getPath();
		String logId = format(LOGID_FORMAT, CorrelationIdentifier.getTraceId(), CorrelationIdentifier.getSpanId());
		log.error(format(MSG_FORMAT, logId, path, ex.getMessage()), ex);
		log.trace("[logId|out] => logId: {}", logId);
		return logId;
	}

	private HttpStatus getStatus(HttpServletRequest request, Throwable ex) {
		HttpStatus code = null;
		if (ex instanceof ApiException && null != (code = ((ApiException) ex).getStatusCode())) {
			// note that ApiException constructor sets the code
			return code;
		}

		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null)
			return HttpStatus.INTERNAL_SERVER_ERROR;

		return HttpStatus.valueOf(statusCode);
	}

	// this handles invalid fields in json objects and such
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.trace("[handleHttpMessageNotReadable|in] (status: {})", status);
		log.error("[handleHttpMessageNotReadable]", ex);
		log.trace("[handleHttpMessageNotReadable|out] (status: {})", HttpStatus.UNPROCESSABLE_ENTITY);
		return new ResponseEntity<>(ExceptionResponse.createLogReferringExceptionResponse(
				HttpStatus.UNPROCESSABLE_ENTITY.value(), logId(ex)), HttpStatus.UNPROCESSABLE_ENTITY);
	}

	// this handles validation exception, i.e. @NotNull checks
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.trace("[handleMethodArgumentNotValid|in] (status: {})", status);
		log.error("[handleMethodArgumentNotValid]", ex);
		log.trace("[handleMethodArgumentNotValid|out] (status: {})", HttpStatus.UNPROCESSABLE_ENTITY);
		return new ResponseEntity<>(ExceptionResponse.createLogReferringExceptionResponse(
				HttpStatus.UNPROCESSABLE_ENTITY.value(), logId(ex)), HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
