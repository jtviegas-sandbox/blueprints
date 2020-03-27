package com.tgedr.labs.microservices.blueprint.services.knapsack.impl;

import com.tgedr.labs.microservices.blueprint.model.Item;
import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.services.knapsack.Knapsack;
import com.tgedr.labs.microservices.blueprint.services.knapsack.exceptions.KnapsackException;
import kong.unirest.*;
import kong.unirest.apache.ApacheClient;
import mockit.Mock;
import mockit.MockUp;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KnapsackTests {

	@Autowired
	private Knapsack service;

	@Test
	public void submitOK() throws Exception {

		new MockUp<ApacheClient>() {
			@Mock
			public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer){
				return new kong.unirest.HttpResponse(){
					@Override
					public int getStatus() {
						return 200;
					}

					@Override
					public String getStatusText() {
						return null;
					}

					@Override
					public Headers getHeaders() {
						return null;
					}

					@Override
					public Object getBody() {
						return "nfhfkankjsdfnjkadsnfkjlabdnfkjbadsbasbkl";
					}

					@Override
					public Optional<UnirestParsingException> getParsingError() {
						return Optional.empty();
					}

					@Override
					public boolean isSuccess() {
						return true;
					}

					@Override
					public Cookies getCookies() {
						return null;
					}

					@Override
					public Object mapError(Class aClass) {
						return null;
					}

					@Override
					public HttpResponse ifFailure(Class aClass, Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse ifFailure(Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse ifSuccess(Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse map(Function function) {
						return null;
					}

					@Override
					public Object mapBody(Function function) {
						return null;
					}
				};
			}
		};

		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;

		Assert.assertNotNull(service.submitProblem(problem));

	}

	@Test(expected = KnapsackException.class)
	public void submitNotOK() throws Exception {

		new MockUp<ApacheClient>() {
			@Mock
			public <T> HttpResponse<T> request(HttpRequest request, Function<RawResponse, HttpResponse<T>> transformer){
				return new kong.unirest.HttpResponse(){
					@Override
					public int getStatus() {
						return 500;
					}

					@Override
					public String getStatusText() {
						return "something wrong";
					}

					@Override
					public Headers getHeaders() {
						return null;
					}

					@Override
					public Object getBody() {
						return "nfhfkankjsdfnjkadsnfkjlabdnfkjbadsbasbkl";
					}

					@Override
					public Optional<UnirestParsingException> getParsingError() {
						return Optional.empty();
					}

					@Override
					public boolean isSuccess() {
						return false;
					}

					@Override
					public Cookies getCookies() {
						return null;
					}

					@Override
					public Object mapError(Class aClass) {
						return null;
					}

					@Override
					public HttpResponse ifFailure(Class aClass, Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse ifFailure(Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse ifSuccess(Consumer consumer) {
						return null;
					}

					@Override
					public HttpResponse map(Function function) {
						return null;
					}

					@Override
					public Object mapBody(Function function) {
						return null;
					}
				};
			}
		};

		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;

		service.submitProblem(problem);

	}
}
