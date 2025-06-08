package integral.studios.hydro.service;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import integral.studios.hydro.Hydro;
import integral.studios.hydro.util.config.Configuration;
import lombok.Getter;
import org.bson.Document;

@Getter
public class DataBaseService {
    private final Configuration config = Hydro.get().getConfiguration();

    private MongoClient client;

    private MongoDatabase database;

    private MongoCollection<Document> logsCollection;

    public DataBaseService() {
        if (!Hydro.get().getConfiguration().isDatabaseEnabled()) {
            return;
        }

        client = new MongoClient(new ServerAddress(config.getDatabaseHost()));

        database = client.getDatabase(config.getDatabaseName());
        logsCollection = database.getCollection(config.getDatabaseLogsName());
    }
}