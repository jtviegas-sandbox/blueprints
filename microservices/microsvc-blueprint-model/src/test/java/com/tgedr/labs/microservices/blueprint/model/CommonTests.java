package com.tgedr.labs.microservices.blueprint.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgedr.labs.microservices.blueprint.model.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void contextLoads() throws JsonParseException, JsonMappingException, IOException {
		String obj = "{\"id\": \"hdsfkuierhf7834gfvdhgjvdjbre\", \"problem\": {\"capacity\": 60, \"items\": [ { \"weight\": 10, \"value\": 10 }, { \"weight\": 20, \"value\": 3 } , { \"weight\": 33, \"value\": 30 } ] } "
				+ ", \"solution\": { \"items\": [{ \"weight\": 10, \"value\": 10 }], \"time\": 1234 } " +
				", \"statuses\":{\"submitted\": 1505225308, \"started\": 1505225320, \"completed\": null } }";
		Task task = objectMapper.readValue(obj, Task.class);
		Assert.assertEquals( 3, task.getProblem().getItems().size());
		Assert.assertTrue( 1234 == task.getSolution().getTime());
		Assert.assertEquals( 1, task.getSolution().getItems().size());
		Assert.assertEquals( 3, task.getStatuses().size());
	}

}
