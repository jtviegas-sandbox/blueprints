package com.tgedr.labs.microservices.blueprint.services.statemanager;

import com.google.common.collect.ImmutableSet;
import com.tgedr.labs.microservices.blueprint.model.Item;
import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.model.Status;
import com.tgedr.labs.microservices.blueprint.model.Task;
import com.tgedr.labs.microservices.blueprint.services.statemanager.exceptions.StateManagerException;
import com.tgedr.labs.microservices.blueprint.store.Store;
import com.tgedr.labs.microservices.blueprint.store.exceptions.StoreException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StateManagerTests {

	@Autowired
	private StateManager service;

	@MockBean
	private Store store;

	@Test
	public void setStateOK() throws Exception {
		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;

		Task task = Task.from("xpto", problem);
		service.setState(task);

		Assert.assertTrue(true);
	}

	@Test(expected = StateManagerException.class)
	public void setStateNotOK() throws Exception {
		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;

		Task task = Task.from("xpto", problem);
		doThrow(StoreException.class).when(store).save(task);
		service.setState(task);
	}

	@Test
	public void getStatusOK() throws Exception {
		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;
		Task task = Task.from("xpto", problem);
		task.getStatuses().put(Status.completed, 123456l);

		when(store.getTask(any())).thenReturn(Optional.of(task));
		Assert.assertEquals(task.getStatuses(), service.getStatus(task.getId()));

	}

	@Test(expected = StateManagerException.class)
	public void getStatusNotOK() throws Exception {
		doThrow(StoreException.class).when(store).getTask(any());
		service.getStatus("xpto");
	}

	@Test
	public void getStateOK() throws Exception {
		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;
		Task task = Task.from("xpto", problem);
		task.getStatuses().put(Status.completed, 123456l);

		when(store.getTask(any())).thenReturn(Optional.of(task));
		Assert.assertEquals(task, service.getState(task.getId()).get());

	}

	@Test(expected = StateManagerException.class)
	public void getStateNotOK() throws Exception {
		doThrow(StoreException.class).when(store).getTask(any());
		service.getState("xpto");
	}

	@Test
	public void getStatusesOK() throws Exception {
		Problem problem = new Problem(32,
				Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
						, Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
		) ;
		Task task = Task.from("xpto", problem);
		task.getStatuses().put(Status.completed, 123456l);

		Set<Task> tasks = ImmutableSet.of(task );

		Map<String, Map<Status, Long>> statuses = new HashMap<>();
		for( Task t: tasks)
			statuses.put(t.getId(), t.getStatuses());


		when(store.getTasks()).thenReturn(tasks);
		Assert.assertEquals(statuses, service.getStatuses());

	}

	@Test(expected = StateManagerException.class)
	public void getStatusesNotOK() throws Exception {
		doThrow(StoreException.class).when(store).getTasks();
		service.getStatuses();
	}

}
