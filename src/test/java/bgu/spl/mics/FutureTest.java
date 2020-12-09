package bgu.spl.mics;

import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp() {
        future = new Future<>();
    }

    @Test
    public void testResolve() {
        String str = "someResult";
        assertFalse(future.isDone());
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGet() {
        //first  - this check that its wait for resolve to be done
        assertThrows(IllegalMonitorStateException.class, ()->future.get(500, TimeUnit.MILLISECONDS));
        //second  - this check that its bring the right result
        String str = "someResult";
        future.resolve(str);
        String result = future.get(500,TimeUnit.MILLISECONDS);
        assertTrue(str.equals(result));
        //third - check what happen after the get method works
        assertTrue(str.equals(future.get(750,TimeUnit.MILLISECONDS)));
        assertTrue(future.isDone());
        //check for second assigment
        String result2="someResult2";
        future.resolve(result2);
        assertTrue(result2.equals(future.get(1500,TimeUnit.MILLISECONDS)));

        //same for get(time,timeunit)
        //first  - this check that its wait for resolve to be done
        assertFalse(future.get(1,TimeUnit.SECONDS) == null);
        //second  - this check that its bring the right result
        String str1 = "someResult1";
        future.resolve(str1);
        String result1 = future.get(1,TimeUnit.SECONDS);
        assertTrue(str1.equals(result1));
        //third - check what happen after the get method works
        assertTrue(str1.equals(future.get(1,TimeUnit.SECONDS)));
        assertTrue(future.isDone());
        //check for second assigment
        String result3="someResult3";
        future.resolve(result3);
        assertTrue(result3.equals(future.get()));
    }


    @Test
    void testIsDone() {
        assertFalse(future.isDone());

        future.resolve("someResulte");
        assertTrue(future.isDone());

        future.get();
        assertTrue(future.isDone());

        future.get(1, TimeUnit.SECONDS);
        assertTrue(future.isDone());
    }
}