//package code_wave.todo;
//
//import code_wave.todo.service.ShortTermWeatherAPI;
//import code_wave.todo.service.TodoService;
//import code_wave.todo.service.WeatherAPI;
//import code_wave.todo.repository.TodoRepository;
//import code_wave.todo.repository.UserRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.web.client.RestTemplate;
//
//@Configuration
//@ComponentScan(basePackages = "code_wave.todo")
//@EnableJpaRepositories(basePackages = "code_wave.todo.repository")
//public class AppConfig {
//
//    // Bean for the TodoService
//    @Bean
//    public TodoService todoService(TodoRepository todoRepository, UserRepository userRepository) {
//        return new TodoService(todoRepository, userRepository);
//    }
//
//    // Bean for the WeatherAPI
//    @Bean
//    public WeatherAPI weatherAPI() {
//        return new WeatherAPI();
//    }
//
//    // Bean for the ShortTermWeatherAPI
//    @Bean
//    public ShortTermWeatherAPI shortTermWeatherAPI() {
//        return new ShortTermWeatherAPI();
//    }
//
//    // Bean for RestTemplate (used for external API calls)
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//}
