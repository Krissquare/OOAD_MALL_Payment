package cn.edu.xmu.oomall.order.model.bo;
import cn.edu.xmu.oomall.core.util.ReturnNo;
public enum OrderState {
    //100 待付款
    NEW_ORDER(101,"新订单"),
    WAIT_PAY_REST(102,"待支付尾款"),
    //200 待收货
    FINISH_PAY(201,"付款完成"),
    WAIT_GROUP(202,"待成团"),
    NOT_FROUP(203,"未成团"),
    //300 已发货
    SEND_GOODS(300,"已发货"),
    //400 已完成
    COMPLETE_ORDER(400,"已完成"),
    //500 已取消
    CANCEL_ORDER(500,"已取消");
    private int code;
    private String message;
    OrderState(int code, String message){
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }
}
