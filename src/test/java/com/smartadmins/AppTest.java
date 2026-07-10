package com.cloudtechnet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {

    @Test
    void testAdd() {
        assertEquals(10, App.add(5,5));
    }
}