package eu.arima;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.util.ClassUtils;

import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

@Component
public class LogListener implements ApplicationListener<AbstractAuthenticationEvent> {

	private static final Log logger = LogFactory.getLog(LogListener.class);

	/**
	 * If set to true, {@link InteractiveAuthenticationSuccessEvent} will be logged
	 * (defaults to true)
	 */
	private boolean logInteractiveAuthenticationSuccessEvents = true;

	@Override
	public void onApplicationEvent(AbstractAuthenticationEvent event) {
		if (!this.logInteractiveAuthenticationSuccessEvents && event instanceof InteractiveAuthenticationSuccessEvent) {
			return;
		}
		logger.warn(LogMessage.of(() -> getLogMessage(event)));
	}

	private String getLogMessage(AbstractAuthenticationEvent event) {
		StringBuilder builder = new StringBuilder();
		builder.append("Authentication event ");
		builder.append(ClassUtils.getShortName(event.getClass()));
		builder.append(": ");
		builder.append(event.getAuthentication().getName());
		builder.append("; details: ");
		builder.append(event.getAuthentication().getDetails());
		if (event instanceof AbstractAuthenticationFailureEvent) {
			builder.append("; exception: ");
			builder.append(((AbstractAuthenticationFailureEvent) event).getException().getMessage());
		}
		return builder.toString();
	}

	public boolean isLogInteractiveAuthenticationSuccessEvents() {
		return this.logInteractiveAuthenticationSuccessEvents;
	}

	public void setLogInteractiveAuthenticationSuccessEvents(boolean logInteractiveAuthenticationSuccessEvents) {
		this.logInteractiveAuthenticationSuccessEvents = logInteractiveAuthenticationSuccessEvents;
	}

}
