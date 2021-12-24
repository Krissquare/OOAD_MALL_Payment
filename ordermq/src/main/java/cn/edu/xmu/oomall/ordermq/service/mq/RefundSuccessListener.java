package cn.edu.xmu.oomall.ordermq.service.mq;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ordermq.dao.OrderDao;
import cn.edu.xmu.oomall.ordermq.microservice.InternalCustomService;
import cn.edu.xmu.oomall.ordermq.microservice.InternalGoodsService;
import cn.edu.xmu.oomall.ordermq.microservice.vo.CustomerModifyPointsVo;
import cn.edu.xmu.oomall.ordermq.microservice.vo.IntegerQuantityVo;
import cn.edu.xmu.oomall.ordermq.model.bo.Order;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderItem;
import cn.edu.xmu.oomall.ordermq.model.po.OrderPo;
import cn.edu.xmu.oomall.ordermq.service.mq.bo.RefundState;
import cn.edu.xmu.oomall.ordermq.service.mq.vo.RefundNotifyMessage;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/14 9:25
 */
@Service
@RocketMQMessageListener(consumerGroup = "${oomall.refund.order}", topic = "${oomall.refund.order}")
public class RefundSuccessListener implements RocketMQListener<String> {
    @Autowired
    OrderDao orderDao;

    @Autowired
    InternalGoodsService internalGoodsService;

    @Autowired
    InternalCustomService internalCustomService;

    @Override
    public void onMessage(String message) {
        Set<Long> couponIds = new HashSet<>();
        RefundNotifyMessage refundNotifyMessage = JSONObject.parseObject(message, RefundNotifyMessage.class);
        if (!refundNotifyMessage.getRefundState().equals(RefundState.FINISH_REFUND)) {
            return;
        }
        ReturnObject orderByOrderSn = orderDao.getOrderByOrderSn(refundNotifyMessage.getDocumentId());
        if (orderByOrderSn.getCode() != ReturnNo.OK) {
            return;
        }
        OrderPo orderPo = (OrderPo) orderByOrderSn.getData();
        Order order = cloneVo(orderPo, Order.class);

        ReturnObject orderItemListReturnObject = orderDao.listOrderItemsByOrderId(order.getId());
        if (orderItemListReturnObject.getCode() != ReturnNo.OK) {
            return;
        }
        List<OrderItem> orderItems = (List<OrderItem>) orderItemListReturnObject.getData();
        for (OrderItem orderItem : orderItems) {
            //增加库存
            internalGoodsService.updateOnsaleQuantity(orderItem.getOnsaleId(), new IntegerQuantityVo(Math.toIntExact(orderItem.getQuantity())));
            //优惠券
            if (orderItem.getCouponId() != null) {
                couponIds.add(orderItem.getCouponId());
            }
        }
        //退优惠卷
        for (Long id : couponIds) {
            InternalReturnObject internalReturnObject = internalCustomService.refundCoupon(id);
            if (internalReturnObject.getErrno() != 0) {
                return;
            }
        }
        //回滚积点
        if (order.getPoint()!=null&&order.getPoint()!=0){
            InternalReturnObject internalReturnObject = internalCustomService.changeCustomerPoint(order.getCustomerId(), new CustomerModifyPointsVo(order.getPoint()));
            if (internalReturnObject.getErrno() != 0) {
                return;
            }
        }
        //因为退款成不成功与订单无关所以不处理
        return;
    }
}
