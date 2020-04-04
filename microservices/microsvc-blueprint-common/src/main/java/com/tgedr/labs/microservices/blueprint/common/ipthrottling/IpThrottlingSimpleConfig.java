package com.tgedr.labs.microservices.blueprint.common.ipthrottling;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Set;
import java.util.function.Supplier;

import static java.lang.String.format;

@Slf4j
@Configuration
public class IpThrottlingSimpleConfig implements WebMvcConfigurer {

    private static final String CONFIG_SEPARATOR = ":";
    @Value("${ip.throttling.path-rate-per-minute:#{null}}")
    private Set<String> pathIpThrottlingConfig;
    @Value("${ip.throttling.client-rate-per-minute:0}")
    private int clientIpThrottlingRatePerMinute;
    @Value("${ip.throttling.default-rate-per-minute:20}")
    private int defaultIpThrottlingRatePerMinute;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if( null != pathIpThrottlingConfig && ! pathIpThrottlingConfig.isEmpty() )
            for( String config: pathIpThrottlingConfig)
                createPathRelatedInterceptor(config, registry);

        if ( 0 <  clientIpThrottlingRatePerMinute )
            createClientRelatedInterceptor(clientIpThrottlingRatePerMinute, defaultIpThrottlingRatePerMinute, registry);

    }

    void createClientRelatedInterceptor(final int clientIpThrottlingRatePerMinute, final int defaultIpThrottlingRatePerMinute,
                                        final InterceptorRegistry registry) {
        try {
            Refill refill = Refill.intervally(defaultIpThrottlingRatePerMinute, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(defaultIpThrottlingRatePerMinute, refill).withInitialTokens(defaultIpThrottlingRatePerMinute);
            Bucket defaultBucket = Bucket4j.builder().addLimit(limit).build();

            registry.addInterceptor(
                new ClientIpThrottlingInterceptor(new BucketSupplier(clientIpThrottlingRatePerMinute)
                        , defaultBucket));

            log.info("[createClientRelatedInterceptor] created client ip throttling interceptor with minute rate: {}", clientIpThrottlingRatePerMinute);
        } catch (Exception e) {
            log.warn("[createClientRelatedInterceptor] could not create client ip throttling interceptor", e);
        }
    }

    void createPathRelatedInterceptor(final String config, final InterceptorRegistry registry) {
        try {
            String[] parts = config.split(CONFIG_SEPARATOR);
            if( 2 != parts.length )
                throw new Exception(format("wrong path ip throttling config [ip.throttling.path-rate-per-minute]: %s", config));

            String path = parts[0];
            int minuteRate = Integer.parseInt(parts[1]);

            Refill refill = Refill.intervally(minuteRate, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(minuteRate, refill).withInitialTokens(minuteRate);
            Bucket bucket = Bucket4j.builder().addLimit(limit).build();
            registry.addInterceptor(new PathIpThrottlingInterceptor(bucket)).addPathPatterns(path);
            log.info("[createPathRelatedInterceptor] created ip throttling interceptor from config: {}", config);
        } catch (Exception e) {
            log.warn("[createPathRelatedInterceptor] wrong path ip throttling config [ip.throttling.path-rate-per-minute]: {}", config, e);
        }
    }

    static class BucketSupplier implements Supplier<Bucket> {
        final int limitPerMinute;
        BucketSupplier(final int limitPerMinute){
            this.limitPerMinute = limitPerMinute;
        }

        @Override
        public Bucket get() {
            Refill refill = Refill.intervally(limitPerMinute, Duration.ofMinutes(1));
            Bandwidth limit = Bandwidth.classic(limitPerMinute, refill).withInitialTokens(limitPerMinute);
            return Bucket4j.builder().addLimit(limit).build();
        }
    }


}
