package entity;

import engine.DrawManager.SpriteType;

import java.awt.*;

/**
 * Implement an item box that activates an item when destroyed.
 *
 * @author Seochan Moon
 *
 */

public class ItemBox extends Entity {

    /** Prevents being hit by bullets immediately after being created. */
    public boolean appearRightNow = true;

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     */
    public ItemBox(int positionX, int positionY) {
        super(positionX, positionY, 7 * 2, 7 * 2, Color.YELLOW);

        this.spriteType = SpriteType.ItemBox;
    }

    public boolean appearRightNow = true;
}
