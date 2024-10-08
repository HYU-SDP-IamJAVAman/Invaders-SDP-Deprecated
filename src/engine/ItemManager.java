package engine;

import entity.Bullet;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.lang.Math.max;

/**
 * Manages item logic
 *
 * @author Seochan Moon
 */

public class ItemManager {
    private static final int ITEM_DROP_PROBABILITY = 99;

    private ItemType itemType;
    private boolean GhostActive;
    private int shotNum;
    private Random rand;
    private Ship ship;
    private EnemyShipFormation enemyShipFormation;

    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation) {
        this.itemType = null;
        this.GhostActive = false;
        this.shotNum = 1;
        this.rand = new Random();
        this.ship = ship;
        this.enemyShipFormation = enemyShipFormation;
    }

    public enum ItemType {
        Bomb,
        LineBomb,
        Barrier,
        Ghost,
        TimeStop,
        MultiShot
    }

    public boolean dropItem() {
        return (rand.nextInt(100) + 1) <= ITEM_DROP_PROBABILITY;
    }

    public ItemType selectItemType() {
        switch (rand.nextInt(6)) {
            case 0:
                this.itemType = ItemType.Bomb;
                break;
            case 1:
                this.itemType = ItemType.LineBomb;
                break;
            case 2:
                this.itemType = ItemType.Barrier;
                break;
            case 3:
                this.itemType = ItemType.Ghost;
                break;
            case 4:
                this.itemType = ItemType.TimeStop;
                break;
            case 5:
                this.itemType = ItemType.MultiShot;
                break;
        }

        return this.itemType;
    }

    public void operateBomb() {}

    public void operateLineBomb(Set<Bullet> recyclable, Bullet bullet, int shipsDestroyed, int score) {
        int maxRow = 0;
        List<List<EnemyShip>> enemyShips = this.enemyShipFormation.getEnemyShips();

        for (List<EnemyShip> ship : enemyShips) {
            maxRow = max(maxRow, ship.size() - 1);
        }

        List<EnemyShip> destroyList = new ArrayList<>();

        for (int i = 0; i < enemyShips.size(); i++) {
            for (int j = 0; j < enemyShips.get(i).size(); j++) {
                EnemyShip enemyShip = enemyShips.get(i).get(j);
                if (i == maxRow && !enemyShip.isDestroyed()) {
                    destroyList.add(enemyShip);
                }
            }
        }

        for (EnemyShip destroyedShip : destroyList) {
            score += destroyedShip.getPointValue();
            shipsDestroyed++;
            enemyShipFormation.destroy(destroyedShip);
            recyclable.add(bullet);
        }
    }

    public void operateBarrier() {}

    public void operateGhost() {
        this.GhostActive = true;
        this.ship.setColor(Color.DARK_GRAY);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                this.GhostActive = false;
                this.ship.setColor(Color.GREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void operateTimeStop() {}

    public void operateMultiShot() {
        if (this.shotNum < 3) {
            this.shotNum++;
        }
    }

    public int getShotNum() {
        return shotNum;
    }

    public boolean isGhostActive() {
        return GhostActive;
    }
}
