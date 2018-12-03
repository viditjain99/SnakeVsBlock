import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Leaderboard
{
    ArrayList<Player> leaderboardData=new ArrayList<>();
    ListView leaderboardListView;
    File leaderboard=new File("leaderboard.dat");
    ObjectOutputStream objectOutputStream;

    /**
     * Function to setup the leaderboard
     * @param screenDimens A rectangle with same dimensions as of the screen
     */
    public Leaderboard(Rectangle2D screenDimens)
    {
        leaderboardListView=new ListView();
        leaderboardListView.getItems().clear();
        try
        {
            if(!leaderboard.exists())
            {
                leaderboard.createNewFile();
            }
            else
            {
                ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(leaderboard));
                leaderboardData=(ArrayList<Player>) objectInputStream.readObject();
                objectInputStream.close();
            }
        }
        catch(Exception e)
        {

        }
    }
    /**
     * Function to add the player data to the leaderboard
     * @param player The player instance to be added to the leaderboard
     */
    public void addScores(Player player)
    {
        leaderboardData.add(player);
    }
    
    /**
     * 
     * @param screenDimens A rectangle with same dimensions as of the screen
     * @return the list corresponding to the sorted leaderboard
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ListView setLeaderboard(Rectangle2D screenDimens) throws IOException, ClassNotFoundException
    {
        leaderboardListView.getItems().clear();
        Text leaderboardLabel=new Text("\t"+"\t"+"\t"+"\t"+"\t"+"Leaderboard");
        leaderboardLabel.setFill(Color.WHITE);
        leaderboardLabel.setFont(Font.font("verdana",40));
        leaderboardListView.setFixedCellSize(screenDimens.getHeight()/11);
        leaderboardListView.setFocusTraversable(false);
        leaderboardListView.getItems().add(leaderboardLabel);
        if(leaderboardData.size()==0)
        {
            Text text=new Text("\t"+"\t"+"\t"+"\t"+"  "+"Leaderboard is empty");
            text.setFill(Color.WHITE);
            text.setFont(Font.font("verdana",40));
            leaderboardListView.getItems().add(text);
        }
        else
        {
            Collections.sort(leaderboardData,Comparator.comparingInt(Player::getScore).reversed());
            if(leaderboardData.size()<10)
            {
                for(int i=0;i<leaderboardData.size();i++)
                {
                    Text text=new Text((i+1)+"."+"\t"+leaderboardData.get(i).getName()+"\t"+leaderboardData.get(i).getScore()+"\t"+leaderboardData.get(i).getDateAndTime());
                    text.setFont(Font.font("verdana",40));
                    text.setFill(Color.WHITE);
                    text.setTextAlignment(TextAlignment.CENTER);
                    leaderboardListView.getItems().add(text);
                    leaderboardListView.setFixedCellSize(screenDimens.getHeight()/11);
                    leaderboardListView.setFocusTraversable(false);
                }
            }
            else
            {
                for(int i=0;i<10;i++)
                {
                    Text text=new Text((i+1)+"."+"\t"+leaderboardData.get(i).getName()+"\t"+leaderboardData.get(i).getScore()+"\t"+leaderboardData.get(i).getDateAndTime());
                    text.setFont(Font.font("verdana",40));
                    text.setFill(Color.WHITE);
                    text.setTextAlignment(TextAlignment.CENTER);
                    leaderboardListView.getItems().add(text);
                    leaderboardListView.setFixedCellSize(screenDimens.getHeight()/11);
                    leaderboardListView.setFocusTraversable(false);
                }
            }
            for(int i=0;i<leaderboardData.size();i++)
            {
                leaderboardData.get(i).marked=0;
            }
        }
        return leaderboardListView;
    }
    
    /**
     * Function to serialise the leaderboard in case the player exits the game.
     * @throws IOException
     */
    public void saveLeaderboard() throws IOException
    {
        objectOutputStream=new ObjectOutputStream(new FileOutputStream(leaderboard));
        objectOutputStream.writeObject(leaderboardData);
        objectOutputStream.flush();
        objectOutputStream.close();
    }
}