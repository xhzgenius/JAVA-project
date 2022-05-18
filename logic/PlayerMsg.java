package logic;
/**** 通用消息接口  ****/
/**
 * Usage: PlayerMsg msg = new PlayerMsg().Builder(ex_id).name(ex_name).level(0).build()
 */
public class PlayerMsg {
     public int Id;
     public String name;
     public int coin;
     public String operation;
     public int level;

     /*** 生成器 ***/
     public static class Builder{
          private final int Id;
          private String name="";
          private int coin=0;
          private String operation="";
          private int level=0;

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
     public void setOperation(String val){
          this.operation = val;
     }
     public void setCoin(int val){
          this.coin = val;
     }
     public void setLevel(int val){
          this.level = val;
     }
     public void setId(int val){
          this.Id = val;
     }
     public void setName(String val){
          this.name = val;
     }
}
