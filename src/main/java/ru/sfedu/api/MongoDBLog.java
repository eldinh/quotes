package ru.sfedu.api;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.sfedu.model.CommandName;
import ru.sfedu.model.RepositoryName;
import ru.sfedu.utils.ValidEntityListValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static ru.sfedu.Constants.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class MongoDBLog {
    private static final Logger log = LogManager.getLogger(MongoDBLog.class.getName());

    private static <T> MongoCollection<Document> getCollection(Class<T> pojoClass) throws Exception {
        log.info("Starting MongoDBLog getCollection[0]");
        log.info("getCollection[1]: Class: {}", pojoClass);
        try {
            String MONGODB_URI = String.format(getConfigurationEntry(MONGODB_CONNECT_URI),
                    getConfigurationEntry(MONGODB_USER), getConfigurationEntry(MONGODB_PASSWORD));

            CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            log.debug("getCollection[2]: Get collection from database");
            return new MongoClient(new MongoClientURI(MONGODB_URI))
                    .getDatabase(getConfigurationEntry(MONGODB_DATABASE))
                    .withCodecRegistry(codecRegistry)
                    .getCollection(pojoClass.getSimpleName());
        } catch (Exception e){
            log.error("Function MongoDBLog getCollection had failed[3]");
            throw new Exception(e);
        }

    }
    public static  <T> void save(CommandName command, RepositoryName repository , List<T> changes) throws Exception {
        log.info("Starting MongoDBLog save[4]");
        try {
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
            log.error("Function MongoDBLog save had failed[7]: {}", e.getMessage());
        }
    }

    public static <T> void save(CommandName command, RepositoryName repository , T changes) throws Exception {
        save(command, repository, new ArrayList<>(List.of(changes)));
    }


}
