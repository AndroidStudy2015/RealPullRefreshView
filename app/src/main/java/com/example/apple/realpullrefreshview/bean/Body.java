package com.example.apple.realpullrefreshview.bean;

/**
 * Created by apple on 2017/7/14.
 */

public class Body {
   private String name ;
   private int age ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Body(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
