package com.example.graficdemo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    /**
     * Test per il metodo main.
     */
    @Test
    void main() {
        assertDoesNotThrow(() -> App.main(new String[]{"127.0.0.1", "8080"}));
    }
}