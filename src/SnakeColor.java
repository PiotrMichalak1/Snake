import java.awt.*;
import java.util.Random;

import static java.lang.Math.min;


public class SnakeColor {
    int[] initialRGB;

    SnakeColor(Random random) {
        initialRGB = new int[]{random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)};
    }

    public Color createColorForSnakeBody(int bodyPart) {
        int R = (initialRGB[0]+7*bodyPart)%512;
        int G = (initialRGB[1]+256-4*bodyPart)%512;
        int B = (initialRGB[2]+9*bodyPart)%512;

        int red = min(R,511-R);
        int green = min(G,511-G);
        int blue = min(B,511-B);
        return new Color(red, green, blue);
    }

    public void setNewRandomStartingColors(Random random) {
        initialRGB = new int[]{random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)};
    }
}
