package cn.edu.xmu.oomall.ordermq.model.vo;

import cn.edu.xmu.oomall.ordermq.model.bo.Order;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发消息用的vo
 *
 * @author xiuchen lang 22920192204222
 * @date 2021/12/11 22:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAndOrderItemsVo {
    Order order;
    List<OrderItem> orderItems;
}
