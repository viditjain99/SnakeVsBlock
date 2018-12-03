import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DestroyBlock extends ImageView implements Token
{
    private int tokenId=4;
    TranslateTransition translateTransition;
    
    /**
     * Constructor to setup a destroy block.
     * @param rectangle2D A rectangle with same dimensions as of the screen
     * @throws FileNotFoundException
     */
    public DestroyBlock(Rectangle2D rectangle2D) throws FileNotFoundException
    {
        FileInputStream fileInputStream=new FileInputStream("destroy.png");
        Image image=new Image(fileInputStream);
        setImage(image);
        setPreserveRatio(true);
        setFitHeight(100);
        setFitWidth(100);
        setY(-400);

        translateTransition=new TranslateTransition();
        double time=(3600+3*rectangle2D.getHeight())/(800+rectangle2D.getHeight());
        translateTransition.setDuration(Duration.millis(time*1000));
        translateTransition.setNode(this);
        translateTransition.setByY(rectangle2D.getHeight()+600);
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