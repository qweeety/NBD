package mongo.mappers;

import domain.GymRoom;
import domain.PoolRoom;
import domain.TrainingRoom;
import mongo.model.GymRoomDoc;
import mongo.model.PoolRoomDoc;
import mongo.model.TrainingRoomDoc;

public class TrainingRoomMapper {

    public static TrainingRoomDoc toDoc(TrainingRoom room) {
        if (room instanceof GymRoom gym) {
            return new GymRoomDoc(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (room instanceof PoolRoom pool) {
            return new PoolRoomDoc(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room type: " + room.getClass().getName());
    }

    public static TrainingRoom toDomain(TrainingRoomDoc doc) {
        if (doc instanceof GymRoomDoc gym) {
            return new GymRoom(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (doc instanceof PoolRoomDoc pool) {
            return new PoolRoom(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room document type: " + doc.getClass().getName());
    }
}