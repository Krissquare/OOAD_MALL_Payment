package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.GoodsService;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentState;
import cn.edu.xmu.oomall.order.microservice.vo.QuantityVo;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderItemPo;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.service.mq.vo.PaymentNotifyMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * 预售定金支付成功
 *
 * @author xiuchen lang 22920192204222
 * @date 2021/12/16 17:42
 */
@Service
@RocketMQMessageListener(topic = "${oomall.payment.order.deposittopic}", consumerGroup = "${oomall.payment.order.deposittopic}")
public class AdvanceDepositPaySuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;
    @Autowired
    GoodsService goodsService;

    @Override
    public void onMessage(String message) {
        PaymentNotifyMessage paymentNotifyMessage = JacksonUtil.toObj(message, PaymentNotifyMessage.class);
        if(!paymentNotifyMessage.getPaymentState().equals(PaymentState.ALREADY_PAY.getCode())){
            return;
        }
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(paymentNotifyMessage.getDocumentId());
        if (orderByOrderSn.getCode() != ReturnNo.OK) {
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);
        ReturnObject orderItemListReturnObject = orderDao.listOrderItemsByOrderId(order.getId());
        if (orderItemListReturnObject.getCode() != ReturnNo.OK) {
            return;
        }
        List<OrderItemPo> orderItemPos = (List<OrderItemPo>) orderItemListReturnObject.getData();
        for (OrderItemPo orderItemPo : orderItemPos) {
            //减少库存
            goodsService.decreaseOnSale(orderItemPo.getShopId(), orderItemPo.getOnsaleId(), new QuantityVo(-orderItemPo.getQuantity()));
        }
        if (order.getAdvancesaleId() != 0) {
            //这是预售 不用拆单子
            if (order.getState() == OrderState.NEW_ORDER.getCode()) {
                //定金
                order.setState(OrderState.WAIT_PAY_REST.getCode());
            } else {
                return;
            }
            setPoModifiedFields(order, 0L, null);
            orderDao.updateOrder(order);
            return;
        }
    }
}
