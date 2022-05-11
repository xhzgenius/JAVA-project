/**
 * 游戏中非法操作造成的Exception，比如铸币不足3时试图买随从，或者从手牌里放一个不存在的随从等等。
 *                                                                                 ——XHZ
 * 单独创建这个类只是为了便于在catch的时候识别。
 */

public class GameException extends Exception {
    public GameException(String message)
    {
        super(message);
    }
}
