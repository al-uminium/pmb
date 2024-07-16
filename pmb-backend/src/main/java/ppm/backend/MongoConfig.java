// package ppm.backend;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.mongodb.core.MongoTemplate;

// import com.mongodb.client.MongoClient;
// import com.mongodb.client.MongoClients;

// @Configuration
// public class MongoConfig {

//   @Value("${spring.data.mongodb.uri}")
//   private String mongoUri;

//   @Bean
//   public MongoTemplate createMongoTemplate() {
//     MongoClient client = MongoClients.create(mongoUri);
//     return new MongoTemplate(client, "test");
//   }
// }
