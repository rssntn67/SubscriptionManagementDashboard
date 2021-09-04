package it.arsinfo.smd;

import it.arsinfo.smd.woocommerce.api.WooCommerceApiService;
import it.arsinfo.smd.woocommerce.config.WooCommerceApiConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UIUserTests {


    @Autowired
    private WooCommerceApiService wooCommerceApiService;

    @Autowired
    private WooCommerceApiConfig wooCommerceApiConfig;

    @BeforeEach
    public void onSetUp() {

    }

    @AfterEach
    public void onTearDown() {
    }

    @Test
    public void testConfiguration() {
        assertNotNull(wooCommerceApiService);
        assertNotNull(wooCommerceApiConfig);
        assertEquals("https://www.retepreghierapapa.it",wooCommerceApiConfig.getUrl());

    }

}