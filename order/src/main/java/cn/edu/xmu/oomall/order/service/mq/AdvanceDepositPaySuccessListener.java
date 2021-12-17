package cn.edu.xmu.oomall.order.service.mq;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentState;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.service.mq.vo.NotifyMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * 预售定金支付成功
 * @author xiuchen lang 22920192204222
 * @date 2021/12/16 17:42
 */
@Service
@RocketMQMessageListener(topic = "${oomall.order.pay.advance.deposit}",consumerGroup = "${oomall.order.pay.advance.deposit}")
public class AdvanceDepositPaySuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;

    @Override
    public void onMessage(String message) {
        NotifyMessage notifyMessage = JacksonUtil.toObj(message, NotifyMessage.class);
        if(!notifyMessage.getState().equals(PaymentState.ALREADY_PAY.getCode())){
            return;
        }
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(notifyMessage.getDocumentId());
        if (orderByOrderSn.getCode()!= ReturnNo.OK){
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);
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
