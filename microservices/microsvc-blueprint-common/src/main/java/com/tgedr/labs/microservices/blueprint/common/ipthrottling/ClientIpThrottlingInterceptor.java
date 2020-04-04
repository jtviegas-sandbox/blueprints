package com.tgedr.labs.microservices.blueprint.common.ipthrottling;

import io.github.bucket4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ClientIpThrottlingInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final Bucket defaultBucket;

    private final Supplier<Bucket> clientBucketSupplier;
    private static final String CLIENT_ID_HEADER = "Authorization";

    public ClientIpThrottlingInterceptor(final Supplier<Bucket> clientBucketSupplier, Bucket defaultBucket) {
        this.clientBucketSupplier = clientBucketSupplier;
        this.defaultBucket = defaultBucket;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Bucket requestBucket;

        String clientKey = request.getHeader(CLIENT_ID_HEADER);
        if (clientKey != null && !clientKey.isEmpty())
            requestBucket = this.buckets.computeIfAbsent(clientKey, key -> clientBucketSupplier.get());
        else
            requestBucket = this.defaultBucket;

        ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
            return true;

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

}
