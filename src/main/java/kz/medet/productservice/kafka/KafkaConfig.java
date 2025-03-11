package kz.medet.productservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("product.to.order.responses").build();
    }

    @Bean
    public NewTopic newTopic2(){
        return TopicBuilder.name("product.to.order.ProductCreated").build();
    }

}
