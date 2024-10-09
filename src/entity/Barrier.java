package entity;
import java.awt.Color;

import engine.DrawManager.SpriteType;

public class Barrier extends Entity{

    private int health;
    public Barrier(final int positionX, final int positionY) {
        super(positionX, positionY, 39 * 2, 11 * 2, Color.GREEN);
        this.spriteType = SpriteType.Barrier;
        this.health = 1;
    }

    public void reduceHealth() {
        this.health--;
    }

    public boolean isDestroyed() {
        return this.health <= 0;
    }

    public int getHealth() {
        return this.health;
    }


}
