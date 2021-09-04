package it.arsinfo.smd;

import it.arsinfo.smd.dao.*;
import it.arsinfo.smd.entity.AreaSpedizione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpesaSpedizione;
import it.arsinfo.smd.helper.SmdHelper;
import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.service.api.WooCommerceOrderService;
import it.arsinfo.smd.service.impl.SmdServiceImpl;
import it.arsinfo.smd.service.impl.WooCommerceOrderServiceDaoImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UIUserTests {

    
    @BeforeEach
    public void onSetUp() {

    }

    @AfterEach
    public void onTearDown() {
    }

    @Test
    public void testConfiguration() {
    }

}