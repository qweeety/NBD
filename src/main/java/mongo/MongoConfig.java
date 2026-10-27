package mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mongo.model.AthleteDoc;
import mongo.model.GymRoomDoc;
import mongo.model.PoolRoomDoc;
import mongo.model.TrainingDoc;
import mongo.model.TrainingRoomDoc;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConfig implements AutoCloseable {

    public static final String DEFAULT_CONNECTION_STRING =
            "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0"
    public static final String DATABASE_NAME = "trainingcenter";

    public static final String ATHLETES = "athletes";
    public static final String TRAININGS = "trainings";
    public static final String TRAINING_ROOMS = "trainingRooms";

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoConfig() {
        this(DEFAULT_CONNECTION_STRING, DATABASE_NAME);
    }

    public MongoConfig(String connectionString, String databaseName) {
        CodecRegistry pojoRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .register(AthleteDoc.class,
                                TrainingDoc.class,
                                TrainingRoomDoc.class,
                                GymRoomDoc.class,
                                PoolRoomDoc.class)
                        .build())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(pojoRegistry)
                .writeConcern(WriteConcern.MAJORITY)
                .retryWrites(true)
                .retryReads(true)
                .applyToClusterSettings(b -> b.serverSelectionTimeout(15, TimeUnit.SECONDS))
                .build();

        this.client = MongoClients.create(settings);
        this.database = client.getDatabase(databaseName);
    }

    public MongoClient getClient() { return client; }

    public MongoDatabase getDatabase() { return database; }

    public MongoCollection<AthleteDoc> getAthletes() {
        return database.getCollection(ATHLETES, AthleteDoc.class);
    }

    public MongoCollection<TrainingDoc> getTrainings() {
        return database.getCollection(TRAININGS, TrainingDoc.class);
    }

    public MongoCollection<TrainingRoomDoc> getTrainingRooms() {
        return database.getCollection(TRAINING_ROOMS, TrainingRoomDoc.class);
    }

    @Override
    public void close() {
        client.close();
    }
}