import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

public class Ball extends Group implements Token
{
    Circle ball;
    Text weightText;
    int tokenId=0;
    TranslateTransition translateTransition;

    /**
     * Constructor to setup a ball
     * @param rectangle2D rectangle with dimensions equal to the screen
     */
    public Ball(Rectangle2D rectangle2D)
    {
        ball=new Circle();
        ball.setRadius(25);
        ball.setFill(Color.YELLOW);
        Random random=new Random();
        int weight=1+random.nextInt(5);
        weightText=new Text(ball.getCenterY()-5,ball.getCenterY()-60,weight+"");
        weightText.setFill(Color.WHITE);
        weightText.setFont(Font.font("verdana",20));
        getChildren().addAll(ball,weightText);
        setLayoutY(-ball.getRadius()-ball.getRadius()-10);
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

    /**
     * Function to get the reference of the circle object inside Ball
     * @return The circle object inside Ball
     */
    public Circle getBall()
    {
        return this.ball;
    }

    /**
     * Function to get the weight of the Ball
     * @return The weight of the ball
     */
    public int getWeight()
    {
        return Integer.parseInt(this.weightText.getText());
    }
}