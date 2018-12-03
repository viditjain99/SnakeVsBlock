import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.Serializable;

public class Block extends StackPane implements Token
{
    private Color color;
    Text weightText;
    private Rectangle rectangle;
    int weight;
    private int x;
    private int tokenId=1;
    TranslateTransition translateTransition;

    /**
     * Constructor to setup a block
     * @param color The color of the block
     * @param weight The weight of the block
     * @param x The x coordinate of the block
     * @param rectangle2D A rectangle with same dimensions as of the screen
     */
    
    public Block(Color color,int weight,int x,Rectangle2D rectangle2D)
    {
        Screen screen=Screen.getPrimary();
        Rectangle2D screenDimen=screen.getBounds();
        setWidth(screenDimen.getWidth()/8);
        setHeight(getWidth());

        this.color=color;
        rectangle=new Rectangle();
        rectangle.setFill(color);
        rectangle.setWidth(screenDimen.getWidth()/8);
        rectangle.setHeight(rectangle.getWidth());
        rectangle.setStrokeWidth(5);
        rectangle.setArcHeight(30);
        rectangle.setArcWidth(30);
        rectangle.setStroke(Color.BLACK);

        this.weight=weight;
        weightText=new Text();
        weightText.setText(weight+"");
        weightText.setFill(Color.BLACK);
        weightText.setFont(Font.font("verdana", FontWeight.BOLD,50));

        getChildren().addAll(rectangle,weightText);
        this.x=x;
        setLayoutX(x);
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
     * Constructor to setup a block
     * @param color The color of the block
     * @param weight The weight of the block
     * @param x The x coordinate of the block
     * @param rectangle2D A rectangle with same dimensions as of the screen
     */
    public Block(Color color,int weight,int x,int y,Rectangle2D rectangle2D)
    {
        Screen screen=Screen.getPrimary();
        Rectangle2D screenDimen=screen.getBounds();
        setWidth(screenDimen.getWidth()/8);
        setHeight(getWidth());

        this.color=color;
        rectangle=new Rectangle();
        rectangle.setFill(color);
        rectangle.setWidth(screenDimen.getWidth()/8);
        rectangle.setHeight(rectangle.getWidth());
        rectangle.setStrokeWidth(5);
        rectangle.setArcHeight(30);
        rectangle.setArcWidth(30);
        rectangle.setStroke(Color.BLACK);

        this.weight=weight;
        weightText=new Text();
        weightText.setText(weight+"");
        weightText.setFill(Color.BLACK);
        weightText.setFont(Font.font("verdana", FontWeight.BOLD,50));

        getChildren().addAll(rectangle,weightText);
        this.x=x;
        setLayoutX(x);
        setLayoutY(-600);

        translateTransition=new TranslateTransition();
        double time=(3600+3*screenDimen.getHeight())/(800+screenDimen.getHeight());
        translateTransition.setDuration(Duration.millis(time*1000));
        translateTransition.setNode(this);
        translateTransition.setByY(rectangle2D.getHeight()+600);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        translateTransition.setInterpolator(Interpolator.LINEAR);
    }

    /**
     * Function to get the reference to the rectangle object inside Block
     * @return The rectangle object inside the Block
     */
    public Rectangle getRectangle()
    {
        return this.rectangle;
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