package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    @Attribute
    @CsvBindByPosition(position = 0)
    private Long id;
    @Attribute
    @CsvBindByPosition(position = 1)
    private String name;
    @Attribute
    @CsvBindByPosition(position = 2)
    private int age;

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
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
