package com.tgedr.labs.microservices.blueprint.common.ipthrottling;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PathIpThrottlingInterceptor implements HandlerInterceptor {

    private final Bucket bucket;

    public PathIpThrottlingInterceptor(final Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        ConsumptionProbe probe = this.bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed())
            return true;
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

}
