package engine;

import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

/**
 * Manages item logic
 *
 * @author Seochan Moon
 */

public class ItemManager {
    private static final int ITEM_DROP_PROBABILITY = 99;

    private ItemType itemType;
    private boolean GhostActive;
    private boolean timeStopActive;
    private int shotNum;
    private Random rand;
    private boolean isMaxShotNum = false;
    private Ship ship;
    private EnemyShipFormation enemyShipFormation;

    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation) {
        this.itemType = null;
        this.ghostActive = false;
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
        if (!isMaxShotNum) {
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
        } else {
            switch (rand.nextInt(5)) {
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
            }
        }

        return this.itemType;
    }

    public void operateBomb() {}

    public Entry<Integer, Integer> operateLineBomb() {
        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShips();
        int targetRow = -1;
        int maxCnt = -1;

        for (int i = 0; i < enemyships.size(); i++) {
            int aliveCnt = 0;
            for (int j = 0; j < enemyships.get(i).size(); j++) {
                if (enemyships.get(i).get(j) != null && !enemyships.get(i).get(j).isDestroyed()) {
                    aliveCnt++;
                }
            }

            if (aliveCnt > maxCnt) {
                maxCnt = aliveCnt;
                targetRow = i;
            }
        }

        if (targetRow != -1) {
            List<EnemyShip> destroyList = new ArrayList<>(enemyships.get(targetRow));
            for (EnemyShip destroyedShip : destroyList) {
                addScore += destroyedShip.getPointValue();
                addShipsDestroyed++;
                enemyShipFormation.destroy(destroyedShip);
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    public void operateBarrier() {}

    public void operateGhost() {
        this.ghostActive = true;
        this.ship.setColor(Color.DARK_GRAY);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                this.ghostActive = false;
                this.ship.setColor(Color.GREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void operateTimeStop() {
        this.timeStopActive = true;
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                this.timeStopActive = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void operateMultiShot() {
        if (this.shotNum < 3) {
            this.shotNum++;
            if (shotNum == 3) {
                isMaxShotNum = true;
            }
        }
    }

    public int getShotNum() {
        return this.shotNum;
    }

    public boolean isGhostActive() {
        return this.ghostActive;
    }

    public boolean isTimeStopActive() {
        return this.timeStopActive;
    }
}
