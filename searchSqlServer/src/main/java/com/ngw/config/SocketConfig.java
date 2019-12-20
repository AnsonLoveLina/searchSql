package com.ngw.config;

import com.ngw.service.SocketService;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zy-xx on 2019/11/4.
 */
@Configuration
public class SocketConfig {

    @Value("${socketIO.url}")
    private String url;

    @Bean
    public SocketService socketService() {
        SocketService socketService = new SocketService();
        socketService.socketConnect(url);
        return socketService;
    }
}
