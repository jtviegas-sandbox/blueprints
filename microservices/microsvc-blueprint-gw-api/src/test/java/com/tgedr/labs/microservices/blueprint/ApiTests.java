package com.tgedr.labs.microservices.blueprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgedr.labs.microservices.blueprint.common.services.knapsack.Knapsack;
import com.tgedr.labs.microservices.blueprint.common.services.state.StateManager;
import com.tgedr.labs.microservices.blueprint.model.*;
import org.apache.commons.lang3.RandomStringUtils;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper jsonMapper;

	@MockBean
	private StateManager taskStateManager;

	@MockBean
	private Knapsack knapsack;

	@Test
	public void test_001_postShouldBeOkAndReturnId() throws Exception {

		Problem problem = new Problem(60, Arrays.asList(Item.from(10, 10),
						Item.from(20, 3), Item.from(33, 30)));
		when(knapsack.submitProblem(problem)).thenReturn(RandomStringUtils.random(14, true,false ));
		MvcResult result = mockMvc
				.perform(post("/api/tasks").contentType("application/json")
						.content(jsonMapper.writeValueAsString(problem)))
				.andExpect(status().isCreated()).andReturn();
		Assert.assertNotNull(result.getResponse().getContentAsString());
	}

	@Test
	public void test_002_postShouldRequireValidInput() throws Exception {
		mockMvc.perform(post("/api/tasks").contentType("application/json")
				.content("{\"capacity\": 1234, \"items\": []}")).andExpect(status().is(422));
	}

	@Test
	public void test_003_shouldGetRightNumOfTasksThere() throws Exception {

		Map<String, Map<Status, Long>> otherTasks = new HashMap<>();
		Map<Status, Long> otherTaskStatuses = new HashMap<>();
		otherTaskStatuses.put(Status.submitted, System.currentTimeMillis()/1000);
		otherTasks.put(RandomStringUtils.random(14, true,false ), otherTaskStatuses);
		when(taskStateManager.getStatuses()).thenReturn(otherTasks);

		MvcResult result = mockMvc.perform(get("/api/tasks").contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		Map<String, Map<Status, Long>> tasks = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Map<Status, Long>>>(){});
		int numOfTasks = tasks.keySet().size();

		Problem problem = new Problem(60, Arrays.asList(Item.from(10, 10),
				Item.from(20, 3), Item.from(33, 30)));
		when(knapsack.submitProblem(problem)).thenReturn(RandomStringUtils.random(14, true,false ));
		result = mockMvc
				.perform(post("/api/tasks").contentType("application/json")
						.content(jsonMapper.writeValueAsString(problem)))
				.andExpect(status().isCreated()).andReturn();
		Assert.assertNotNull(result.getResponse().getContentAsString());

		Map<Status, Long> otherTaskStatuses2 = new HashMap<>();
		otherTaskStatuses2.put(Status.submitted, System.currentTimeMillis()/1000);
		otherTasks.put(result.getResponse().getContentAsString(), otherTaskStatuses2);
		when(taskStateManager.getStatuses()).thenReturn(otherTasks);

		result = mockMvc.perform(get("/api/tasks").contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		tasks = jsonMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Map<Status, Long>>>(){});
		Assert.assertEquals(numOfTasks + 1, tasks.keySet().size());

	}

	@Test
	public void test_004_shouldGetTaskFromId() throws Exception {

		Problem problem = new Problem(60, Arrays.asList(Item.from(10, 10),
				Item.from(20, 3), Item.from(33, 30)));

		when(knapsack.submitProblem(problem)).thenReturn(RandomStringUtils.random(14, true,false ));
		MvcResult result = mockMvc
				.perform(post("/api/tasks").contentType("application/json")
						.content(jsonMapper.writeValueAsString(problem)))
				.andExpect(status().isCreated()).andReturn();
		Assert.assertNotNull(result.getResponse().getContentAsString());
		final String taskId = result.getResponse().getContentAsString();

		Task task = Task.from(taskId, problem);
		when(taskStateManager.getState(taskId)).thenReturn(Optional.of(task));

		result = this.mockMvc.perform(get("/api/tasks/{id}", taskId).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		Task task2 = jsonMapper.readValue(result.getResponse().getContentAsString(), Task.class);
		Assert.assertEquals(task, task2);

	}

	@Test
	public void test_005_shouldGetNotFoundTaskForNotThereClearly() throws Exception {

		when(taskStateManager.getState(any())).thenReturn(Optional.empty());
		this.mockMvc.perform(get("/api/tasks/{id}", 123456789).contentType("application/json"))
				.andExpect(status().isNotFound());
	}

	@Test // (timeout = 60000)
	public void test_006_shouldGetFinalTask() throws Exception {

		Problem problem = new Problem(60, Arrays.asList(Item.from(10, 10),
				Item.from(20, 3), Item.from(33, 30)));

		when(knapsack.submitProblem(problem)).thenReturn(RandomStringUtils.random(14, true,false ));
		MvcResult result = mockMvc
				.perform(post("/api/tasks").contentType("application/json")
						.content(jsonMapper.writeValueAsString(problem)))
				.andExpect(status().isCreated()).andReturn();
		Assert.assertNotNull(result.getResponse().getContentAsString());
		final String taskId = result.getResponse().getContentAsString();

		Task task = Task.from(taskId, problem);
		Solution solution = new Solution(Arrays.asList(Item.from(10, 10),Item.from(33, 30)), 1223456);
		task.setSolution(solution);
		task.getStatuses().put(Status.submitted, 123456789l);
		task.getStatuses().put(Status.started, 123456790l);
		task.getStatuses().put(Status.completed, 123456799l);
		when(taskStateManager.getState(taskId)).thenReturn(Optional.of(task));

		result = this.mockMvc.perform(get("/api/tasks/{id}", taskId).contentType("application/json"))
				.andExpect(status().isOk()).andReturn();
		Task task2 = jsonMapper.readValue(result.getResponse().getContentAsString(), Task.class);
		Assert.assertEquals(task, task2);

	}

}
