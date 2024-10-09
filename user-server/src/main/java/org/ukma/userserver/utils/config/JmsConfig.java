package org.ukma.userserver.utils.config;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.lang.Nullable;

@Configuration
public class JmsConfig {

    @Bean
    public DynamicDestinationResolver destinationResolver() {
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(@Nullable Session session, String destinationName,
                                                      boolean pubSubDomain) throws JMSException {
                pubSubDomain = destinationName.endsWith("topic");
                return super.resolveDestinationName(session, destinationName, pubSubDomain);
            }
        };
    }
}
