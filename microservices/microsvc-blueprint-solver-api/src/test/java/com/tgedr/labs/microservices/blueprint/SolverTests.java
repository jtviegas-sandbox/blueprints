package com.tgedr.labs.microservices.blueprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgedr.labs.microservices.blueprint.common.services.state.StateManager;
import com.tgedr.labs.microservices.blueprint.model.Item;
import com.tgedr.labs.microservices.blueprint.model.Problem;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SolverTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper jsonMapper;

	@MockBean
	private StateManager taskStateManager;

	private static final String endpoint = "/api/problem";

	@Test
	public void test_001_shouldAcceptTask() throws Exception {

		Problem expected = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;

		MvcResult result = this.mockMvc.perform(post(endpoint).contentType("application/json")
				.content(jsonMapper.writeValueAsString(expected))).andExpect(status().isOk()).andReturn();
		Assert.assertNotNull(result.getResponse().getContentAsString());
	}

	@Test
	public void test_002_shouldAcceptOnlyValidTasks() throws Exception {
		String wrongJson = "{\"capacity\": 1234, \"items\": []}";
		mockMvc.perform(post(endpoint).contentType("application/json").content(wrongJson))
				.andExpect(status().is(422));
	}
}
