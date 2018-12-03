import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
public class Game extends Application
{
	static Stack<Scene> sceneStack=new Stack<>();   //stack for keeping a track of all scenes. When back pressed pop the scene, when new scene is set push the scene on stack
    static double offset=0;
    static double snakeX=0;
    static Text snakeLengthTextView;
    static Leaderboard leaderboard;
	static Timeline generateObstacles;
	static Timeline checkCollisionsWithBlock;
	static Timeline checkCollisionsWithBalls;
	static Timeline checkCollisionsWithPowerups;
	static boolean pauseButtonPressed=false;
	static boolean gameOver=true;
	static ObjectOutputStream objectOutputStream;
	static ObjectInputStream objectInputStream;
	ArrayList<Object> stateSave;
	File stateSaveData=new File("stateSaveData.dat");
	boolean savedDataAvailable=false;
	AudioClip buttonClick=new AudioClip(new File("button_click.wav").toURI().toString());
	static FileInputStream f;
	static String memSkin = "";

	/**
	 * Pauses the game
	 * @param playGame Object of the PlayGame class which is responsible for the game play
	 * @param tokens A stack containing all the tokens generated in the game
	 */
	public static void pauseGame(PlayGame playGame,Stack<Token> tokens)
	{
		double width=Screen.getPrimary().getBounds().getWidth();
		if(!pauseButtonPressed)
		{
			pauseButtonPressed=true;
			playGame.pauseMenuItem.setText("Play");
			playGame.statusText.setX((width/2)-80);
			playGame.statusText.setText("Paused");
			playGame.statusText.toFront();
			generateObstacles.pause();
			checkCollisionsWithBlock.pause();
			checkCollisionsWithBalls.pause();
			checkCollisionsWithPowerups.pause();
			for(int i=0;i<tokens.size();i++)
			{
				Token token=tokens.get(i);
				if(token.getTokenId()==0)
				{
					Ball ball=(Ball)token;
					ball.translateTransition.pause();
				}
				else if(token.getTokenId()==1)
				{
					Block block=(Block)token;
					block.translateTransition.pause();
				}
				else if(token.getTokenId()==2)
				{
					Coin coin=(Coin)token;
					coin.translateTransition.pause();
				}
				else if(token.getTokenId()==4)
				{
					DestroyBlock destroyBlock=(DestroyBlock) token;
					destroyBlock.translateTransition.pause();
				}
				else if(token.getTokenId()==5)
				{
					Magnet magnet=(Magnet) token;
					magnet.translateTransition.pause();
				}
				else if(token.getTokenId()==6)
				{
					Shield shield=(Shield)token;
					shield.translateTransition.pause();
				}
				else if(token.getTokenId()==7)
				{
					Wall wall=(Wall)token;
					wall.translateTransition.pause();
				}
			}
		}
		else
		{
			Timer timer=new Timer();
			class Start extends TimerTask
			{
				public void run()
				{
					pauseButtonPressed=false;
					playGame.pauseMenuItem.setText("Pause");
					generateObstacles.play();
					checkCollisionsWithBlock.play();
					checkCollisionsWithBalls.play();
					checkCollisionsWithPowerups.play();
					for(int i=0;i<tokens.size();i++)
					{
						Token token=tokens.get(i);
						if(token.getTokenId()==0)
						{
							Ball ball=(Ball)token;
							ball.translateTransition.play();
						}
						else if(token.getTokenId()==1)
						{
							Block block=(Block)token;
							block.translateTransition.play();
						}
						else if(token.getTokenId()==2)
						{
							Coin coin=(Coin)token;
							coin.translateTransition.play();
						}
						else if(token.getTokenId()==4)
						{
							DestroyBlock destroyBlock=(DestroyBlock) token;
							destroyBlock.translateTransition.play();
						}
						else if(token.getTokenId()==5)
						{
							Magnet magnet=(Magnet) token;
							magnet.translateTransition.play();
						}
						else if(token.getTokenId()==6)
						{
							Shield shield=(Shield)token;
							shield.translateTransition.play();
						}
						else if(token.getTokenId()==7)
						{
							Wall wall=(Wall)token;
							wall.translateTransition.play();
						}
					}
					timer.cancel();
				}
			}
			Timer timer1=new Timer();
			playGame.statusText.setX(width/2);
			timer1.scheduleAtFixedRate(new TimerTask()
			{
				int displayTime=3;
				@Override
				public void run()
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							playGame.statusText.setText(displayTime+"");
							displayTime--;
							if(displayTime==-1)
							{
								playGame.statusText.setText("");
								playGame.statusText.toBack();
								timer1.cancel();
							}
						}
					});
				}
			},0,1000);
			timer.schedule(new Start(),3000);
		}
	}

	/**
	 * Resumes the game from the paused state or when the player presses resume if saved game is available
	 * @param playGame Object of the PlayGame class which is responsible for the game play
	 * @param tokens A stack containing all the tokens generated in the game
	 */
	public void resumeGame(PlayGame playGame,Stack<Token> tokens)
	{
		Timer timer=new Timer();
		Timer timer1=new Timer();
		playGame.statusText.toFront();
		Screen screen=Screen.getPrimary();
		double width=screen.getBounds().getWidth();
		playGame.statusText.setX(width/2);
		class Start extends TimerTask
		{
			public void run()
			{
				playGame.pauseMenuItem.setText("Pause");

				pauseButtonPressed=false;
				generateObstacles.play();
				checkCollisionsWithBlock.play();
				checkCollisionsWithBalls.play();
				checkCollisionsWithPowerups.play();
				for(int i=0;i<tokens.size();i++)
				{
					Token token=tokens.get(i);
					if(token.getTokenId()==0)
					{
						Ball ball=(Ball)token;
						ball.translateTransition.play();
					}
					else if(token.getTokenId()==1)
					{
						Block block=(Block)token;
						block.translateTransition.play();
					}
					else if(token.getTokenId()==2)
					{
						Coin coin=(Coin)token;
						coin.translateTransition.play();
					}
					else if(token.getTokenId()==4)
					{
						DestroyBlock destroyBlock=(DestroyBlock) token;
						destroyBlock.translateTransition.play();
					}
					else if(token.getTokenId()==5)
					{
						Magnet magnet=(Magnet) token;
						magnet.translateTransition.play();
					}
					
					else if(token.getTokenId()==6)
					{
						Shield shield=(Shield)token;
						shield.translateTransition.play();
					}
					else if(token.getTokenId()==7)
					{
						Wall wall=(Wall)token;
						wall.translateTransition.play();
					}
				}
				timer.cancel();
			}
		}

		timer1.scheduleAtFixedRate(new TimerTask()
		{
			int displayTime=3;
			@Override
			public void run()
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						playGame.statusText.setText(displayTime+"");
						playGame.statusText.toFront();
						displayTime--;
						if(displayTime==-1)
						{
							playGame.statusText.setText("");
							playGame.statusText.toBack();
							timer1.cancel();
						}
					}
				});
			}
		},0,1000);
		timer.schedule(new Start(),3000);
	}
	/**
	 * The start method to run the application
	 */
    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException
    {
    	f = new FileInputStream("emoji1.png");
    	memSkin = "emoji1.png";
    	
    	StackPane stackPanea = new StackPane(); 
		
	    ObservableList<Node> lista = stackPanea.getChildren(); 

        FileInputStream b = new FileInputStream("emoji1.png");
        Image butimageb = new Image(b);
        ImageView imageViewbutb = new ImageView(butimageb);
        imageViewbutb.setFitWidth(200);
    	imageViewbutb.setFitHeight(200);
    	
    	
    	FileInputStream c = new FileInputStream("emoji2.png");
        Image startimc = new Image(c);
        ImageView imageViewstartc = new ImageView(startimc);
        imageViewstartc.setFitWidth(200);
    	imageViewstartc.setFitHeight(200);
    	
    	FileInputStream d = new FileInputStream("emoji3.png");
        Image resumeimd = new Image(d);
        ImageView imageViewresumed = new ImageView(resumeimd);
        imageViewresumed.setFitWidth(200);
    	imageViewresumed.setFitHeight(200);
       
        Button nea = new Button("",imageViewstartc);
        Button leadera = new Button("", imageViewbutb);
        Button resumea = new Button("", imageViewresumed);
        
        
        nea.setMaxWidth(200);
        nea.setMaxHeight(200);
        
        leadera.setMaxWidth(200);
        leadera.setMaxHeight(200);
        
        resumea.setMaxWidth(200);
        resumea.setMaxHeight(200);
        
        
        
		nea.setVisible(true);
		nea.setTranslateX(-650);
		nea.setTranslateY(-320);
		
		leadera.setVisible(true);
		leadera.setTranslateX(-400);
		leadera.setTranslateY(-320);
		
		
		resumea.setVisible(true);
		resumea.setTranslateX(-150);
		resumea.setTranslateY(-320);
		
		nea.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
		leadera.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
		resumea.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
		
		
		FileInputStream back = new FileInputStream("back.png");
        Image backim = new Image(back);
        ImageView imageViewback = new ImageView(backim);
        imageViewback.setFitWidth(180);
    	imageViewback.setFitHeight(50);
		
		Button backButton = new Button("",imageViewback);
   	 	backButton.setVisible(true);
   	 	backButton.setTranslateX(600);
   	 	backButton.setTranslateY(-380);
   	 	backButton.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
   	 	backButton.setFocusTraversable(false);

		lista.addAll(nea,leadera, resumea,backButton); 
		Screen screen=Screen.getPrimary();
		Rectangle2D screenDimens=screen.getBounds();

		Scene scene2a = new Scene(stackPanea, screenDimens.getWidth(), screenDimens.getHeight(),Color.BLACK);
		
		
		nea.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
				try {
					f = new FileInputStream("emoji2.png");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	memSkin = "emoji2.png";

    		}
		});
		
		leadera.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
				try {
					f = new FileInputStream("emoji1.png");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	memSkin = "emoji1.png";


    		}
		});
		
		resumea.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
				try {
					f = new FileInputStream("emoji3.png");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	memSkin = "emoji3.png";


    		}
		});
		
    	
    	
    	
    	FileInputStream pic1 = new FileInputStream("emoji1.png");
    	FileInputStream pic2 = new FileInputStream("emoji2.png");
    	FileInputStream pic3 = new FileInputStream("emoji3.png"); //////////////////// skin ends here.
    	
    	FileInputStream skins = new FileInputStream("skins.png");
        Image skinim = new Image(skins);
        ImageView imageViewskin= new ImageView(skinim);
        imageViewskin.setFitWidth(180);
    	imageViewskin.setFitHeight(50);
    	
    	Button skinBut = new Button("", imageViewskin);
    	skinBut.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
    	
    	
        primaryStage.setFullScreenExitHint("");
        StackPane stackPane = new StackPane();
	    ObservableList<Node> list = stackPane.getChildren();
		primaryStage.setFullScreen(true);

		FileInputStream input = new FileInputStream("snake3.jpg");
		Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1000);
        imageView.setFitHeight(500);

        FileInputStream butim = new FileInputStream("leader.png");
        Image butimage = new Image(butim);
        ImageView imageViewbut = new ImageView(butimage);
        imageViewbut.setFitWidth(180);
    	imageViewbut.setFitHeight(50);


    	FileInputStream start = new FileInputStream("start.png");
        Image startim = new Image(start);
        ImageView imageViewstart = new ImageView(startim);
        imageViewstart.setFitWidth(180);
    	imageViewstart.setFitHeight(50);

    	FileInputStream resumei = new FileInputStream("resume.png");
        Image resumeim = new Image(resumei);
        ImageView imageViewresume = new ImageView(resumeim);
        imageViewresume.setFitWidth(180);
    	imageViewresume.setFitHeight(50);


        Button ne = new Button("",imageViewstart);
        Button leader = new Button("", imageViewbut);
        Button resume = new Button("", imageViewresume);


		ne.setVisible(true);
		ne.setTranslateX(100);
		ne.setTranslateY(-180);

		leader.setVisible(true);
		leader.setTranslateX(350);
		leader.setTranslateY(-180);


		resume.setVisible(true);
		resume.setTranslateX(100);
		resume.setTranslateY(170);
		
		skinBut.setVisible(true);
		skinBut.setTranslateX(350);
		skinBut.setTranslateY(170);
		

		ne.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
		leader.setStyle("-fx-font: 22 arial; -fx-base: #000000;");
		resume.setStyle("-fx-font: 22 arial; -fx-base: #000000;");

		list.addAll(imageView,ne,leader, resume,skinBut);

//		Screen screen=Screen.getPrimary();
//		Rectangle2D screenDimens=screen.getBounds();


		 Scene scene2 = new Scene(stackPane,screenDimens.getWidth(),screenDimens.getHeight(),Color.BLACK);
		 primaryStage.setScene(scene2);
		 sceneStack.push(scene2);

		 StackPane stackPanel = new StackPane();
		 ObservableList<Node> listl = stackPanel.getChildren();

		 FileInputStream inputl = new FileInputStream("black.png");
		 Image imagel = new Image(inputl);
		 ImageView imageViewl = new ImageView(imagel);
		 imageViewl.setFitWidth(2000);
		 imageViewl.setFitHeight(1000);

		 FileInputStream butiml = new FileInputStream("leader.png");
		 Image butimagel = new Image(butiml);
		 ImageView imageViewbutl = new ImageView(butimagel);
		 imageViewbutl.setFitWidth(360);
		 imageViewbutl.setFitHeight(100);
		 imageViewbutl.setTranslateY(-300);

//		 FileInputStream back = new FileInputStream("back.png");
//		 Image backim = new Image(back);
//		 ImageView imageViewback = new ImageView(backim);
//		 imageViewback.setFitWidth(100);
//		 imageViewback.setFitHeight(40);
		 
		 

		 Button backBut = new Button("",imageViewback);
		 backBut.setVisible(true);
		 backBut.setTranslateX(640);
		 backBut.setTranslateY(-420);
		 backBut.setFocusTraversable(false);
		 backBut.setStyle("-fx-font: 22 arial; -fx-base: #000000;");

		 listl.addAll(imageViewl,imageViewbutl, backBut);

		leaderboard=new Leaderboard(screenDimens);
		StackPane leaderboardStackPane=new StackPane(leaderboard.setLeaderboard(screenDimens),backBut);
		leaderboardStackPane.getStylesheets().add(getClass().getResource("finalCSS.css").toExternalForm());
		Scene scene2l = new Scene(leaderboardStackPane,screenDimens.getWidth(),screenDimens.getHeight());
		 

        primaryStage.setTitle("Snake vs Block");
        primaryStage.setFullScreen(true);

		Stack<Token> tokens=new Stack<>();
		Group gameRoot=new Group();
		Snake snake=new Snake();
		Scene scene=new Scene(gameRoot,screenDimens.getWidth(),screenDimens.getHeight(),Color.BLACK);

        if(!stateSaveData.exists())
        {
            stateSaveData.createNewFile();
            objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
            stateSave=new ArrayList<>();
            objectOutputStream.writeObject(stateSave);
            objectOutputStream.flush();
            objectOutputStream.close();
        }

		objectInputStream=new ObjectInputStream(new FileInputStream(stateSaveData));
		stateSave=(ArrayList<Object>)objectInputStream.readObject();
		objectInputStream.close();

		if(stateSave.size()==0)
        {
            savedDataAvailable=false;
        }
        else
        {
            savedDataAvailable=true;
        }
		
		PlayGame playGame=new PlayGame(gameRoot,snake,primaryStage);
		primaryStage.getIcons().add(new Image("file:icon.png"));
		generateObstacles=new Timeline(new KeyFrame(Duration.seconds(2), event1->
		{
			try
			{
				playGame.obstaclePicker(gameRoot,Screen.getPrimary().getBounds(),tokens);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}));
		generateObstacles.setCycleCount(Timeline.INDEFINITE);

		checkCollisionsWithBlock=new Timeline(new KeyFrame(Duration.seconds(0.1), event2-> playGame.checkCollisionsWithBlock(tokens,gameRoot,primaryStage,scene2)));
		checkCollisionsWithBlock.setCycleCount(Timeline.INDEFINITE);

		checkCollisionsWithBalls=new Timeline(new KeyFrame(Duration.seconds(0.1), event3-> playGame.checkCollisionsWithBall(tokens,gameRoot)));
		checkCollisionsWithBalls.setCycleCount(Timeline.INDEFINITE);

		checkCollisionsWithPowerups=new Timeline(new KeyFrame(Duration.seconds(0.01), event4-> playGame.checkCollisionsWithPowerups(tokens,gameRoot,primaryStage)));
		checkCollisionsWithPowerups.setCycleCount(Timeline.INDEFINITE);

        
		ne.setOnAction(new EventHandler<ActionEvent>()
        {
			@Override
    		public void handle(ActionEvent event)
            {
				playGame.pauseMenuItem.setText("Pause");
				pauseButtonPressed = false;
                buttonClick.play();
			    stateSave.clear();
			    savedDataAvailable=false;
                Game.generateObstacles.stop();
                Game.checkCollisionsWithBlock.stop();
                Game.checkCollisionsWithBalls.stop();
                Game.checkCollisionsWithPowerups.stop();
              
               
                gameRoot.getChildren().clear();
                tokens.clear();
                Game.offset=0;
                
                
                Random random=new Random();
				int playerId=random.nextInt(100);
        		Player player=new Player("Player"+playerId);
        		playGame.setPlayer(player);
				gameRoot.getChildren().add(playGame.rectangle);
				gameRoot.getChildren().add(playGame.statusText);
				gameRoot.getChildren().add(playGame.playerName);
        		snake.getSnakeBody().clear();
        		playGame.getScore().setText("0");
        		playGame.statusText.setText("");
        		playGame.playerName.setText(player.getName());
                snakeX=screenDimens.getWidth()/2;
                try
				{
					f = new FileInputStream(memSkin);
				}
				catch (FileNotFoundException e1)
				{
					e1.printStackTrace();
				}
        		snake.increaseLengthBy(5,screenDimens,gameRoot,f);
        		snake.setMagnetActivated(false);
        		snake.setShieldState(false);
        		
				snakeLengthTextView=new Text(screenDimens.getWidth()/2-10,(screenDimens.getHeight()/2+150)-40,snake.getSnakeBody().size()-1+"");
				snakeLengthTextView.setFont(Font.font("verdana",15));
				snakeLengthTextView.setFill(Color.WHITE);
				gameRoot.getChildren().add(snakeLengthTextView);
				
				gameRoot.getChildren().add(playGame.menuButton);
				gameRoot.getChildren().add(playGame.getScore());
				
				Task<Void> sleeper = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
						}
						return null;
					}
				};
				sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
					@Override
					public void handle(WorkerStateEvent event) {
						
						primaryStage.setTitle("Game");
		        		primaryStage.setScene(scene);
		        		primaryStage.setFullScreen(true);
		        		

						generateObstacles.playFromStart();
		        		checkCollisionsWithBalls.playFromStart();
		        		checkCollisionsWithBlock.playFromStart();
		        		checkCollisionsWithPowerups.playFromStart();
						sceneStack.push(scene);
						pauseButtonPressed=false;
						gameOver=false;
					}
				});
				new Thread(sleeper).start();
				
        		
    		}
		});

		leader.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
                buttonClick.play();
			    primaryStage.setTitle("Leaderboard");
                try
                {
                    leaderboard.setLeaderboard(screenDimens);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                sceneStack.push(scene2l);
        		primaryStage.setScene(scene2l);
        		primaryStage.setFullScreen(true);
    		}
		});
		
		skinBut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
                primaryStage.setFullScreen(true);
			    primaryStage.setTitle("Skins");
			    sceneStack.push(scene2a);
                primaryStage.setScene(scene2a);

    		}
		});
		
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
    		public void handle(ActionEvent event) {
				primaryStage.setTitle("MainPage");
                sceneStack.pop();
                primaryStage.setScene(scene2);
                primaryStage.setFullScreen(true);

    		}
		});
		

        resume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                if(!gameOver)
                {
                	playGame.pauseMenuItem.setText("Pause");
                    primaryStage.setTitle("Game");
                    primaryStage.setScene(scene);
                    primaryStage.setFullScreen(true);
                    buttonClick.play();
                    resumeGame(playGame, tokens);
                }
                else if(savedDataAvailable)
                {
        			playGame.pauseMenuItem.setText("Pause");
                    buttonClick.play();
                    savedDataAvailable=false;
                    int snakeLength=(Integer)stateSave.get(0);
                    int score=(Integer)stateSave.get(1);
                    Player player=(Player)stateSave.get(2);
                    Game.generateObstacles.stop();
                    Game.checkCollisionsWithBlock.stop();
                    Game.checkCollisionsWithBalls.stop();
                    Game.checkCollisionsWithPowerups.stop();


                    gameRoot.getChildren().clear();
                    tokens.clear();
                    Game.offset=0;


                    Random random=new Random();
                    playGame.setPlayer(player);
                    gameRoot.getChildren().add(playGame.rectangle);
                    snake.getSnakeBody().clear();
                    playGame.getScore().setText(score+"");
                    snakeX=screenDimens.getWidth()/2;
                    try
					{
    					f = new FileInputStream((String)stateSave.get(3));
    				}
    				catch (FileNotFoundException e1)
					{
    					// TODO Auto-generated catch block
    					e1.printStackTrace();
    				}
                    snake.increaseLengthBy(snakeLength,screenDimens,gameRoot,f);
                    snake.setMagnetActivated(false);
                    snake.setShieldState(false);

                    snakeLengthTextView=new Text(screenDimens.getWidth()/2-10,(screenDimens.getHeight()/2+150)-40,snake.getSnakeBody().size()-1+"");
                    snakeLengthTextView.setFont(Font.font("verdana",15));
                    snakeLengthTextView.setFill(Color.WHITE);
                    gameRoot.getChildren().add(snakeLengthTextView);

                    gameRoot.getChildren().add(playGame.menuButton);
                    gameRoot.getChildren().add(playGame.getScore());
                    playGame.playerName.setText(player.getName());
                    gameRoot.getChildren().add(playGame.playerName);

                    Task<Void> sleeper = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                            }
                            return null;
                        }
                    };
                    sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {

                            primaryStage.setTitle("Game");
                            primaryStage.setScene(scene);
                            primaryStage.setFullScreen(true);


                            generateObstacles.playFromStart();
                            checkCollisionsWithBalls.playFromStart();
                            checkCollisionsWithBlock.playFromStart();
                            checkCollisionsWithPowerups.playFromStart();
                            sceneStack.push(scene);
                            gameOver=false;
                        }
                    });
                    new Thread(sleeper).start();
                }
            }
        });

        playGame.mainMenuMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buttonClick.play();
                pauseButtonPressed=true;
                generateObstacles.pause();
                checkCollisionsWithBlock.pause();
                checkCollisionsWithBalls.pause();
                checkCollisionsWithPowerups.pause();
                for(int i=0;i<tokens.size();i++)
                {
                    Token token=tokens.get(i);
                    if(token.getTokenId()==0)
                    {
                        Ball ball=(Ball)token;
                        ball.translateTransition.pause();
                    }
                    else if(token.getTokenId()==1)
                    {
                        Block block=(Block)token;
                        block.translateTransition.pause();
                    }
                    else if(token.getTokenId()==2)
                    {
                        Coin coin=(Coin)token;
                        coin.translateTransition.pause();
                    }
                    else if(token.getTokenId()==4)
                    {
                        DestroyBlock destroyBlock=(DestroyBlock) token;
                        destroyBlock.translateTransition.pause();
                    }
                    else if(token.getTokenId()==5)
                    {
                        Magnet magnet=(Magnet) token;
                        magnet.translateTransition.pause();
                    }
                    else if(token.getTokenId()==6)
                    {
                        Shield shield=(Shield)token;
                        shield.translateTransition.pause();
                    }
                    else if(token.getTokenId()==7)
                    {
                        Wall wall=(Wall)token;
                        wall.translateTransition.pause();
                    }
                }
                primaryStage.setTitle("MainPage");
                primaryStage.setScene(scene2);
                primaryStage.setFullScreen(true);
            }
        });

        playGame.pauseMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                buttonClick.play();
                pauseGame(playGame,tokens);
            }
        });

        playGame.restartMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
				playGame.pauseMenuItem.setText("Pause");
				pauseButtonPressed = false;
                generateObstacles.stop();
                checkCollisionsWithBlock.stop();
                checkCollisionsWithBalls.stop();
                checkCollisionsWithPowerups.stop();
                buttonClick.play();

                Game.offset = 0;

                gameRoot.getChildren().clear();


                snake.getSnakeBody().clear();
                gameRoot.getChildren().add(playGame.menuButton);
                try {
					f = new FileInputStream(memSkin);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                snake.increaseLengthBy(5,screenDimens,gameRoot,f);
                Game.snakeLengthTextView.setText(snake.getSnakeBody().size()-1+"");
                gameRoot.getChildren().add(Game.snakeLengthTextView);
                playGame.getScore().setText("0");
                playGame.playerName.setText(playGame.getPlayer().getName());
                gameRoot.getChildren().addAll(playGame.getScore(),playGame.playerName);

                Task<Void> sleeper = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch (InterruptedException e)
                        {
                        }
                        return null;
                    }
                };
                sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        generateObstacles.playFromStart();
                        checkCollisionsWithBlock.playFromStart();
                        checkCollisionsWithBalls.playFromStart();
                        checkCollisionsWithPowerups.playFromStart();

                    }
                });
                new Thread(sleeper).start();
            }
        });

        backBut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.setTitle("MainPage");
                sceneStack.pop();
                buttonClick.play();
                primaryStage.setScene(scene2);
                primaryStage.setFullScreen(true);
                try
                {
                    leaderboard.saveLeaderboard();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.show();


        scene.setOnKeyPressed(event->
        {
            if(event.getCode()==KeyCode.ESCAPE)
            {
                try
                {
                    if(!gameOver)
                    {
                        stateSave.clear();
                        stateSave.add(snake.getSnakeBody().size());
                        stateSave.add(Integer.parseInt(playGame.getScore().getText()));
                        stateSave.add(playGame.getPlayer());
                        stateSave.add(memSkin);
                    }
                    else
                    {
                        stateSave.clear();
                    }
                    objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
                    objectOutputStream.writeObject(stateSave);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
                catch(Exception e)
                {

                }
                Platform.exit();
            }
        });

        scene2.setOnKeyPressed(event->
        {
            if(event.getCode()==KeyCode.ESCAPE)
            {
                if(pauseButtonPressed)
                {
                    try
                    {
                        if(!gameOver)
                        {
                            stateSave.clear();
                            stateSave.add(snake.getSnakeBody().size());
                            stateSave.add(Integer.parseInt(playGame.getScore().getText()));
                            stateSave.add(playGame.getPlayer());
							stateSave.add(memSkin);
                        }
                        else
                        {
                            stateSave.clear();
                        }
                        objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
                        objectOutputStream.writeObject(stateSave);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    }
                    catch(Exception e)
                    {

                    }
                }
                else
                {
                    try
                    {
                        stateSave.clear();
                        objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
                        objectOutputStream.writeObject(stateSave);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    }
                    catch(Exception e)
                    {

                    }
                }
                Platform.exit();
            }
        });

        scene2l.setOnKeyPressed(event->
        {
            if(event.getCode()==KeyCode.ESCAPE)
            {
                if(pauseButtonPressed)
                {
                    try
                    {
                        if(!gameOver)
                        {
                            stateSave.clear();
                            stateSave.add(snake.getSnakeBody().size());
                            stateSave.add(Integer.parseInt(playGame.getScore().getText()));
                            stateSave.add(playGame.getPlayer());
							stateSave.add(memSkin);
                        }
                        else
                        {
                            stateSave.clear();
                        }
                        objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
                        objectOutputStream.writeObject(stateSave);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    }
                    catch(Exception e)
                    {

                    }
                }
                else
                {
                    try
                    {
                        stateSave.clear();
                        objectOutputStream=new ObjectOutputStream(new FileOutputStream(stateSaveData));
                        objectOutputStream.writeObject(stateSave);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                    }
                    catch(Exception e)
                    {

                    }
                }
                Platform.exit();
            }
        });

		scene.setOnMouseDragged(event->
		{
			if(!playGame.wallCollided) {
				ArrayList<Circle> snakeBody=snake.getSnakeBody();
				if(!pauseButtonPressed)
				{
					snakeBody.get(0).setCenterX(event.getSceneX());
					snakeLengthTextView.setX(event.getSceneX());
					if(snakeX!=event.getSceneX())
					{
						for(int i=1;i<snakeBody.size();i++)
						{

							Path path=new Path();
							MoveTo moveTo=new MoveTo(snakeX,snakeBody.get(i).getCenterY());
							LineTo lineTo=new LineTo(event.getSceneX(),snakeBody.get(i).getCenterY());
							path.getElements().addAll(moveTo,lineTo);
							PathTransition pathTransition=new PathTransition();
							pathTransition.setPath(path);
							pathTransition.setNode(snakeBody.get(i));
							pathTransition.setDelay(Duration.millis(i*20));
							pathTransition.setDuration(Duration.millis(200));
							pathTransition.setCycleCount(1);
							pathTransition.setAutoReverse(false);
							pathTransition.play();
						}
						snakeX=event.getSceneX();
					}

				}
			}

		});
//        scene.setOnMouseDragged(event->
//		{
//			if(!pauseButtonPressed)
//			{
//				ArrayList<Circle> snakeBody=snake.getSnakeBody();
//				for(int i=0;i<snake.getSnakeBody().size();i++)
//				{
//					snakeBody.get(i).setCenterX(event.getSceneX());
//				}
//				snakeLengthTextView.setX(event.getSceneX());
//				snakeX=event.getSceneX();
//			}
//		});


    }

    public static void main(String[] args)
    {
        launch(args);
    }
}