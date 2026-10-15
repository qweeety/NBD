package jpa.mappers;

import domain.GymRoom;
import domain.PoolRoom;
import domain.TrainingRoom;
import jpa.model.GymRoomEnt;
import jpa.model.PoolRoomEnt;
import jpa.model.TrainingRoomEnt;

public class TrainingRoomMapper {

    public static TrainingRoomEnt toEnt(TrainingRoom room) {
        if (room instanceof GymRoom gym) {
            return new GymRoomEnt(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (room instanceof PoolRoom pool) {
            return new PoolRoomEnt(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room type");
    }

    public static TrainingRoom toDomain(TrainingRoomEnt ent) {
        if (ent instanceof GymRoomEnt gym) {
            return new GymRoom(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (ent instanceof PoolRoomEnt pool) {
            return new PoolRoom(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room entity type");
    }
}