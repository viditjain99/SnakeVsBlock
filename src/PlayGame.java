import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static javafx.scene.paint.Color.*;
import static javafx.scene.paint.Color.CYAN;

public class PlayGame
{
    private Player player;
    private Button backButton;
    private Text score;
    Text statusText,playerName;
    private Button startAgainButton;
    Rectangle2D screenDimens;
    Group root;
    MenuItem pauseMenuItem=new MenuItem("Pause");
    MenuItem restartMenuItem=new MenuItem("Restart");
    MenuItem mainMenuMenuItem=new MenuItem("Main Menu");
    static Snake snake;
    AudioClip blockCollision,ballCollision,destroyBlockCollision,coinCollision,gameOver;
    Rectangle rectangle;
    MenuButton menuButton;
    ArrayList<Coin> coinArr;
    static int coinPoint=1;
    static boolean wallCollided=false;
    
    /**
     * Constructor to setup the game scene.
     * @param root The parent group of the scene
     * @param snake the instaance of the player's snake.
     * @param primaryStage The main stage of the game.
     * @throws FileNotFoundException
     */
    public PlayGame(Group root,Snake snake,Stage primaryStage) throws FileNotFoundException
    {
        Screen screen=Screen.getPrimary();
        screenDimens=screen.getBounds();
        double screenWidth=screenDimens.getWidth();
        double screenHeight=screenDimens.getHeight();
        this.root=root;
        this.snake=snake;
        blockCollision=new AudioClip(new File("block_collision.wav").toURI().toString());
        ballCollision=new AudioClip(new File("ball_eating.wav").toURI().toString());
        destroyBlockCollision=new AudioClip(new File("destroy_block.wav").toURI().toString());
        coinCollision=new AudioClip(new File("coin.mp3").toURI().toString());
        gameOver=new AudioClip(new File("game_over.wav").toURI().toString());
        coinArr=new ArrayList<>();
        rectangle=new Rectangle(0,0,screenWidth,screenHeight);
        rectangle.setFill(Color.TRANSPARENT);
        //root.getChildren().add(rectangle);



        FileInputStream back = new FileInputStream("back.png");
        Image backim = new Image(back);
        ImageView imageViewback = new ImageView(backim);
        imageViewback.setFitWidth(180);
    	imageViewback.setFitHeight(50);

    	FileInputStream reset = new FileInputStream("reset.png");
        Image resetim = new Image(reset);
        ImageView imageViewreset = new ImageView(resetim);
        imageViewreset.setFitWidth(180);
    	imageViewreset.setFitHeight(70);



    	 backButton = new Button("",imageViewback);
    	 backButton.setVisible(true);
    	 backButton.setTranslateX((screenWidth/2) - 50);
    	 backButton.setTranslateY(10);
    	 backButton.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
    	 backButton.setFocusTraversable(false);

        score=new Text(20,20,"0");
        score.setFill(Color.WHITE);
        score.setFont(Font.font("verdana",20));

        statusText=new Text(screenWidth/2-100,screenHeight/2,"");
        statusText.setFill(Color.WHITE);
        statusText.setFont(Font.font("verdana",50));

        startAgainButton = new Button("",imageViewreset);
        startAgainButton.setVisible(true);
        startAgainButton.setTranslateX((screenWidth) - 225);
        startAgainButton.setTranslateY(-10);
        startAgainButton.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
        startAgainButton.setFocusTraversable(false);

        menuButton=new MenuButton("Options");
        menuButton.getItems().addAll(pauseMenuItem,restartMenuItem,mainMenuMenuItem);
        menuButton.setLayoutX(70);
        menuButton.setWrapText(true);
        //root.getChildren().add(menuButton);
        menuButton.setFocusTraversable(false);

        playerName=new Text(180,20,"");
        playerName.setFill(Color.WHITE);
        playerName.setFont(Font.font("verdana",20));

    }
    
    /**
     * Function to set the current player.
     * @param player instance of the current player.
     */
    
    public void setPlayer(Player player)
    {
        this.player=player;
    }
    
    /**
     * Function to get the current player instance
     * @return the current player instance
     */

    public Player getPlayer()
    {
        return this.player;
    }
    
    /**
     * Function to get the TEXT VIEW of the current player's score.
     * @return the Text View of the current player's score.
     */
    public Text getScore()
    {
        return this.score;
    }
    
    /**
     * Function to generate individual blocks in the game
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     */

    public void singleBlockGenerator(Group root, Rectangle2D rectangle2D, Stack<Token> tokens)
    {
        Random random=new Random();
        int screenWidth=(int) rectangle2D.getWidth();
        int numOfBlocks=2+random.nextInt(2);
        if(numOfBlocks==2)
        {
            int partition=screenWidth/2;
            for(int i=0;i<2;i++)
            {
                int weight=1+random.nextInt(snake.getSnakeBody().size()+10);
                Color color=Color.CYAN;
                if(weight>=1 && weight<=5)
                {
                    color=CYAN;
                }
                else if(weight>=5 && weight<=10)
                {
                    color=new Color(0.127,1,0,1);
                }
                else if(weight>=11 && weight<=20)
                {
                    color=new Color(0.034,0.5,0.034,1);
                }
                else if(weight>=21 && weight<=30)
                {
                    color=YELLOW;
                }
                else if(weight>=31 && weight<=40)
                {
                    color=ORANGE;
                }
                else if(weight>40)
                {
                    color=RED;
                }
                int max=0;
                int min=0;
                if(i==0)
                {
                    max=partition-180;
                    min=100;
                }
                if(i==1)
                {
                    max=screenWidth-180;
                    min=partition+100;
                }
                int x=random.nextInt((max-min)+1)+min;
                int numOfWalls=random.nextInt(3);
                Block block=new Block(color,weight,x,rectangle2D);
                tokens.push(block);
                root.getChildren().add(block);
                block.translateTransition.play();
                if(numOfWalls==1)
                {
                    Wall wall=new Wall(block.getLayoutX(),rectangle2D);
                    wall.setHeight(200);
                    wall.setY(block.getLayoutY()+block.getHeight()/2+wall.getHeight()/2);
                    root.getChildren().add(wall);
                    tokens.add(wall);

                    wall.translateTransition.play();
                }
                else if(numOfWalls==2)
                {
                    Wall wall1=new Wall(block.getLayoutX(),rectangle2D);
                    wall1.setHeight(200);
                    wall1.setY(block.getLayoutY()+block.getHeight()/2+wall1.getHeight()/2);

                    Wall wall2=new Wall(block.getLayoutX()+block.getWidth(),rectangle2D);
                    wall2.setHeight(200);
                    wall2.setY(block.getLayoutY()+block.getHeight()/2+wall2.getHeight()/2);
                    root.getChildren().addAll(wall1,wall2);
                    tokens.add(wall1);
                    tokens.add(wall2);

                    wall1.translateTransition.play();
                    wall2.translateTransition.play();
                }
            }
        }

        else if(numOfBlocks==3)
        {
            int partition1=screenWidth/3;
            int partition2=2*partition1;
            for(int i=0;i<3;i++)
            {
                Color color=Color.CYAN;
                int weight=1+random.nextInt(snake.getSnakeBody().size()+10);
                if(weight>=1 && weight<=5)
                {
                    color=CYAN;
                }
                else if(weight>=5 && weight<=10)
                {
                    color=new Color(0.127,1,0,1);
                }
                else if(weight>=11 && weight<=20)
                {
                    color=new Color(0.034,0.5,0.034,1);
                }
                else if(weight>=21 && weight<=30)
                {
                    color=YELLOW;
                }
                else if(weight>=31 && weight<=40)
                {
                    color=ORANGE;
                }
                else if(weight>40)
                {
                    color=RED;
                }
                int max=0;
                int min=0;
                if(i==0)
                {
                    max=partition1-180;
                    min=130;
                }
                if(i==1)
                {
                    max=partition2;
                    min=partition1+100;
                }
                if(i==2)
                {
                    max=screenWidth-200;
                    min=partition2+200;
                }
                int x=random.nextInt((max-min)+1)+min;
                int numOfWalls=random.nextInt(3);
                Block block=new Block(color,weight,x,rectangle2D);
                tokens.push(block);
                root.getChildren().add(block);
                block.translateTransition.play();
                if(numOfWalls==1)
                {
                    Wall wall=new Wall(block.getLayoutX(),rectangle2D);
                    wall.setHeight(200);
                    wall.setY(block.getLayoutY()+block.getHeight()/2+wall.getHeight()/2);
                    root.getChildren().add(wall);
                    tokens.add(wall);

                    wall.translateTransition.play();
                }
                else if(numOfWalls==2)
                {
                    Wall wall1=new Wall(block.getLayoutX(),rectangle2D);
                    wall1.setHeight(200);
                    wall1.setY(block.getLayoutY()+block.getHeight()/2+wall1.getHeight()/2);

                    Wall wall2=new Wall(block.getLayoutX()+block.getWidth(),rectangle2D);
                    wall2.setHeight(200);
                    wall2.setY(block.getLayoutY()+block.getHeight()/2+wall2.getHeight()/2);
                    root.getChildren().addAll(wall1,wall2);
                    tokens.add(wall1);
                    tokens.add(wall2);

                    wall1.translateTransition.play();
                    wall2.translateTransition.play();
                }
            }
        }
    }
    
    /**
     * Function to generate a group of blocks.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     * @throws FileNotFoundException
     */
    public void groupBlockGenerator(Group root, Rectangle2D rectangle2D, Stack<Token> tokens) throws FileNotFoundException {
        Random random=new Random();
        int screenWidth=(int) rectangle2D.getWidth();
        double centreX=0;
        int numOfWalls=random.nextInt(5);
        int wallCount=0;
        int weightLessIndex=random.nextInt(8);
        for(int i=0;i<8;i++)
        {
            int weight=1+random.nextInt(snake.getSnakeBody().size()+40);
            if(i==weightLessIndex)
            {
                if(snake.getSnakeBody().size()==1)
                {
                    weight=0;
                }
                else if(snake.getSnakeBody().size()>1)
                {
                    weight=1+random.nextInt(snake.getSnakeBody().size()-1);
                }
            }
            int x=25+random.nextInt(screenWidth);
            Color color=Color.CYAN;
            if(weight>=1 && weight<=5)
            {
                color=CYAN;
            }
            else if(weight>=5 && weight<=10)
            {
                color=new Color(0.127,1,0,1);
            }
            else if(weight>=11 && weight<=20)
            {
                color=new Color(0.034,0.5,0.034,1);
            }
            else if(weight>=21 && weight<=30)
            {
                color=YELLOW;
            }
            else if(weight>=31 && weight<=40)
            {
                color=ORANGE;
            }
            else if(weight>40)
            {
                color=RED;
            }
            if(weight!=0)
            {
                Block block=new Block(color,weight,x,rectangle2D);
                block.setLayoutX(centreX);
                centreX+=block.getWidth();
                tokens.push(block);
                root.getChildren().add(block);

                int decision=random.nextInt(3);
                if(wallCount<numOfWalls && i!=0 && decision<=1)
                {
                    Wall wall=new Wall(centreX-block.getWidth(),rectangle2D);
                    wall.setHeight(200);
                    wall.setY(block.getLayoutY()+block.getHeight()/2+wall.getHeight()/2);
                    root.getChildren().add(wall);
                    tokens.add(wall);
                    wall.translateTransition.play();
                    wallCount++;
                }

                block.translateTransition.play();
            }
            else
            {
                centreX+=screenDimens.getWidth()/8;
            }
        }
    }
    
    /**
     * Function to generate coins.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     */

    public void coinGenerator(Group root,Rectangle2D rectangle2D,Stack<Token> tokens)
    {
        int screenWidth=(int) rectangle2D.getWidth();
        Coin coin=null;
        try
        {
            coin=new Coin(rectangle2D);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Random r = new Random();
        int low = (screenWidth/2)-650;
        int high = (screenWidth/2) + 651;
        int x = r.nextInt(high-low) + low;

        coin.setX(x);
        root.getChildren().add(coin);
        tokens.push(coin);
        coin.translateTransition.play();
    }
    /**
     * Function to generate magnets along with test-coins.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     * @throws FileNotFoundException
     */
    public void magnetGenerator(Group root,Rectangle2D rectangle2D,Stack<Token> tokens) throws FileNotFoundException
    {
        int screenWidth=(int) rectangle2D.getWidth();
        Magnet magnet=null;
        try
        {
            magnet=new Magnet(rectangle2D);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Random r = new Random();
        int low = (screenWidth/2)-150;
        int high = (screenWidth/2) + 151;
        int x = r.nextInt(high-low) + low;

        magnet.setX(x);

        Coin coin1 = new Coin(1,x+500,rectangle2D);
        Coin coin2 = new Coin(2,x+250,rectangle2D,1);
        Coin coin3 = new Coin(1,x-250,rectangle2D,1);
        Coin coin4 = new Coin(2,x-600,rectangle2D);

//        coinArr.add(coin1);
//        coinArr.add(coin2);
//        coinArr.add(coin3);
//        coinArr.add(coin4);

        magnet.translateTransition.play();
        coin1.translateTransition.play();
        coin2.translateTransition.play();
        coin3.translateTransition.play();
        coin4.translateTransition.play();
        
        root.getChildren().addAll(magnet,coin1,coin2,coin3,coin4);

        tokens.push(magnet); 
        tokens.push(coin1);
        tokens.push(coin2);
        tokens.push(coin3);
        tokens.push(coin4);
    }
    
    
    /**
     * Function to generate shields.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     */
    public void shieldGenerator(Group root,Rectangle2D rectangle2D,Stack<Token> tokens)
    {
        int screenWidth=(int) rectangle2D.getWidth();
        Shield shield=null;
        try
        {
            shield=new Shield(rectangle2D);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Random r = new Random();
        int choice=r.nextInt(2);
        if(choice==0)
        {
            int low=100;
            int high=screenWidth/2-100;
            int x=r.nextInt(high-low)+low;
            shield.setLayoutX(x);

            x=r.nextInt(screenWidth-200-screenWidth/2-100)+screenWidth/2+100;
            int weight=1+r.nextInt(snake.getSnakeBody().size()+10);
            Color color=Color.CYAN;
            if(weight>=1 && weight<=5)
            {
                color=CYAN;
            }
            else if(weight>=5 && weight<=10)
            {
                color=new Color(0.127,1,0,1);
            }
            else if(weight>=11 && weight<=20)
            {
                color=new Color(0.034,0.5,0.034,1);
            }
            else if(weight>=21 && weight<=30)
            {
                color=YELLOW;
            }
            else if(weight>=31 && weight<=40)
            {
                color=ORANGE;
            }
            else if(weight>40)
            {
                color=RED;
            }
            int numOfWalls=1+r.nextInt(3);
            Block block=new Block(color,weight,x,rectangle2D);
            if(numOfWalls==1)
            {
                Wall wall=new Wall(block.getLayoutX(),rectangle2D);
                wall.setHeight(200);
                wall.setY(block.getLayoutY()+block.getHeight()/2+wall.getHeight()/2);
                root.getChildren().add(wall);
                tokens.add(wall);

                wall.translateTransition.play();
            }
            if(numOfWalls==2)
            {
                Wall wall1=new Wall(block.getLayoutX(),rectangle2D);
                wall1.setHeight(200);
                wall1.setY(block.getLayoutY()+block.getHeight()/2+wall1.getHeight()/2);

                Wall wall2=new Wall(block.getLayoutX()+block.getWidth(),rectangle2D);
                wall2.setHeight(200);
                wall2.setY(block.getLayoutY()+block.getHeight()/2+wall2.getHeight()/2);
                root.getChildren().addAll(wall1,wall2);
                tokens.add(wall1);
                tokens.add(wall2);

                wall1.translateTransition.play();
                wall2.translateTransition.play();
            }
            Ball ball=new Ball(rectangle2D);
            x=r.nextInt(screenWidth/2+100-screenWidth/2)+screenWidth/2;
            ball.setLayoutX(x);

            Ball ball1=new Ball(rectangle2D);
            ball1.setLayoutX(ball.getLayoutX()+500);
            root.getChildren().addAll(shield,block,ball,ball1);
            tokens.push(shield);
            tokens.push(block);
            tokens.push(ball);
            tokens.push(ball1);
            ball1.translateTransition.play();
            ball.translateTransition.play();
            block.translateTransition.play();
            shield.translateTransition.play();
        }
        else
        {
            int low=screenWidth/2+100;
            int high=screenWidth-100;
            int x=r.nextInt(high-low)+low;
            shield.setLayoutX(x);

            int weight=1+r.nextInt(snake.getSnakeBody().size()+10);
            x=r.nextInt(screenWidth/2-200-200)+200;
            int colorChoice=r.nextInt(4);
            Color color;
            if(colorChoice==1)
            {
                color=GREEN;
            }
            else if(colorChoice==2)
            {
                color=BLUE;
            }
            else if(colorChoice==3)
            {
                color=ORANGE;
            }
            else
            {
                color=CYAN;
            }
            int numOfWalls=1+r.nextInt(3);
            Block block=new Block(color,weight,x,rectangle2D);
            if(numOfWalls==1)
            {
                Wall wall=new Wall(block.getLayoutX(),rectangle2D);
                wall.setHeight(200);
                wall.setY(block.getLayoutY()+block.getHeight()/2+wall.getHeight()/2);
                root.getChildren().add(wall);
                tokens.add(wall);

                wall.translateTransition.play();
            }
            if(numOfWalls==2)
            {
                Wall wall1=new Wall(block.getLayoutX(),rectangle2D);
                wall1.setHeight(200);
                wall1.setY(block.getLayoutY()+block.getHeight()/2+wall1.getHeight()/2);

                Wall wall2=new Wall(block.getLayoutX()+block.getWidth(),rectangle2D);
                wall2.setHeight(200);
                wall2.setY(block.getLayoutY()+block.getHeight()/2+wall2.getHeight()/2);
                root.getChildren().addAll(wall1,wall2);
                tokens.add(wall1);
                tokens.add(wall2);

                wall1.translateTransition.play();
                wall2.translateTransition.play();
            }
            Ball ball=new Ball(rectangle2D);
            x=r.nextInt(100)+screenWidth/2;
            ball.setLayoutX(x);

            Ball ball1=new Ball(rectangle2D);
            ball1.setLayoutX(ball.getLayoutX()-500);
            root.getChildren().addAll(shield,block,ball,ball1);
            tokens.push(shield);
            tokens.push(block);
            tokens.push(ball);
            tokens.push(ball1);
            ball1.translateTransition.play();
            ball.translateTransition.play();
            block.translateTransition.play();
            shield.translateTransition.play();
        }
    }

    /**
     * Function to generate destroy blocks.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     */
    public void destroyBlockGenerator(Group root,Rectangle2D rectangle2D,Stack<Token> tokens)
    {
        int screenWidth=(int) rectangle2D.getWidth();
        DestroyBlock destroyBlock=null;
        try
        {
            destroyBlock=new DestroyBlock(rectangle2D);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        Random r = new Random();
        int low = (screenWidth/2)-100;
        int high = (screenWidth/2) + 101;
        int x = r.nextInt(high-low) + low;

        destroyBlock.setX(x);

        int weight=1+r.nextInt(snake.getSnakeBody().size()+10);
        Color color=Color.CYAN;
        if(weight>=1 && weight<=5)
        {
            color=CYAN;
        }
        else if(weight>=5 && weight<=10)
        {
            color=new Color(0.127,1,0,1);
        }
        else if(weight>=11 && weight<=20)
        {
            color=new Color(0.034,0.5,0.034,1);
        }
        else if(weight>=21 && weight<=30)
        {
            color=YELLOW;
        }
        else if(weight>=31 && weight<=40)
        {
            color=ORANGE;
        }
        else if(weight>40)
        {
            color=RED;
        }

        Block block=new Block(color,weight,x+200,3,rectangle2D);

        weight=1+r.nextInt(snake.getSnakeBody().size()+10);
        color=Color.CYAN;
        if(weight>=1 && weight<=5)
        {
            color=CYAN;
        }
        else if(weight>=5 && weight<=10)
        {
            color=new Color(0.127,1,0,1);
        }
        else if(weight>=11 && weight<=20)
        {
            color=new Color(0.034,0.5,0.034,1);
        }
        else if(weight>=21 && weight<=30)
        {
            color=YELLOW;
        }
        else if(weight>=31 && weight<=40)
        {
            color=ORANGE;
        }
        else if(weight>40)
        {
            color=RED;
        }
        Block block3=new Block(color,weight,x-600,4,rectangle2D);

        tokens.push(destroyBlock);
        tokens.push(block);
        tokens.push(block3);

        root.getChildren().addAll(destroyBlock,block,block3);

        block.translateTransition.play();
        block3.translateTransition.play();
        destroyBlock.translateTransition.play();
    }
    /**
     * Function to generate balls.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     */
    public void ballGenerator(Group root,Rectangle2D rectangle2D,Stack<Token> tokens)
    {
        int screenWidth=(int) rectangle2D.getWidth();
        int screenHeight = (int) rectangle2D.getHeight();
        Random random=new Random();
       // int prevX=0;
        
        Random r = new Random();
        int low = (screenWidth/2)-150;
        int high = (screenWidth/2) + 151;
        int x = r.nextInt(high-low) + low;
        
        int z=0;

        for(int i=0;i<5;i++)
        {
            Ball ball=new Ball(rectangle2D);
            
            if(i==0) {
            	z = x;
            }
            else if(i==1) {
            	z = x-600;
            }
            else if(i==2) {
                z = x+250;
            }
            else if(i==3) {
                z = x-250;
            }
            else {
            	z = x+524;
            }
            

            
            ball.setLayoutX(z);

            root.getChildren().add(ball);
            ball.translateTransition.play();
            tokens.push(ball);
        }
    }

    /**
     * Function that chooses the token to be spawned next.
     * @param root The parent group of the scene
     * @param rectangle2D A rectange with same dimensions as that of the screen.
     * @param tokens The stack consisting of all the token currently on the screen.
     * @throws FileNotFoundException
     */
    public void obstaclePicker(Group root,Rectangle2D rectangle2D,Stack<Token> tokens) throws FileNotFoundException
    {
    	Random r = new Random();
        int low = 1;
        int high = 101;
        int obstacle =r.nextInt(high-low) + low;
        if(obstacle>=1 && obstacle<=10)
        {
            singleBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=11 && obstacle<=20)
        {
            groupBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=21 && obstacle<=25)
        {
            ballGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=26 && obstacle<=30)
        {
            destroyBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=31 && obstacle<=40)
        {
            singleBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=41 && obstacle<=50)
        {
            groupBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=51 && obstacle<=55)
        {
            ballGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=56 && obstacle <=58)
        {
            magnetGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=59 && obstacle<=60)
        {
            coinGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=61 && obstacle<=65)
        {
            singleBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=66 && obstacle<=70)
        {
            ballGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=71 && obstacle<=73)
        {
            shieldGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=74 && obstacle<=76)
        {
            destroyBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=77 && obstacle<=80)
        {
            ballGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=81 && obstacle<=90)
        {
            groupBlockGenerator(root,rectangle2D,tokens);
        }
        else if(obstacle>=91 && obstacle<=100)
        {
            singleBlockGenerator(root,rectangle2D,tokens);
        }
        
        menuButton.toFront();
        score.toFront();
        playerName.toFront();
        
    }
    /**
     * Function to check collision of the snake with blocks and the do the needful (decrease snake length, end game in case the snake dies)
     * @param tokens The stack consisting of all the token currently on the screen.
     * @param root The parent group of the scene
     * @param primaryStage The main stage of the game.
     * @param scene The scene to move to in case the game ends.
     */
    public void checkCollisionsWithBlock(Stack<Token> tokens, Group root, Stage primaryStage, Scene scene)
    {
        if(snake.getSnakeBody().size()>=1)
        {
            Screen screen = Screen.getPrimary();
            Rectangle2D screenDimen = screen.getBounds();

            ArrayList<Circle> snakeBody = snake.getSnakeBody();
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token.getTokenId() == 1) {
                    Block block = (Block) token;
                    Shape intersect = Shape.intersect(block.getRectangle(), snakeBody.get(0));
                    if (intersect.getBoundsInLocal().getWidth() != -1 && snake.isShieldActivated()) {
                        root.getChildren().remove(block);
                        score.setText(Integer.parseInt(score.getText()) + block.weight + "");
                        blockCollision.play();
                        tokens.remove(block);
                    }
                    else if (intersect.getBoundsInLocal().getWidth() != -1 && snakeBody.size() - 1 >= Integer.parseInt(block.weightText.getText())) {
                        if (block.weight > 5) {
                            Game.generateObstacles.pause();
                            Game.checkCollisionsWithBlock.pause();
                            Game.checkCollisionsWithBalls.pause();
                            Game.checkCollisionsWithPowerups.pause();
                            for (int j = 0; j < tokens.size(); j++) {
                                Token token1 = tokens.get(j);
                                if (token1.getTokenId() == 0) {
                                    Ball ball = (Ball) token1;
                                    ball.translateTransition.pause();
                                } else if (token1.getTokenId() == 1) {
                                    Block block1 = (Block) token1;
                                    block1.translateTransition.pause();
                                } else if (token1.getTokenId() == 2) {
                                    Coin coin = (Coin) token1;
                                    coin.translateTransition.pause();
                                } else if (token1.getTokenId() == 4) {
                                    DestroyBlock destroyBlock = (DestroyBlock) token1;
                                    destroyBlock.translateTransition.pause();
                                } else if (token1.getTokenId() == 5) {
                                    Magnet magnet = (Magnet) token1;
                                    magnet.translateTransition.pause();
                                } else if (token1.getTokenId() == 6) {
                                    Shield shield = (Shield) token1;
                                    shield.translateTransition.pause();
                                } else if (token1.getTokenId() == 7) {
                                    Wall wall = (Wall) token1;
                                    wall.translateTransition.pause();
                                }
                            }
                            snake.decreaseLengthBy(block.weight, root, screenDimens, block);
                            Timer timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            root.getChildren().remove(block);
                                            tokens.remove(block);
                                            blockCollision.play();
                                            score.setText(Integer.parseInt(score.getText()) + block.weight + "");
                                            Game.generateObstacles.play();
                                            Game.checkCollisionsWithBlock.play();
                                            Game.checkCollisionsWithBalls.play();
                                            Game.checkCollisionsWithPowerups.play();
                                            Random r = new Random();

                                            for (int p = 0; p < 20; p++) {

                                                Circle cir = new Circle(8);

                                                cir.setCenterX(block.getLayoutX() + (r.nextInt((int) screenDimens.getWidth() / 8)));
                                                cir.setCenterY(block.getTranslateY() - r.nextInt(50) - r.nextInt((int) screenDimens.getWidth() / 8));
                                                cir.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                                                FadeTransition f = new FadeTransition(Duration.seconds(2), cir);
                                                f.setFromValue(1);
                                                f.setToValue(0);
                                                f.play();

                                                root.getChildren().add(cir);


                                            }
                                            for (int j = 0; j < tokens.size(); j++) {
                                                Token token1 = tokens.get(j);
                                                if (token1.getTokenId() == 0) {
                                                    Ball ball = (Ball) token1;
                                                    ball.translateTransition.play();
                                                } else if (token1.getTokenId() == 1) {
                                                    Block block1 = (Block) token1;
                                                    block1.translateTransition.play();
                                                } else if (token1.getTokenId() == 2) {
                                                    Coin coin = (Coin) token1;
                                                    coin.translateTransition.play();
                                                } else if (token1.getTokenId() == 4) {
                                                    DestroyBlock destroyBlock = (DestroyBlock) token1;
                                                    destroyBlock.translateTransition.play();
                                                } else if (token1.getTokenId() == 5) {
                                                    Magnet magnet = (Magnet) token1;
                                                    magnet.translateTransition.play();
                                                } else if (token1.getTokenId() == 6) {
                                                    Shield shield = (Shield) token1;
                                                    shield.translateTransition.play();
                                                } else if (token1.getTokenId() == 7) {
                                                    Wall wall = (Wall) token1;
                                                    wall.translateTransition.play();
                                                }
                                            }
                                        }
                                    });
                                    timer.cancel();
                                }
                            }, block.weight * 100, block.weight * 100);
                        }
                        else
                            {
                            Random r = new Random();

                            root.getChildren().remove(block);

                            for (int p = 0; p < 20; p++) {

                                Circle cir = new Circle(8);

                                cir.setCenterX(block.getLayoutX() + (r.nextInt((int) screenDimen.getWidth() / 8)));
                                cir.setCenterY(block.getTranslateY() - r.nextInt(50) - r.nextInt((int) screenDimen.getWidth() / 8));
                                cir.setFill(Color.color(Math.random(), Math.random(), Math.random()));
                                FadeTransition f = new FadeTransition(Duration.seconds(2), cir);
                                f.setFromValue(1);
                                f.setToValue(0);
                                f.play();

                                root.getChildren().add(cir);


                            }

                            blockCollision.play();
                            Game.snakeLengthTextView.setText(snakeBody.size() - 1 - block.weight + "");
                            snake.decreaseLengthBy(block.weight, root, screenDimens, block);
                            score.setText(Integer.parseInt(score.getText()) + block.weight + "");
                            tokens.remove(block);
                        }
                    } else if (intersect.getBoundsInLocal().getWidth() != -1 && !(snakeBody.size() - 1 >= block.weight)) {
                        Game.pauseButtonPressed = true;
                        Game.generateObstacles.pause();
                        Game.checkCollisionsWithBlock.pause();
                        Game.checkCollisionsWithBalls.pause();
                        Game.checkCollisionsWithPowerups.pause();
                        for (int j = 0; j < tokens.size(); j++) {
                            Token token1 = tokens.get(j);
                            if (token1.getTokenId() == 0) {
                                Ball ball = (Ball) token1;
                                ball.translateTransition.pause();
                            } else if (token1.getTokenId() == 1) {
                                Block block1 = (Block) token1;
                                block1.translateTransition.pause();
                            } else if (token1.getTokenId() == 2) {
                                Coin coin = (Coin) token1;
                                coin.translateTransition.pause();
                            } else if (token1.getTokenId() == 4) {
                                DestroyBlock destroyBlock = (DestroyBlock) token1;
                                destroyBlock.translateTransition.pause();
                            } else if (token1.getTokenId() == 5) {
                                Magnet magnet = (Magnet) token1;
                                magnet.translateTransition.pause();
                            } else if (token1.getTokenId() == 6) {
                                Shield shield = (Shield) token1;
                                shield.translateTransition.pause();
                            } else if (token1.getTokenId() == 7) {
                                Wall wall = (Wall) token1;
                                wall.translateTransition.pause();
                            }
                        }
                        if (snakeBody.size() > 1) {
                            int num = snakeBody.size() - 1;
                            snake.decreaseLengthForEndGame(num, root, block);
                            Timer timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Game.pauseButtonPressed = false;
                                            player.setScore(Integer.parseInt(score.getText()));
                                            Game.leaderboard.addScores(player);


                                            Game.generateObstacles.stop();
                                            Game.checkCollisionsWithBlock.stop();
                                            Game.checkCollisionsWithBalls.stop();
                                            Game.checkCollisionsWithPowerups.stop();
                                            gameOver.play();

                                            Alert alert = new Alert(AlertType.INFORMATION);
                                            alert.setTitle(null);
                                            alert.setHeaderText(null);
                                            alert.setContentText("Your name: " + player.getName() + "\n" + "Your score: " + player.getScore() + "\n" + "Better luck next time.");

                                            alert.setOnHidden(evt -> Platform.exit());


                                            alert.initModality(Modality.APPLICATION_MODAL);
                                            alert.initOwner(primaryStage);

                                            alert.setOnHidden(evt -> primaryStage.setScene(scene));

                                            alert.show();
                                            Game.gameOver = true;

                                            primaryStage.setFullScreen(true);
                                            root.getChildren().clear();
                                            tokens.clear();
                                            Game.offset = 0;
                                        }
                                    });
                                    timer.cancel();
                                }
                            }, block.weight * 100, block.weight * 100);
                        } else {
                            Timer timer = new Timer();
                            timer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Game.gameOver = true;
                                            Game.pauseButtonPressed = false;
                                            player.setScore(Integer.parseInt(score.getText()));
                                            Game.leaderboard.addScores(player);


                                            Game.generateObstacles.stop();
                                            Game.checkCollisionsWithBlock.stop();
                                            Game.checkCollisionsWithBalls.stop();
                                            Game.checkCollisionsWithPowerups.stop();
                                            gameOver.play();

                                            Alert alert = new Alert(AlertType.INFORMATION);
                                            alert.setTitle(null);
                                            alert.setHeaderText(null);
                                            alert.setContentText("Your name: " + player.getName() + "\n" + "Your score: " + player.getScore() + "\n" + "Better luck next time.");

                                            alert.setOnHidden(evt -> Platform.exit());


                                            alert.initModality(Modality.APPLICATION_MODAL);
                                            alert.initOwner(primaryStage);

                                            alert.setOnHidden(evt -> primaryStage.setScene(scene));

                                            alert.show();

                                            primaryStage.setFullScreen(true);
                                            root.getChildren().clear();
                                            tokens.clear();
                                            Game.offset = 0;
                                        }
                                    });
                                    timer.cancel();
                                }
                            }, 1000, 1000);
                        }
                    }
                }
            }
        }
    }
    /**
     * Function to check collision of the snake with balls and increase the snake length accordingly.
     * @param tokens The stack consisting of all the token currently on the screen.
     * @param root The parent group of the scene
     */
    public void checkCollisionsWithBall(Stack<Token> tokens,Group root)
    {
        Circle snakeHead=snake.getSnakeBody().get(0);
        for(int i=0;i<tokens.size();i++)
        {
            Token token=tokens.get(i);
            if(token.getTokenId()==0)
            {
                Ball ball=(Ball)token;
                Shape intersect=Shape.intersect(ball.getBall(),snakeHead);
                if(intersect.getBoundsInLocal().getWidth()!=-1)
                {
                    root.getChildren().remove(ball);
                    tokens.remove(ball);
                    ballCollision.play();
                    int weight=ball.getWeight();
                    snake.increaseLengthBy(weight,screenDimens,root);
                    Game.snakeLengthTextView.setText(snake.getSnakeBody().size()-1+"");
                }
            }
        }
    }
    /**
     * Function to check collision with any of the available powerups or the wall and do the needful.
     * @param tokens
     * @param root
     * @param primaryStage
     */
    public void checkCollisionsWithPowerups(Stack<Token> tokens, Group root,Stage primaryStage)
    {
        wallCollided=false;
        Circle snakeHead=snake.getSnakeBody().get(0);
        for(int i=0;i<tokens.size();i++)
        {
            Token token=tokens.get(i);
            if(token.getTokenId()==2)
            {
                Coin coin=(Coin)token;
                if(coin.getBoundsInParent().intersects(snakeHead.getBoundsInParent()))
                {
                    root.getChildren().remove(coin);
                    tokens.remove(coin);
                    player.setCoinCount(player.getCoinCount()+1);
                    coinCollision.play();
                }
            }

            else if(token.getTokenId()==4)
            {
            	DestroyBlock destroyBlock=(DestroyBlock)token;
                if(destroyBlock.getBoundsInParent().intersects(snakeHead.getBoundsInParent()))
                {
                    root.getChildren().remove(destroyBlock);
                    
//                  tokens.remove(destroyBlock);
                    int points=0;
                    for(int j=0;j<tokens.size();j++)
                    {
                        if(tokens.get(j).getTokenId()==1)
                        {
                            Block block=(Block)tokens.get(j);
                            if(rectangle.getLayoutBounds().contains(block.getBoundsInParent()))
                            {
                                points=points+block.weight;
                                root.getChildren().remove(block);
                                tokens.remove(block);
                                destroyBlockCollision.play();
                            }
                        }
                    }
                    score.setText(Integer.parseInt(score.getText())+points+"");
                }
            }

            else if(token.getTokenId()==5)
            {
                Magnet magnet=(Magnet)token;
                if(magnet.getBoundsInParent().intersects(snakeHead.getBoundsInParent()))
                {
                	root.getChildren().remove(magnet);
                	
                	for(int j=0;j<tokens.size();j++)
                    {
                        if(tokens.get(j).getTokenId()==2)
                        {
                            Coin block=(Coin)tokens.get(j);
                            if(rectangle.getLayoutBounds().contains(block.getBoundsInParent()))
                            {
                            	if(block.getErasable()==true) {
                            		coinCollision.play();
                            		root.getChildren().remove(block);
                                    tokens.remove(block);
                                    player.setCoinCount(player.getCoinCount()+1);
                            	}
                               
                            }
                        }
                    }

                   
                    snake.setMagnetActivated(true);
                    for(int j=1;j<snake.getSnakeBody().size();j++)
                    {
                        if(j<snake.getSnakeBody().size()/2)
                        {
                            snake.getSnakeBody().get(j).setFill(Color.RED);
                        }
                        else
                        {
                            snake.getSnakeBody().get(j).setFill(Color.WHITE);
                        }
                    }
                    Timer timer=new Timer();
                    class SetMagnet extends TimerTask
                    {
                        public void run()
                        {
                            snake.setMagnetActivated(false);
                            for(int j=1;j<snake.getSnakeBody().size();j++)
                            {
                                snake.getSnakeBody().get(j).setFill(Color.YELLOW);
                            }
                            timer.cancel();
                        }
                    }
                    timer.schedule(new SetMagnet(),5000);
                }
            }

            else if(token.getTokenId()==6)
            {
                Shield shield=(Shield)token;
                if(shield.getBoundsInParent().intersects(snakeHead.getBoundsInParent()))
                {
                	
                    root.getChildren().remove(shield);
                    snake.setShieldState(true);
                    for(int j=1;j<snake.getSnakeBody().size();j++)
                    {
                        snake.getSnakeBody().get(j).setFill(Color.DARKGOLDENROD);
                    }
                    Timer timer=new Timer();
                    class SetShield extends TimerTask
                    {
                        public void run()
                        {
                            snake.setShieldState(false);
                            for(int j=1;j<snake.getSnakeBody().size();j++)
                            {
                                snake.getSnakeBody().get(j).setFill(Color.YELLOW);
                            }
                            timer.cancel();
                        }
                    }
                    timer.schedule(new SetShield(),5000);
                }
            }
            else if(token.getTokenId()==7)
            {
            	 Wall wall=(Wall)token;
            	 if(wall.getBoundsInParent().intersects(snakeHead.getBoundsInParent()))
            	 {
                     wallCollided = true;
                 }
            }
        }
    }
}
