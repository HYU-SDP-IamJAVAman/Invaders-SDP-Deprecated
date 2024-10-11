package engine;

import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;
import entity.Barrier;

import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Random;
import java.util.Set;

/**
 * Manages item logic
 *
 * @author Seochan Moon
 */

public class ItemManager {
    private static final int ITEM_DROP_PROBABILITY = 100;

    private ItemType itemType;
    private boolean timeStopActive;
    private boolean ghostActive;
    private int shotNum;
    private Random rand;
    private Ship ship;
    private EnemyShipFormation enemyShipFormation;
    private boolean isMaxShotNum;

    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation) {
        this.itemType = null;
        this.ghostActive = false;
        this.shotNum = 1;
        this.rand = new Random();
        this.ship = ship;
        this.enemyShipFormation = enemyShipFormation;
        this.isMaxShotNum = false;
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

    public Entry<Integer, Integer> operateBomb() {
        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShips();
        int enemyShipsSize = enemyships.size();

        int maxCnt = -1;
        int maxRow = 0, maxCol = 0;

        for (int i = 0; i <= enemyShipsSize - 3; i++) {

            List<EnemyShip> rowShips = enemyships.get(i);
            int rowSize = rowShips.size();

            for (int j = 0; j <= rowSize - 3; j++) {

                int currentCnt = 0;

                for (int x = i; x < i + 3; x++) {

                    List<EnemyShip> subRowShips = enemyships.get(x);

                    for (int y = j; y < j + 3; y++) {
                        EnemyShip ship = subRowShips.get(y);

                        if (ship != null && !ship.isDestroyed())
                            currentCnt++;
                    }
                }

                if (currentCnt > maxCnt) {
                    maxCnt = currentCnt;
                    maxRow = i;
                    maxCol = j;
                }
            }
        }

        List<EnemyShip> targetEnemyShips = new ArrayList<>();
        for (int i = maxRow; i < maxRow + 3; i++) {
            List<EnemyShip> subRowShips = enemyships.get(i);
            for (int j = maxCol; j < maxCol + 3; j++) {
                EnemyShip ship = subRowShips.get(j);

                if (ship != null && !ship.isDestroyed())
                    targetEnemyShips.add(ship);
            }
        }

        if (!targetEnemyShips.isEmpty()) {
            for (EnemyShip destroyedShip : targetEnemyShips) {
                addScore += destroyedShip.getPointValue();
                addShipsDestroyed++;
                enemyShipFormation.destroy(destroyedShip);
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

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
                if (destroyedShip != null && !destroyedShip.isDestroyed()) {
                    addScore += destroyedShip.getPointValue();
                    addShipsDestroyed++;
                    enemyShipFormation.destroy(destroyedShip);
                }
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    public void operateBarrier(Set<Barrier> barriers) {

        int screenWidth = 448;
        int middle = screenWidth / 2 - 39;
        int range = 150;
        barriers.clear();

        barriers.add(new Barrier(middle, 400));
        barriers.add(new Barrier(middle - range, 400));
        barriers.add(new Barrier(middle + range, 400));

    }

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
