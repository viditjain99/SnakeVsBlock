import javafx.animation.TranslateTransition;

public interface Token
{
	/**
	 * Function to get the ID the the token the snake has collided with.
	 * @return the ID of the token
	 */
    int getTokenId();
}