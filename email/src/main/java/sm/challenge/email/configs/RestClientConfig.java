package sm.challenge.email.configs;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate(requestFactory());
    }

    private ClientHttpRequestFactory requestFactory() {
        /* @formatter:off */
        RequestConfig config = RequestConfig
            .custom()
            .setSocketTimeout(5000)
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .build();
        
        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Unable to initialise SSL context", e);
            throw new RuntimeException(e);
        }
        
        HttpClient client = HttpClients
            .custom()
            .setDefaultRequestConfig(config)
            .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
            .build();
        /* @formatter:on */
        return new HttpComponentsClientHttpRequestFactory(client);
    }

}
