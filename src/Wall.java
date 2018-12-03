import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class Wall extends Rectangle implements Token
{
    double x;
    private int tokenId=7;
    TranslateTransition translateTransition;
    
    /**
     * Constructor to setup a wall.
     * @param x x-coordinate of the wall.
     * @param rectangle2D A rectangle with same dimensions as of the screen
     */
    public Wall(double x, Rectangle2D rectangle2D)
    {
        this.x=x;
        setWidth(5);
        setFill(Color.WHITE);
        setX(x);
        setY(-400);

        translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.millis(3000));
        translateTransition.setNode(this);
        translateTransition.setByY(rectangle2D.getHeight()+400);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setInterpolator(Interpolator.LINEAR);
    }
    /**
     * Function to retrieve the id of the token for identification when collision happens with the snake
     * @return The id of the token
     */
    @Override
    public int getTokenId()
    {
        return this.tokenId;
    }
}