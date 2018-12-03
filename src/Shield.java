import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Shield extends ImageView implements Token
{
    private int tokenId=6;
    TranslateTransition translateTransition;
    
    /**
     * Constructor to setup a shield.
     * @param rectangle2D A rectangle with same dimensions as of the screen
     * @throws FileNotFoundException
     */
    public Shield(Rectangle2D rectangle2D) throws FileNotFoundException
    {
        FileInputStream fileInputStream=new FileInputStream("shield.png");
        Image image=new Image(fileInputStream);
        setImage(image);
        setPreserveRatio(true);
        setFitHeight(75);
        setFitWidth(75);
        setLayoutY(-400);

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