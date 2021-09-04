package it.arsinfo.smd.woocommerce.impl;

import it.arsinfo.smd.woocommerce.config.WooCommerceApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WooCommerceApiServiceConfigImpl implements WooCommerceApiConfig {

    @Value("${wooapi.url}")
    private String url;

    @Value("${wooapi.consumer.key}")
    private String key;

    @Value("${wooapi.consumer.secret}")
    private String secret;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getConsumerKey() {
        return key;
    }

    @Override
    public String getConsumerSecret() {
        return secret;
    }
}
