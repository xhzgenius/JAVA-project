package logic;
public class Bot {
    private Game game; // 该bot能调用整个game对象的公共方法
    private int playerID;
    public Bot(Game game, int playerID)
    {
        this.game = game;
        this.playerID = playerID;
    }
    void act() throws GameException
    {
        // call getShowFellows and enroll the first one
        System.out.println(String.format("[Bot] %d act", playerID));
        System.out.println(game.getShowFellows(playerID).toString());
        Fellow fellow = game.getShowFellows(playerID).get(0);
        game.enroll(playerID, fellow);
        game.cast(playerID, fellow, 0);
    }
}
