package api;

import junit.framework.TestCase;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseTest extends TestCase {
    List<User> users = new ArrayList<>(Arrays.asList(
            new User(0, "Andrew", 29)
            , new User(1, "NorAdeww", 10)
            , new User(2, "Dinh", 19)
            , new User(3, "Rodion", 19)
            , new User(4, "Ev", 20)
            , new User(5, "Danil", 19)
            , new User(6, "Artem", 19)
            , new User(7, "Sanya", 19)));

    List<User> userWithoutID = new ArrayList<>(Arrays.asList(
            new User("Andrew", 29)
            , new User( "NorAdeww", 10)
            , new User( "Dinh", 19)
            , new User( "Rodion", 19)
            , new User( "Ev", 20)
            , new User( "Danil", 19)
            , new User( "Artem", 19)
            , new User( "Sanya", 19)
    ));
    Result<User> result;

}
