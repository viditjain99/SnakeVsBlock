import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Player implements Serializable
{
    private String name;
    private int score;
    private String dateAndTime;
    private boolean gameOver;
    private Snake snake;
    private int coinCount;
    int marked;
    
    /**
     * Constructor to setup the player data.
     * @param name Name of the player
     */

    public Player(String name)
    {
        this.name=name;
        this.score=0;
        this.gameOver=false;
        this.snake=new Snake();
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime localDateTime=LocalDateTime.now();
        this.dateAndTime=dateTimeFormatter.format(localDateTime);
        marked=0;
    }
    
    /**
     * Function to set the score of the player
     * @param score The score to set
     */

    public void setScore(int score)
    {
        this.score=score;
    }
    /**
     * Function to get the score of the player
     * @return the player score
     */
    public int getScore()
    {
        return this.score;
    }
    
    /**
     * Function to get the current  and time.
     * @return the current date and time
     */
    public String getDate()
    {
        return this.dateAndTime;
    }
    
    /**
     * Function to get the name of the player
     * @return the name of the player
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Function to get the current date and time.
     * @return the current date and time.
     */
    public String getDateAndTime()
    {
        return this.dateAndTime;
    }
    
    /**
     * Function to get the number of coins the player has earned.
     * @return player's coin count
     */

    public int getCoinCount()
    {
        return this.coinCount;
    }
    
    /**
     * Function to set the number of coins in the player's vault.
     * @param coinCount number of coins to be placed in the player's vault.
     */

    public void setCoinCount(int coinCount)
    {
        this.coinCount=coinCount;
    }
}