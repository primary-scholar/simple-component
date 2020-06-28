package com.mimu.simple.zkreference;


import org.springframework.stereotype.Component;

/**
 author: mimu
 date: 2020/4/25
 */
@Component
public class ZKPropertyModel {

    private int age;
    private Integer iage;
    private String name;
    private boolean aBoolean;
    private Boolean aBBoolean;
    private String tmp;
    private InnerModel inner;

    @ZKReference(key = "abc", value = "1")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @ZKReference(key = "abc", value = "1")
    public Integer getIage() {
        return iage;
    }

    public void setIage(Integer iage) {
        this.iage = iage;
    }

    @ZKReference(key = "abd", value = "1")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ZKReference(key = "abe", value = "1")
    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    @ZKReference(key = "abe", value = "1")
    public Boolean getaBBoolean() {
        return aBBoolean;
    }

    public void setaBBoolean(Boolean aBBoolean) {
        this.aBBoolean = aBBoolean;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    @ZKReference(key = "abf", value = "")
    public InnerModel getInner() {
        return inner;
    }

    public void setInner(InnerModel inner) {
        this.inner = inner;
    }

    static class InnerModel {
        private int age;
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "InnerModel{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

}
