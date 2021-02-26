package com.kalsym.usersservice;

import com.kalsym.usersservice.models.daos.Authority;
import com.kalsym.usersservice.repositories.AuthoritiesRepository;
import com.kalsym.usersservice.utils.Logger;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 *
 * @author Sarosh
 */
@Component
public class ListenerBean {

    @Autowired
    AuthoritiesRepository authoritiesRepository;

    @EventListener
    public void handleEvent(ContextRefreshedEvent event) {

        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            Map<RequestMappingInfo, HandlerMethod> map = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();

            map.forEach((RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) -> {
                try {
                    if (!handlerMethod.getMethod().getName().equalsIgnoreCase("error")
                            && !handlerMethod.getMethod().getName().equalsIgnoreCase("errorHtml")) {

                        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "name: " + requestMappingInfo.getName());
                        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "method: " + handlerMethod.getMethod().getName());
                        Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "description: " + requestMappingInfo.toString());
                        Authority authority = new Authority();
                        authority.setId(requestMappingInfo.getName());
                        authority.setName(handlerMethod.getMethod().getName());
                        authority.setDescription(requestMappingInfo.toString());
                        authority.setServiceId("users-service");

                        if (null != authority.getId()) {

                            authoritiesRepository.save(authority);
                            Logger.application.info(Logger.pattern, UsersServiceApplication.VERSION, "", "inserted authority", "");
                        }

                    }

                } catch (Exception e) {
                    Logger.application.warn(Logger.pattern, UsersServiceApplication.VERSION, "error inserting authority", e.getMessage());
                }

            });
        }
    }
}
