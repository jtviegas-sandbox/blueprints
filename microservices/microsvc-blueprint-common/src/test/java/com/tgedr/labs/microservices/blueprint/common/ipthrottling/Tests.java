package com.tgedr.labs.microservices.blueprint.common.ipthrottling;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
public class Tests {

    private static final int defaultIpThrottlingRatePerMinute = 20;
    private final int iterations = 1000;
    private static final long TEST_DURATION_IN_MILLIS = 3 * 60 * 1000l;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private Bucket defaultBucket;

    @Before
    public void init(){
        Refill refill = Refill.intervally(defaultIpThrottlingRatePerMinute, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(defaultIpThrottlingRatePerMinute, refill).withInitialTokens(defaultIpThrottlingRatePerMinute);
        defaultBucket = Bucket4j.builder().addLimit(limit).build();
    }

    @Test
    public void shouldRateLimitOnTooBusyClient() throws InterruptedException, ExecutionException {

        final int clientRatePerMinute = 500;
        final int testRatePerMinute = 525;

        ClientIpThrottlingInterceptor interceptor = new ClientIpThrottlingInterceptor(
                new IpThrottlingSimpleConfig.BucketSupplier(clientRatePerMinute)
                , defaultBucket );
        RegistryMock registry = new RegistryMock().withInterceptor(interceptor);
        IpThrottlingTest test = IpThrottlingTest.with(registry, executor).doIt(iterations, testRatePerMinute);

        executor.shutdown();

        Assert.assertFalse(test.get());
    }

    @Test
    public void shouldNotRateLimitOnCalmClient() throws InterruptedException, ExecutionException {

        final int clientRatePerMinute = 500; // 600/min
        final int testRatePerMinute = 500;

        ClientIpThrottlingInterceptor interceptor = new ClientIpThrottlingInterceptor(
                new IpThrottlingSimpleConfig.BucketSupplier(clientRatePerMinute)
                , defaultBucket );
        RegistryMock registry = new RegistryMock().withInterceptor(interceptor);

        IpThrottlingTest test = IpThrottlingTest.with(registry, executor).doIt(iterations, testRatePerMinute);

        executor.shutdown();

        Assert.assertTrue(test.get());
    }

    @Test
    public void shouldIdentifyAndLimitTheBusyClients() throws InterruptedException, ExecutionException {
        final int clientRatePerMinute = 500;
        final int[] rates = new int[]{700,450,590,420};
        final int[] rates4DefaultTests = new int[]{30,20};
        List<IpThrottlingTest> tests = new LinkedList<>();
        // create the interceptor that is going to be tested
        ClientIpThrottlingInterceptor interceptor = new ClientIpThrottlingInterceptor(
                new IpThrottlingSimpleConfig.BucketSupplier(clientRatePerMinute)
                , defaultBucket );
        RegistryMock registry = new RegistryMock().withInterceptor(interceptor);
        // kickoff the tests
        for( int i= 0; i<rates.length; i++ )
            tests.add(IpThrottlingTest.with(registry, executor).doIt(iterations, rates[i]));

        for( int i= 0; i < rates4DefaultTests.length; i++ ) {
            if( 0 != i)
                Thread.sleep(30000);
            tests.add(IpThrottlingTest.with(registry, executor, false).doIt(iterations, rates4DefaultTests[i]));
        }
        // wait for the tests to run
        executor.shutdown();
        // check the outcomes
        int index = 0;
        for(IpThrottlingTest test: tests){
            log.info("test {} outcome: {}", index, test.get());
            if( 0 != index++%2 )
                Assert.assertTrue(test.get());
            else
                Assert.assertFalse(test.get());
        }

    }

    @Test
    public void shouldRateLimitOnSpecificPaths() throws InterruptedException, ExecutionException {

        String[] paths = new String[]{"path1","path2","path3","path4"};
        paths = new String[]{"path1","path2"};
        int[] rates = new int[]{600,750,800,900};
        rates = new int[]{600,750};

        List<IpThrottlingTest> tests = new LinkedList<>();
        RegistryMock registry = new RegistryMock();

        for( int i = 0; i<paths.length; i++ ){
            String path = paths[i];
            int rate = rates[i];
            registry.withInterceptor(path,
            new PathIpThrottlingInterceptor(
                    Bucket4j.builder().addLimit( Bandwidth.classic(rate , Refill.intervally(rate, Duration.ofMinutes(1)))
                                    .withInitialTokens(rate)).build()));
        }

        for( int i = 0; i<paths.length; i++ )
            tests.add(IpThrottlingTest.with(registry, executor, false, Optional.of(paths[i])).doIt(iterations, (int)(rates[i]+(rates[i]*0.1))));

        Thread.sleep(30000);

        for( int i = 0; i<paths.length; i++ )
            tests.add(IpThrottlingTest.with(registry, executor, false, Optional.of(paths[i])).doIt(iterations, (int)(rates[i]-(rates[i]*0.1))));

        executor.shutdown();

        // check the outcomes
        int index = 0;
        for(IpThrottlingTest test: tests){
            log.info("test {} outcome: {}", index, test.get());
            if( paths.length > index++ )
                Assert.assertFalse(test.get());
            else
                Assert.assertTrue(test.get());
        }
    }

    @Test
    public void shouldRateLimitOnSpecificPathTooBusyClient() throws InterruptedException, ExecutionException {

        final int pathRatePerMinute = 600;
        final int testRatePerMinute = 625;
        final int testRatePerMinute2 = 575;

        RegistryMock registry = new RegistryMock().withInterceptor("path1",
                new PathIpThrottlingInterceptor(
                        Bucket4j.builder().addLimit(
                                Bandwidth.classic(pathRatePerMinute
                                        , Refill.intervally(pathRatePerMinute, Duration.ofMinutes(1)))
                                        .withInitialTokens(pathRatePerMinute)).build())
                ).withInterceptor("path2",
                new PathIpThrottlingInterceptor(
                        Bucket4j.builder().addLimit(
                                Bandwidth.classic(pathRatePerMinute
                                        , Refill.intervally(pathRatePerMinute, Duration.ofMinutes(1)))
                                        .withInitialTokens(pathRatePerMinute)).build())
        );
        IpThrottlingTest test1 = IpThrottlingTest.with(registry, executor, false, Optional.of("path1")).doIt(iterations, testRatePerMinute);
        IpThrottlingTest test2 = IpThrottlingTest.with(registry, executor, false, Optional.of("path2")).doIt(iterations, testRatePerMinute2);

        executor.shutdown();

        log.info("test {} outcome: {}", 1, test1.get());
        log.info("test {} outcome: {}", 2, test2.get());
        Assert.assertFalse(test1.get());
        Assert.assertTrue(test2.get());
    }

    @Test
    public void shouldRateLimitOnSpecificPathTooBusyClientAndClientAlso() throws InterruptedException, ExecutionException {

        final int pathRatePerMinute = 600;
        final int pathRatePerMinute2 = 500;
        final int clientRatePerMinute = 450;

        RegistryMock registry = new RegistryMock().withInterceptor("path1",
                new PathIpThrottlingInterceptor(
                        Bucket4j.builder().addLimit(
                                Bandwidth.classic(pathRatePerMinute
                                        , Refill.intervally(pathRatePerMinute, Duration.ofMinutes(1)))
                                        .withInitialTokens(pathRatePerMinute)).build())
        ).withInterceptor("path2",
                new PathIpThrottlingInterceptor(
                        Bucket4j.builder().addLimit(
                                Bandwidth.classic(pathRatePerMinute2
                                        , Refill.intervally(pathRatePerMinute2, Duration.ofMinutes(1)))
                                        .withInitialTokens(pathRatePerMinute2)).build())
        ).withInterceptor(new ClientIpThrottlingInterceptor(
                new IpThrottlingSimpleConfig.BucketSupplier(clientRatePerMinute)
                , defaultBucket ));

        IpThrottlingTest test1 = IpThrottlingTest.with(registry, executor, false, Optional.of("path1")).doIt(iterations, (int)(pathRatePerMinute*0.9));
        IpThrottlingTest test3 = IpThrottlingTest.with(registry, executor, false, Optional.of("path2")).doIt(iterations, (int)(pathRatePerMinute2*0.9));
        IpThrottlingTest test5 = IpThrottlingTest.with(registry, executor, false).doIt(iterations, (int)(defaultIpThrottlingRatePerMinute*0.9));
        IpThrottlingTest test7 = IpThrottlingTest.with(registry, executor).doIt(iterations, (int)(clientRatePerMinute*0.9));
        Thread.sleep(30000);
        IpThrottlingTest test2 = IpThrottlingTest.with(registry, executor, false, Optional.of("path1")).doIt(iterations, (int)(pathRatePerMinute*1.1));
        IpThrottlingTest test4 = IpThrottlingTest.with(registry, executor, false, Optional.of("path2")).doIt(iterations, (int)(pathRatePerMinute2*1.2));
        IpThrottlingTest test6 = IpThrottlingTest.with(registry, executor, false).doIt(iterations, (int)(defaultIpThrottlingRatePerMinute*1.1));
        IpThrottlingTest test8 = IpThrottlingTest.with(registry, executor).doIt(iterations, (int)(clientRatePerMinute*1.1));


        executor.shutdown();

        log.info("test {} outcome: {}", 1, test1.get());
        log.info("test {} outcome: {}", 2, test2.get());
        log.info("test {} outcome: {}", 3, test3.get());
        log.info("test {} outcome: {}", 4, test4.get());
        log.info("test {} outcome: {}", 5, test5.get());
        log.info("test {} outcome: {}", 6, test6.get());
        log.info("test {} outcome: {}", 7, test7.get());
        log.info("test {} outcome: {}", 8, test8.get());
        Assert.assertTrue(test1.get());
        Assert.assertFalse(test2.get());
        Assert.assertTrue(test3.get());
        Assert.assertFalse(test4.get());
        Assert.assertTrue(test5.get());
        Assert.assertFalse(test6.get());
        Assert.assertTrue(test7.get());
        Assert.assertFalse(test8.get());

    }


    static class RegistryMock {

        private final Map<String,HandlerInterceptor> interceptors = new ConcurrentHashMap<>();
        private HandlerInterceptor defaultInterceptor;

        RegistryMock withInterceptor(String path, HandlerInterceptor interceptor){
            this.interceptors.put(path, interceptor);
            return this;
        }

        RegistryMock withInterceptor(HandlerInterceptor interceptor){
            defaultInterceptor = interceptor;
            return this;
        }

        HandlerInterceptor getInterceptor(String path){
            return this.interceptors.get(path);
        }

        HandlerInterceptor getDefaultInterceptor(){
            return defaultInterceptor;
        }

    }

    static class IpThrottlingTest {

        private static int testIndex = 0;
        private final RegistryMock registry;
        private final ExecutorService executor;
        private final List<Future> futures;
        private final String apiKey;
        private final Optional<String> path;
        private Boolean outcome;

        static IpThrottlingTest with(final RegistryMock registry, final ExecutorService executor){
            return new IpThrottlingTest(registry, executor, true, Optional.empty());
        }

        static IpThrottlingTest with(final RegistryMock registry, final ExecutorService executor, final boolean generateKey){
            return new IpThrottlingTest(registry, executor, generateKey, Optional.empty());
        }

        static IpThrottlingTest with(final RegistryMock registry, final ExecutorService executor, final boolean generateKey, Optional<String> path){
            return new IpThrottlingTest(registry, executor, generateKey, path);
        }

        private IpThrottlingTest(final RegistryMock registry, final ExecutorService executor, boolean generateKey , Optional<String> path){
            testIndex++;
            this.registry = registry;
            this.executor = executor;
            this.futures = new LinkedList<>();
            this.path = path;
            if( generateKey )
                apiKey = RandomStringUtils.random(8, true,false);
            else
                apiKey = "";
        }

        IpThrottlingTest doIt(int iterations, int testRatePerMinute) throws InterruptedException {

            long start = System.currentTimeMillis();
            long duration = 0;
            long minute = 0;
            while( TEST_DURATION_IN_MILLIS > (duration = (System.currentTimeMillis() - start)) ){
                long currentMinute = duration / 60000;
                if( minute < currentMinute  ){
                    minute = currentMinute;
                    log.info("[doIt] test {} iteration time: {} m", testIndex, minute);
                }
                futures.add(executor.submit( new Request(registry , apiKey, path)));
                Thread.sleep((int)(1.0/(testRatePerMinute/60000.0)));
            }

            return this;
        }

        boolean get() throws ExecutionException, InterruptedException {
            if( null == outcome ){
                boolean r = true;
                for(Future<Boolean> future: futures)
                    r = r && future.get().booleanValue();

                this.outcome = r;
            }
            return this.outcome.booleanValue();
        }

    }

    static class Request implements Callable<Boolean>{
        private final RegistryMock registry;
        private final String apiKey;
        private final Optional<String> path;

        Request(final RegistryMock registry, final String apiKey, final Optional<String> path){
            this.registry  = registry;
            this.apiKey = apiKey;
            this.path = path;
        }
        @Override
        public Boolean call() throws Exception {
            Boolean r = null;
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", apiKey);
            if( path.isPresent() )
                r = registry.getInterceptor(path.get()).preHandle(request, new MockHttpServletResponse(), null);
            else
                r = registry.getDefaultInterceptor().preHandle(request, new MockHttpServletResponse(), null);

            return r;
        }
    }


}
