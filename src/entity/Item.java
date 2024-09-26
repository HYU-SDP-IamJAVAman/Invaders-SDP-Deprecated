package entity;

import java.util.Random;

public class Item {
    public boolean isMultiShotActivated = false;

    public void itemActivate() {
        if (Math.random() < 0.99) {
            Random random = new Random();
            int randomMethodIndex = random.nextInt(6);

            switch (randomMethodIndex) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    isMultiShotActivated = true;
                    break;
            }

        }
    }

}
