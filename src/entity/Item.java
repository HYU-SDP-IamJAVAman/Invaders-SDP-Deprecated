package entity;

import java.util.Random;

public class Item {

    public boolean isGhostAction;
    public boolean isMultiShotActivated;
    public boolean isLineBombActivated;
    public boolean isBarrierActivated;

    public Item() {
        this.isGhostAction = false;
        this.isMultiShotActivated = false;
        this.isLineBombActivated = false;
        this.isBarrierActivated = false;
    }


    public void itemActivate() {
            Random random = new Random();
            int randomMethodIndex = random.nextInt(6);
            System.out.println(randomMethodIndex);

            switch (randomMethodIndex) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    isBarrierActivated = true;
                    break;
                case 3:
                    isLineBombActivated = true;
                    break;
                case 4:
                    isGhostAction = true;
                    break;
                case 5:
                    isMultiShotActivated = true;
                    break;
            }
    }

    public void setIsGhostActive() {
        isGhostAction = false;
    }

    public void setBarrierDeactivated() {
        isBarrierActivated = false;
    }

}