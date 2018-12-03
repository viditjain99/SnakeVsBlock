import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class Coin extends ImageView implements Token
{

    private int tokenId=2;
    private boolean erasable = false;
    TranslateTransition translateTransition;
    
    /**
     * Function to setup a coin
     * @param rectangle2D A rectangle with same dimensions as of the screen
     * @throws FileNotFoundException
     */

    public Coin(Rectangle2D rectangle2D) throws FileNotFoundException
    {
        FileInputStream fileInputStream=new FileInputStream("coin_image.png");
        Image image=new Image(fileInputStream);
        setImage(image);
        setPreserveRatio(true);
        setFitHeight(75);
        setFitWidth(75);
        setY(-400);

        translateTransition=new TranslateTransition();
        translateTransition.setDuration(Duration.millis(4000));
        translateTransition.setNode(this);
        translateTransition.setByY(rectangle2D.getHeight()+400);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setInterpolator(Interpolator.LINEAR);
    }
    /**
     * Function to check if the given coin is erasable or not.
     * @return coin's erasable property.
     */
    public boolean getErasable() {
    	return erasable;
    }
    /**
     * Constructor to setup a coin.
     * @param z The x coordinate of the coin
     * @param rectangle2D A rectangle with same dimensions as of the screen
     * @throws FileNotFoundException
     */
    public Coin(int v,int z,Rectangle2D rectangle2D, int ID) throws FileNotFoundException
    {
        FileInputStream fileInputStream=new FileInputStream("coin_image.png");
        Image image=new Image(fileInputStream);
        setImage(image);
        setPreserveRatio(true);
        setFitHeight(75);
        setFitWidth(75);
        setY(-600);
        setX (z);
        
        erasable = true;

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
     * Constructor to setup a coin.
     * @param z The x coordinate of the coin
     * @param rectangle2D A rectangle with same dimensions as of the screen
     * @throws FileNotFoundException
     */

    public Coin(int v,int z,Rectangle2D rectangle2D) throws FileNotFoundException
    {
        FileInputStream fileInputStream=new FileInputStream("coin_image.png");
        Image image=new Image(fileInputStream);
        setImage(image);
        setPreserveRatio(true);
        setFitHeight(75);
        setFitWidth(75);
        setY(-600);
        setX (z);

        translateTransition=new TranslateTransition();
        double time=(3600+3*rectangle2D.getHeight())/(800+rectangle2D.getHeight());
        translateTransition.setDuration(Duration.millis(time*1000));
        translateTransition.setNode(this);
        translateTransition.setByY(rectangle2D.getHeight()+600);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        
    }
    
    @Override
    /**
     * Function to retrieve the id of the token for identification when collision happens with the snake
     * @return The id of the token
     */
    public int getTokenId()
    {
        return this.tokenId;
    }
}