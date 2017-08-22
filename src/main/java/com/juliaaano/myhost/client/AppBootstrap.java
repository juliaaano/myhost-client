package com.juliaaano.myhost.client;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.MDCUnitOfWork;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.slf4j.MDC;

import static com.juliaaano.myhost.client.AsciiBanner.asciiBanner;
import static java.util.UUID.randomUUID;

public class AppBootstrap {

    public static void main(String... args) throws Exception {

        asciiBanner("application-ascii-banner.txt").ifPresent(AsciiBanner::print);

        final Main main = new Main();

        main.addMainListener(new MainListener());
        main.addRouteBuilder(new MyHostRouteBuilder());

        main.run(args);
    }

    private static final class MainListener extends MainListenerSupport {

        @Override
        public void configure(CamelContext context) {
            context.setUseMDCLogging(true);
            context.setUnitOfWorkFactory(CorrelationIdUnitOfWork::new);
        }
    }

    private static final class CorrelationIdUnitOfWork extends MDCUnitOfWork {

        private static final String CORRELATION_ID = "X-Request-ID";

        private CorrelationIdUnitOfWork(final Exchange exchange) {

            super(exchange);

            final String correlationId = exchange.getIn().getHeader(
                    CORRELATION_ID,
                    randomUUID()::toString,
                    String.class
            );

            MDC.put(CORRELATION_ID, correlationId);
            exchange.getIn().setHeader(CORRELATION_ID, correlationId);
        }
    }

}
