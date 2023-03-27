import java.awt.*;
import java.awt.image.BufferedImage;

public class BlankCursor {
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");

    public Cursor getBlankCursor(){
        return blankCursor;
    }


}
