package engine;

import entity.Ship;

import java.awt.*;
import java.util.Random;

public class ItemManager {
    private ItemType itemType;
    private boolean GoastActive;
    private int shotNum;

    public ItemManager() {
        this.itemType = null;
        this.GoastActive = false;
        this.shotNum = 1;
    }

    public enum ItemType {
        Bomb,
        LineBomb,
        Barrier,
        Goast,
        TimeStop,
        MultiShot
    }

    public ItemType dropItem() {
        if (Math.random() < 0.99) {
            Random rand = new Random();
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
                    this.itemType = ItemType.Goast;
                    break;
                case 4:
                    this.itemType = ItemType.TimeStop;
                    break;
                case 5:
                    this.itemType = ItemType.MultiShot;
                    break;
            }
        } else {
            this.itemType = null;
        }

        return this.itemType;
    }

    public void operateBomb() {}

    public void operateLineBomb() {}

    public void operateBarrier() {}

    public void operateGoast(Ship ship) {
        this.GoastActive = true;
        ship.setColor(Color.DARK_GRAY);
        new Thread(() -> {
            try {
                Thread.sleep(3000);  // 3초 후 고스트 모드 해제
                this.GoastActive = false;
                ship.setColor(Color.GREEN);
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

    public boolean isGoastActive() {
        return GoastActive;
    }
}
