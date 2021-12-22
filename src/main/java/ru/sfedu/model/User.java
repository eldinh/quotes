package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import ru.sfedu.utils.IDGenerator;
import ru.sfedu.utils.SecurityListCsvConverter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class User implements Serializable {
    @Attribute
    @CsvBindByPosition(position = 0)
    private String id;
    @Attribute
    @CsvBindByPosition(position = 1)
    private String name;
    @ElementList
    @CsvCustomBindByPosition(position = 2, converter = SecurityListCsvConverter.class)
    private List<Security> securityList;
    @ElementList
    @CsvBindByPosition(position = 3)
    private List<Action> actionHistory;

    public User(){}

    public User(UserBuilder userBuilder){
        this.id = userBuilder.getId();
        if (userBuilder.getId()== null)
            this.id = IDGenerator.generate();
        this.name = userBuilder.getName();
        this.securityList = userBuilder.getTickerList();
        this.actionHistory = userBuilder.getActionHistory();
    }



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Security> getSecurityList() {
        return securityList;
    }

    public List<Action> getActionHistory() {
        return actionHistory;
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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", securityList=" + securityList +
                ", actionHistory=" + actionHistory +
                '}';
    }
}

