package jpa.mappers;

import domain.GymRoom;
import domain.PoolRoom;
import domain.TrainingRoom;
import jpa.model.GymRoomEnt;
import jpa.model.PoolRoomEnt;
import jpa.model.TrainingRoomEnt;
import org.hibernate.Hibernate;

public final class TrainingRoomMapper {

    private TrainingRoomMapper() { }

    public static TrainingRoomEnt toEnt(TrainingRoom room) {
        if (room instanceof GymRoom gym) {
            return new GymRoomEnt(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (room instanceof PoolRoom pool) {
            return new PoolRoomEnt(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room type: " + room.getClass().getName());
    }

    public static TrainingRoom toDomain(TrainingRoomEnt ent) {
        // Referencja leniwa (@ManyToOne LAZY) bywa proxy klasy bazowej, na którym instanceof
        // podklasy by zawiódł, więc najpierw pobieramy właściwy obiekt.
        Object real = Hibernate.unproxy(ent);
        if (real instanceof GymRoomEnt gym) {
            return new GymRoom(gym.getId(), gym.getRoomNumber(), gym.isArchived(), gym.getEquipment());
        }
        if (real instanceof PoolRoomEnt pool) {
            return new PoolRoom(pool.getId(), pool.getRoomNumber(), pool.isArchived(), pool.getLaneCount());
        }
        throw new IllegalArgumentException("Unknown room entity type: " + real.getClass().getName());
    }
}
