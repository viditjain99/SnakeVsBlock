import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Snake implements Serializable
{
    private int length;
    private boolean shieldActivated;
    private boolean magentActivated;
    private ArrayList<Circle> snakeBody;
    
    /**
     * Constructor to setup the snake
     */
    

    public Snake()
    {
        snakeBody=new ArrayList<>();
        length=0;
        shieldActivated=false;
        magentActivated=false;
    }
    
    /**
     * Function to change to the snake skin to the one selected by the player.
     * @param a the image input stream of the selected skin.
     */
    
    
    public void setSkin(FileInputStream a) {
    	Image f = new Image(a);
    	
    }
    /**
     * Function to increase the length of the snake in case of collision with a ball.
     * @param num increase in snake's length
     * @param screenDimens A rectangle with same dimensions as of the screen
     * @param root the parent group of the game scene.
     */
    
    
    public void increaseLengthBy(int num, Rectangle2D screenDimens, Group root)
    {
        for(int j=0;j<num;j++)
        {
        	
            Circle circle=new Circle(Game.snakeX,screenDimens.getHeight()/2+150+Game.offset,15, Color.YELLOW);
            Game.offset=Game.offset+30;
            getSnakeBody().add(circle);
            root.getChildren().add(circle);
            
            
        }
        if(isShieldActivated())
        {
            for(int j=1;j<snakeBody.size();j++)
            {
                snakeBody.get(j).setFill(Color.DARKGOLDENROD);
            }
        }

        if(isMagnetActivated())
        {
            for(int j=1;j<snakeBody.size();j++)
            {
                if(j<=snakeBody.size()/2)
                {
                    snakeBody.get(j).setFill(Color.RED);
                }
                else
                {
                    snakeBody.get(j).setFill(Color.WHITE);
                }
            }
        }
    }
    
    /**
     * Function to set the snake at the start of the game.
     * @param num increase in snake's length
     * @param screenDimens A rectangle with same dimensions as of the screen
     * @param root the parent group of the game scene.
     * @param a The skin image input stream.
     */
    
    public void increaseLengthBy(int num, Rectangle2D screenDimens, Group root,FileInputStream a)
    {
    	Image f = new Image(a);
    	
        for(int j=0;j<num;j++)
        {
        	
        	if(j==0) {
        		Circle circle=new Circle(Game.snakeX,screenDimens.getHeight()/2+150+Game.offset,15, Color.YELLOW);
        		circle.setFill(new ImagePattern(f));
                Game.offset=Game.offset+30;
                getSnakeBody().add(circle);
                root.getChildren().add(circle);
        	}
        	else {
        		Circle circle=new Circle(Game.snakeX,screenDimens.getHeight()/2+150+Game.offset,15, Color.YELLOW);
                Game.offset=Game.offset+30;
                getSnakeBody().add(circle);
                root.getChildren().add(circle);
                
        	}
        	
            
            
        }
        if(isShieldActivated())
        {
            for(int j=1;j<snakeBody.size();j++)
            {
                snakeBody.get(j).setFill(Color.DARKGOLDENROD);
            }
        }

        if(isMagnetActivated())
        {
            for(int j=1;j<snakeBody.size();j++)
            {
                if(j<=snakeBody.size()/2)
                {
                    snakeBody.get(j).setFill(Color.RED);
                }
                else
                {
                    snakeBody.get(j).setFill(Color.WHITE);
                }
            }
        }
    }
    
    /**
     * Function to decrease the length of the snake in case of collision with a block.
     * @param num decrease in snake's length
     * @param screenDimens A rectangle with same dimensions as of the screen
     * @param root the parent group of the game scene.
     * @param block instance of the block the snake collided with
     */
    public void decreaseLengthBy(int num,Group root,Rectangle2D screenDimens,Block block)
    {
        if(num>5)
        {
            Timer timer=new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int count=0;
                @Override
                public void run()
                {
                    javafx.application.Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                        	if(snakeBody.size()!=0) {
                        		root.getChildren().remove(snakeBody.get(snakeBody.size()-1));
                                snakeBody.remove(snakeBody.size()-1);
                                Game.offset=Game.offset-30;
                                Game.snakeLengthTextView.setText(snakeBody.size()-1+"");
                                block.weightText.setText(Integer.parseInt(block.weightText.getText())-1+"");
                                count++;
                                if(count==num)
                                {
                                    timer.cancel();
                                }
                        	}
                            
                        }
                    });
                }
            },0,100);
        }
        else
        {
            for(int j=0;j<num;j++)
            {
                root.getChildren().remove(snakeBody.get(snakeBody.size()-1));
                snakeBody.remove(snakeBody.size()-1);
                Game.offset=Game.offset-30;
            }
        }
    }
    /**
     * Function to decrease the length of the snake in case of collision with a block and consequent end of the game.
     * @param num increase in snake's length
     * @param root the parent group of the game scene.
     * @param block instance of the block the snake collided with.
     */
    
    
    public void decreaseLengthForEndGame(int num,Group root,Block block)
    {
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int count=0;
            @Override
            public void run()
            {
                javafx.application.Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {	
                    	if(snakeBody.size()!=0) {
                    		root.getChildren().remove(snakeBody.get(snakeBody.size()-1));
                            snakeBody.remove(snakeBody.size()-1);
                            Game.offset=Game.offset-30;
                            Game.snakeLengthTextView.setText(snakeBody.size()+"");
                            block.weightText.setText(Integer.parseInt(block.weightText.getText())-1+"");
                            count++;
                            if(count==num)
                            {
                                timer.cancel();
                            }
                    	}
                    	
                    }
                });
            }
        },0,100);
    }
    /**
     * Function to activate/deactivate snake's shield.
     * @param state if the snake shield is to be activated or deactivated.
     
     */
    
    public void setShieldState(boolean state)
    {
        this.shieldActivated=state;
    }
    
    /**
     * Function to check if the snake has acquired the shield powerup to dodge blocks.
     * @return if the snake has acquired the shield powerup or not.
     */

    public boolean isShieldActivated()
    {
        return this.shieldActivated;
    }

    /**
     * Function to activate/deactivate snake's magnetic ability.
     * @param state if the snake's magnetic ability is to be activated or deactivated.
     */
    
    
    
    public void setMagnetActivated(boolean state)
    {
        this.magentActivated=state;
    }
    
    /**
     * Function to check if the snake has acquired the magnet powerup to attract coins.
     * @return if the snake has acquired the magnet powerup or not.
     */
    public boolean isMagnetActivated()
    {
        return this.magentActivated;
    }
    /**
     * Function to retrieve the snakeBody array for identification when collision happens with any token.
     * @return The snakeBody array.
     */
    public ArrayList<Circle> getSnakeBody()
    {
        return this.snakeBody;
    }
    
    /**
     * Function to set the snakeBody array to a predefined array of Circles.
     * @param snakeBody the predefined snakeBody array.
     */

    public void setSnakeBody(ArrayList<Circle> snakeBody)
    {
        this.snakeBody=snakeBody;
    }
}