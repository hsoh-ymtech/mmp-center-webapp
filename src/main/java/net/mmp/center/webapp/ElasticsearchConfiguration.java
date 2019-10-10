package net.mmp.center.webapp;

import com.netflix.client.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import java.net.InetAddress;
import java.net.UnknownHostException;

//@EnableElasticsearchRepositories
@Configuration
@EnableElasticsearchRepositories(basePackages = "net.mmp.center.webapp")
public class ElasticsearchConfiguration {
    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.cluster_name}")
    private String clusterName;

    @Bean
    public Client client() throws UnknownHostException {
        /*
        Settings settings = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", clusterName).build();
         */
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));

        return client;
    }

    /*
    @Bean public Client client() throws Exception {
        Settings settings = Settings.builder().put("cluster.name", clusterName).build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        return client;
    }
*/
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() throws Exception {
        return new ElasticsearchTemplate(client());
    }
}
