package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {
    Ewok specific_ewok;

    @BeforeEach
    void setUp() {
        specific_ewok = new Ewok(42);
        // Make sure specific_ewok.serialNumber is 42:
        assertEquals(specific_ewok.getSerialNumber(), 42);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testAcquire() {
        assertTrue(specific_ewok.isAvailable());
        specific_ewok.acquire();
        assertFalse(specific_ewok.isAvailable());
    }

    @Test
    void testRelease() {
        specific_ewok.acquire();
        assertFalse(specific_ewok.isAvailable());
        specific_ewok.release();
        assertTrue(specific_ewok.isAvailable());
    }
}