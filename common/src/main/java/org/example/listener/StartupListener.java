package org.example.listener;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 在应用程序启动时执行的初始化代码
        System.out.println("Application started.");
    }
}
