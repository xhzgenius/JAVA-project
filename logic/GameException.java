package logic;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)//只有生命周期是运行时，那么才可以被反射读取
@Target(ElementType.FIELD)
@interface GameExceptionMessage{
  String value();
}

/**
 * 游戏中非法操作造成的Exception，比如铸币不足3时试图买随从，或者从手牌里放一个不存在的随从等等。
 *                                                                                 ——XHZ
 * 单独创建这个类只是为了便于在catch的时候识别。
 */
public class GameException extends Exception {
    public enum GameExceptionType {
        /** 铸币不足购买 */
        @GameExceptionMessage("铸币不足，无法购买。")
        ENROLL_NO_ENOUGH_COIN,

        /** 随从已满 */
        @GameExceptionMessage("随从数量已满7个，无法进场。")
        BATTLEFIELD_FULL,

        /** 找不到随从 */
        @GameExceptionMessage("您在试图卖出一个自己没有的随从！")
        SELL_FELLOW_NOT_FOUND,

        /** 铸币不足刷新 */
        @GameExceptionMessage("铸币不足，无法刷新商店。")
        REFRESH_NO_ENOUGH_COIN,

        /** 铸币不足升级 */
        @GameExceptionMessage("铸币不足，无法升级商店。")
        UPGRADE_NO_ENOUGH_COIN,

        /** 找不到随从 */
        @GameExceptionMessage("您在试图施放一个自己没有的随从！")
        CAST_FELLOW_NOT_FOUND,

        /** 手牌太多 */
        @GameExceptionMessage("您的手牌已经满了！")
        INVENTORY_FULL, 

        /** 酒馆满级，无法升级 */
        @GameExceptionMessage("酒馆等级已达到最高！")
        MAX_LEVEL
    }

    public GameExceptionType type;

    public GameException(GameExceptionType type)
    {
        super(getMessage(type));
        this.type = type;
    }

    static String getMessage(GameExceptionType type) {
        try {
            return ((GameExceptionMessage)GameExceptionType.class.getField(type.name()).getAnnotation(GameExceptionMessage.class)).value();
        } catch(Exception e) {
            // unreachable
        }
        return "";
    }
}
