/**** 通用消息接口  ****/
/**
 * Usage: PlayerMsg msg = new PlayerMsg().Builder(ex_id).name(ex_name).level(0).build()
 */
public class PlayerMsg {
     public final int Id;
     public final String name;
     public final int coin=0;
     public final String operation="";
     public final int level=0;

     /*** 生成器 ***/
     public static class Builder{
          private final int Id;
          private final String name="";
          private final int coin=0;
          private final String operation="";
          private final int level=0;

          public  Builder(int id){
               this.Id=id;
          }
          public Builder name(String val){
               this.name = val;
               return this;
          }
          public Builder coin(int val){
               this.coin = val;
               return this;
          }
          public Builder operation(String val){
               this.operation = val;
               return this;
          }
          public Builder level(int val){
               this.level = val;
               return this;
          }
          public PlayerMsg build(){
               return new PlayerMsg(this);
          }
     }
     public PlayerMsg(Builder build){
          Id = build.Id;
          name = build.name;
          coin = build.coin;
          operation = build.operation;
          level = build.level;
     }
     /****   Setter  *****/
     public setOperation(String val){
          this.operation = val;
     }
     public setCoin(int val){
          this.operation = val;
     }
     public setLevel(int val){
          this.level = val;
     }
     public setId(int val){
          this.level = val;
     }
     public setName(String val){
          this.name = val;
     }
}
