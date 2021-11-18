package ru.sfedu.model.dao;

import com.opencsv.bean.CsvBindByPosition;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    @CsvBindByPosition(position = 0)
    private Long id;


    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByPosition(position = 2)
    private Integer age;

    public User(long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public User(){}

    @Override
    public String toString() {
        return "User [Id=" + id +
                ", Name=" + name +
                ", Age=" + age +
                "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(age, user.getAge()) && Objects.equals(name, user.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

