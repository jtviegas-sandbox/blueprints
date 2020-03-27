package com.tgedr.labs.microservices.blueprint.store.dynamodb;


import com.tgedr.labs.microservices.blueprint.model.*;
import com.tgedr.labs.microservices.blueprint.store.Store;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreTests {

    @Autowired
    private Store store;

    @Test
    public void test_001_getItAndCheckIt() throws Exception {

        String id = RandomStringUtils.random(12);
        Task expected = Task.from( id, Problem.from(32,
                Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
        ) );

        expected.setSolution(Solution.from(Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))), 123));
        expected.getStatuses().put(Status.completed, 123456l);

        store.save(expected);
        Task actual = store.getTask(id).get();
        Assert.assertEquals(expected, actual);
        Assert.assertFalse(store.getTask(RandomStringUtils.random(12)).isPresent());
    }

    @Test
    public void test_002_unknownTask() throws Exception {
        Optional<Task> expected = store.getTask(RandomStringUtils.random(12));
        Assert.assertFalse(expected.isPresent());
    }

    @Test
    public void test_003_shouldGetRightNUmOfTasks() throws Exception {

        int initialNum = store.getTasks().size();
        Task expected = Task.from( RandomStringUtils.random(12), Problem.from(32,
                Arrays.asList(Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32))
                        , Item.from(RandomUtils.nextInt(1, 32), RandomUtils.nextInt(1, 32)))
        ) );
        store.save(expected);
        Assert.assertEquals(initialNum + 1, store.getTasks().size());
    }
}
