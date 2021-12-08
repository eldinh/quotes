package ru.sfedu.builder;

import ru.sfedu.entity.User;

public class UserBuilder implements IUserBuilder{
    private Long id;
    private String name;
    private int age;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User getResult(){
        return new User(id, name, age);
    }
}
