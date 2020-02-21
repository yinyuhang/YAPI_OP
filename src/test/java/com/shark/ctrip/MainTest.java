package com.shark.ctrip;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MainTest {
    @Test
    public void test1() {
        for (Method m : A.class.getMethods()) {
            for (Parameter p : m.getParameters()) {
                System.out.println(p.getName());
                System.out.println(p.getParameterizedType());
            }
        }
    }

    public interface A {
        void a(String a, Integer i);
    }
}
