package com.tgedr.labs.microservices.blueprint.common.ipthrottling;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.bucket4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


public class ClientIpThrottlingInterceptor implements HandlerInterceptor {

    private final int CACHE_ELEMENT_EXPIRATION_HOURS = 24;
    private final  LoadingCache<String, Bucket> bucketCache;
    private final Bucket defaultBucket;
    private static final String CLIENT_ID_HEADER = "Authorization";

    public ClientIpThrottlingInterceptor(final Supplier<Bucket> clientBucketSupplier, final Bucket defaultBucket) {
        this.defaultBucket = defaultBucket;

        bucketCache = CacheBuilder.newBuilder()
                .maximumSize(1000).expireAfterWrite(CACHE_ELEMENT_EXPIRATION_HOURS, TimeUnit.HOURS)
                .build( CacheLoader.from(clientBucketSupplier));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        Bucket requestBucket;

        String clientKey = request.getHeader(CLIENT_ID_HEADER);
        if (clientKey != null && !clientKey.isEmpty())
            requestBucket = this.bucketCache.get(clientKey);
        else
            requestBucket = this.defaultBucket;

        ConsumptionProbe probe = requestBucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
            return true;

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

}
