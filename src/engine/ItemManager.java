package engine;

import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;
import entity.Barrier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages item drop and use.
 *
 * @author Seochan Moon
 * @author noturavrigk
 * @author specture258
 * @author javadocq
 * @author bamcasa
 * @author D0hunLee
 *
 */
public class ItemManager {
    /** Width of game screen. */
    private static final int WIDTH = 600;
    /** Height of game screen. */
    private static final int HEIGHT = 650;
    /** Item drop probability, (1 ~ 100). */
    private static final int ITEM_DROP_PROBABILITY = 30;

    /** Random generator. */
    private final Random rand;
    /** Player's ship. */
    private final Ship ship;
    /** Formation of enemy ships. */
    private final EnemyShipFormation enemyShipFormation;
    /** Set of Barriers in game screen. */
    private final Set<Barrier> barriers;
    /** Application logger. */
    private final Logger logger;

    /** Check if Time-stop is active. */
    private boolean timeStopActive;
    /** Check if Ghost is active */
    private boolean ghostActive;
    /** Check if the number of shot is max, (maximum 3). */
    private boolean isMaxShotNum;
    /** Number of bullets that player's ship shoot. */
    private int shotNum;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /** Types of item */
    public enum ItemType {
        Bomb,
        LineBomb,
        Barrier,
        Ghost,
        TimeStop,
        MultiShot
    }

    /**
     * Constructor, sets the initial conditions.
     *
     * @param ship Player's ship.
     * @param enemyShipFormation Formation of enemy ships.
     * @param barriers Set of barriers in game screen.
     *
     */
    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation, Set<Barrier> barriers) {
        this.shotNum = 1;
        this.rand = new Random();
        this.ship = ship;
        this.enemyShipFormation = enemyShipFormation;
        this.barriers = barriers;
        this.logger = Core.getLogger();
    }

    /**
     * Drop the item.
     *
     * @return Checks if the item was dropped.
     */
    public boolean dropItem() {
        return (rand.nextInt(101)) <= ITEM_DROP_PROBABILITY;
    }

    /**
     * Select item randomly.
     *
     * @return Item type.
     */
    private ItemType selectItemType() {
        ItemType[] itemTypes = ItemType.values();

        if (isMaxShotNum)
            return itemTypes[rand.nextInt(5)];

        return itemTypes[rand.nextInt(6)];
    }

    /**
     * Uses a randomly selected item.
     *
     * @return If the item is offensive, returns the score to add and the number of ships destroyed.
     *         If the item is non-offensive, returns null.
     */
    public Entry<Integer, Integer> useItem() {
        ItemType itemType = selectItemType();
        logger.info(itemType + " used");

        return switch (itemType) {
            case Bomb -> operateBomb();
            case LineBomb -> operateLineBomb();
            case Barrier -> operateBarrier();
            case Ghost -> operateGhost();
            case TimeStop -> operateTimeStop();
            case MultiShot -> operateMultiShot();
        };
    }

    /**
     * Operate Bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateBomb() {
        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShips();
        int enemyShipsSize = enemyships.size();

        int maxCnt = -1;
        int maxRow = 0, maxCol = 0;

        soundManager.playSound(Sound.ITEM_BOMB);

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

    /**
     * Operate Line-bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateLineBomb() {
        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShips();

        int targetRow = -1;
        int maxCnt = -1;

        soundManager.playSound(Sound.ITEM_BOMB);

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

    /**
     * Operate Barrier item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateBarrier() {

        int middle = WIDTH / 2 - 39;
        int range = 200;
        this.barriers.clear();

        this.barriers.add(new Barrier(middle, HEIGHT - 100));
        this.barriers.add(new Barrier(middle - range, HEIGHT - 100));
        this.barriers.add(new Barrier(middle + range, HEIGHT - 100));

        soundManager.playSound(Sound.ITEM_BARRIER_ON);

        return null;
    }

    /**
     * Operate Ghost item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateGhost() {
        this.ghostActive = true;
        this.ship.setColor(Color.DARK_GRAY);
        soundManager.playSound(Sound.ITEM_GHOST);
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                this.ghostActive = false;
                this.ship.setColor(Color.GREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return null;
    }

    /**
     * Operate Time-stop item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateTimeStop() {
        this.timeStopActive = true;
        soundManager.playSound(Sound.ITEM_TIMESTOP_ON);
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                this.timeStopActive = false;
                soundManager.playSound(Sound.ITEM_TIMESTOP_OFF);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return null;
    }

    /**
     * Operate Multi-shot item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateMultiShot() {
        if (this.shotNum < 3) {
            this.shotNum++;
            if (this.shotNum == 3) {
                this.isMaxShotNum = true;
            }
        }

        return null;
    }

    /**
     * Checks if Ghost is active.
     *
     * @return True when Ghost is active.
     */
    public boolean isGhostActive() {
        return this.ghostActive;
    }

    /**
     * Checks if Time-stop is active.
     *
     * @return True when Time-stop is active.
     */
    public boolean isTimeStopActive() {
        return this.timeStopActive;
    }

    /**
     * Returns the number of bullets that player's ship shoot.
     * @return Number of bullets that player's ship shoot.
     */
    public int getShotNum() {
        return this.shotNum;
    }
}
