package ru.sfedu.api;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.sfedu.model.CommandType;
import ru.sfedu.model.RepositoryType;
import ru.sfedu.utils.ValidEntityListValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static ru.sfedu.Constants.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class MongoHistory {
    private static final Logger log = LogManager.getLogger(MongoHistory.class.getName());

    private static Boolean writeToHistory = true;

    public static void disable(){
        writeToHistory = false;
    }

    private static <T> MongoCollection<Document> getCollection(Class<T> pojoClass) throws Exception {
        log.info("Starting MongoHistory getCollection[0]");
        log.info("getCollection[1]: Class: {}", pojoClass);
        try {
            String mongoURI = String.format(getConfigurationEntry(MONGODB_CONNECT_URI),
                    getConfigurationEntry(MONGODB_USER), getConfigurationEntry(MONGODB_PASSWORD));

            CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            log.debug("getCollection[2]: Get collection from database");
            return new MongoClient(new MongoClientURI(mongoURI))
                    .getDatabase(getConfigurationEntry(MONGODB_DATABASE))
                    .withCodecRegistry(codecRegistry)
                    .getCollection(pojoClass.getSimpleName());
        } catch (Exception e){
            log.error("Function MongoHistory getCollection had failed[3]");
            throw new Exception(e);
        }

    }
    public static  <T> void save(CommandType command, RepositoryType repository , List<T> changes) throws Exception {
        log.info("Starting MongoHistory save[4]");
        try {
            if (!writeToHistory)
                throw new Exception("Writing to history is disable");
            log.info("save[5]: {}, {}, {}", command, repository, changes);
            ValidEntityListValidator.isValid(changes);
            MongoCollection<Document> collection = getCollection(changes.get(0).getClass());
            Document document = new Document();
            document.put(MONGODB_TIME_FIELD, new Date());
            document.put(MONGODB_COMMAND_FIELD, command.toString());
            document.put(MONGODB_REPOSITORY_FIELD, repository.toString());
            document.put(MONGODB_CHANGES_FIELD, changes);
            log.debug("save[6]: save the info to log");
            collection.insertOne(document);
        }catch (Exception e){
            log.error("Function MongoHistory save had failed[7]: {}", e.getMessage());
        }
    }

    public static <T> void save(CommandType command, RepositoryType repository , T changes) throws Exception {
        save(command, repository, new ArrayList<>(List.of(changes)));
    }


}
