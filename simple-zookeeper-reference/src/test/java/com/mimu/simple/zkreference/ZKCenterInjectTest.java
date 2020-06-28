package com.mimu.simple.zkreference;


import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 author: mimu
 date: 2020/4/25
 */
public class ZKCenterInjectTest {

    @Test
    public void info(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ApplicationConfig.class);
        context.refresh();
        ZKPropertyModel bean = context.getBean(ZKPropertyModel.class);
        while (true){
            System.out.println(bean.getAge());
            System.out.println(bean.getName());
            System.out.println(bean.isaBoolean());
            System.out.println(bean.getaBBoolean());
            System.out.println(bean.getTmp());
            System.out.println(bean.getInner());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
