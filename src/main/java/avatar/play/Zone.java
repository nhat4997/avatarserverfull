package avatar.play;

import java.io.IOException;
import java.io.DataOutputStream;

import avatar.constants.Cmd;
import avatar.item.Item;
import avatar.model.Boss;
import avatar.model.Npc;
import avatar.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import avatar.network.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Zone {

    private Map map;
    private int id;
    private ArrayList<User> players;
    private MapService service;

    public Zone(Map map, int id) {
        this.map = map;
        this.id = id;
        this.players = new ArrayList<>();
        this.service = new MapService(null);
        this.service.setZone(this);
    }

    public User find(int id) {
        synchronized (players) {
            for (User us : players) {
                if (us.getId() == id) {
                    return us;
                }
            }
        }
        return null;
    }

    public void add(User us) {
        synchronized (players) {
            players.add(us);
        }
    }

    public void remove(User us) {
        synchronized (players) {
            players.remove(us);
        }
    }

    public void enter(User us, short x, short y) {
        try {
            Zone zone = us.getZone();
            if (zone != null) {
                zone.leave(us);
            }
            us.setZone(this);
            us.setX(x);
            us.setY(y);
            if (!(us instanceof Npc)) {
                getService().addPlayer(us);
                us.getService().weather((byte) 2);
                us.getAvatarService().enter(this);
            }
            add(us);
        } catch (Exception ex) {
            Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leave(User user) {
        remove(user);
        getService().leavePark(user.getId());
        user.setZone(null);
    }

    public void update() {

    }
}