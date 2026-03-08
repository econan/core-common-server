package com.impacsys.core.common.server.aspect;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.impacsys.core.common.command.AbstractCommonCommand;
import com.impacsys.core.common.entity.AbstractCommonEntity;
import com.impacsys.core.common.logging.ImpacsysCommonLogger;
import com.impacsys.core.common.utils.httprequest.CommonHttpRequestUtil;

/**
 * TODO: To write log asynchronously @see com.lmax.disruptor or using <code>ApplicationEventPublisher</code>
 *
 * @author hkb@imcorp.kr
 * @since 2025.7.9
 *
 * @param <C>
 * @param <E>
 */
@Component
@Aspect
public class LoggingRequestAndResponseAspect<C extends AbstractCommonCommand, E extends AbstractCommonEntity> {

	private static final Logger LOGGER = ImpacsysCommonLogger.getInstance(LoggingRequestAndResponseAspect.class);

	@Value("${show.response:false}")
	private boolean showResponse;

	@AfterReturning(
			pointcut = "@annotation(com.impacsys.core.common.server.aspect.group.LoggingRequestAndResponse)",
			returning = "responseEntity")
	public void afterReturning(final JoinPoint jointPoint, final Object responseEntity) {
		this.writeLog(jointPoint, responseEntity);
	}

	/**
	 * Writes a log which is about request and response as a JSON
	 */
	private void writeLog(final JoinPoint jointPoint, final Object responseEntity) {

		final Object entity = Optional.ofNullable(responseEntity)
				.filter(response -> this.showResponse)
				.map(response -> ((ResponseEntity<?>) response).getBody())
				.orElse(null);

		final LoggingRequestAndResponseTracer<?> tracer = new LoggingRequestAndResponseTracer<>(
				this.getCommand(jointPoint),
				entity,
				CommonHttpRequestUtil.getHttpServletRequest());

		ImpacsysCommonLogger.getJsonConsolLogger().info(tracer.toString());
	}

	/**
	 *
	 * @param jointPoint
	 * @return
	 */
	private AbstractCommonCommand getCommand(final JoinPoint jointPoint) {

		if (jointPoint == null) {
			return null;
		}

		try {
			final Optional<AbstractCommonCommand> command = (jointPoint.getArgs().length > 0) ?
					Optional.ofNullable((AbstractCommonCommand)jointPoint.getArgs()[0])
					: Optional.empty();

			return command.orElse(null);

		} catch (final ClassCastException e){

			LOGGER.error(e.getMessage());

			return null;
		}

	}
}
