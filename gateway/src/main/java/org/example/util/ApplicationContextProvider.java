package org.example.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 用于获取 Spring 的 ApplicationContext 对象
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * 获取 ApplicationContext 对象
     * @return ApplicationContext 对象
     */
    public static ApplicationContext getContext() {
        return applicationContext;
    }
}
