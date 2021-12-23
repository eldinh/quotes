package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import ru.sfedu.utils.IdGenerator;
import ru.sfedu.utils.SecurityCsvConverter;

import java.util.Objects;

public class Action {
    @Attribute
    @CsvBindByPosition(position = 0)
    String id;
    @Attribute
    @CsvBindByPosition(position = 1)
    String date;
    @Attribute
    @CsvBindByPosition(position = 2)
    ActionType action;
    @Attribute
    @CsvBindByPosition(position = 3)
    String userID;
    @Element
    @CsvCustomBindByPosition(position = 4, converter = SecurityCsvConverter.class)
    Security security;


    public Action() {
        this.id = IdGenerator.generate();
    }

    public Action(String date, ActionType action, String userID, Security security) {
        this();
        this.date = date;
        this.action = action;
        this.userID = userID;
        this.security = security;
    }

    public Action(ActionBuilder actionBuilder){
        this.id = actionBuilder.getId();
        if (this.id == null)
            id = IdGenerator.generate();
        this.date = actionBuilder.getDate();
        this.action = actionBuilder.getAction();
        this.userID = actionBuilder.getUserID();
        this.security = actionBuilder.getSecurity();
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public ActionType getAction() {
        return action;
    }

    public String getUserID() {
        return userID;
    }

    public Security getSecurity() {
        return security;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action1 = (Action) o;
        return Objects.equals(id, action1.id) && Objects.equals(date, action1.date) && action == action1.action
                && Objects.equals(userID, action1.userID) && Objects.equals(security, action1.security);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, action, userID, security);
    }

    @Override
    public String toString() {
        return "Action{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", action=" + action +
                ", userID='" + userID + '\'' +
                ", security=" + security +
                "}\n";
    }
}
