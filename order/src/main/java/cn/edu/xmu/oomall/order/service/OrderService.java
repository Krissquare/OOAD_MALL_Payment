package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.*;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentState;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentType;
import cn.edu.xmu.oomall.order.microservice.bo.RefundState;
import cn.edu.xmu.oomall.order.microservice.bo.RefundType;
import cn.edu.xmu.oomall.order.microservice.vo.*;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    ActivityService activityService;

    @Autowired
    CouponService couponService;

    @Autowired
    FreightService freightService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    CustomService customService;

    @Autowired
    ShopService shopService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    RocketMQTemplate rocketMQTemplate;

    /**
     * 1.获得订单的所有状态
     *
     * @author Fang Zheng
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listAllOrderState() {
        HashMap<Integer, String> ret = new HashMap<>();
        for (OrderState item : OrderState.values()) {
            ret.put(item.getCode(), item.getMessage());
        }
        return new ReturnObject(ret);
    }

    /**
     * 2.买家查询名下订单 (概要)。
     *
     * @author Fang Zheng
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listCustomerBriefOrder(Long userId,
                                               String orderSn,
                                               Integer state,
                                               LocalDateTime beginTime,
                                               LocalDateTime endTime,
                                               Integer pageNumber,
                                               Integer pageSize) {
        return orderDao.listBriefOrderByUserId(userId, orderSn, state, beginTime, endTime, pageNumber, pageSize);
    }

    /**
     * 3.新建订单
     *
     * @param simpleOrderVo
     * @param userId
     * @param userName
     * @return
     * @author created by xiuchen lang
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertOrder(SimpleOrderVo simpleOrderVo, Long userId, String userName) {
        OrderAndOrderItemsVo orderAndOrderItemsVo = new OrderAndOrderItemsVo();
        List<Long> weights = new ArrayList<>();
        List<FreightCalculatingPostVo> freightCalculatingPostVos = new ArrayList<>();
        List<OrderItem> orderItemsBo = new ArrayList<>();
        Set<Long> couponIds = new HashSet<>();
        Set<Long> couponActivityIds = new HashSet<>();
        Set<Long> shopIds = new HashSet<>();
        // 订单的orderItem不能为空
        List<SimpleOrderItemVo> orderItems = simpleOrderVo.getOrderItems();
        if (orderItems.size() == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }
        //验证orderItem的所有可能会不存在的id,以及组装orderitem
        for (SimpleOrderItemVo simpleOrderItemVo : orderItems) {
            OrderItem orderItem = cloneVo(simpleOrderItemVo, OrderItem.class);
            // 判断productId是否存在
            InternalReturnObject<ProductVo> productVo = goodsService.getProductById(simpleOrderItemVo.getProductId());
            if (productVo.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(productVo.getErrno()));
            }
            weights.add(productVo.getData().getWeight());
            // 判断onsaleId是否存在
            InternalReturnObject<OnSaleVo> onSaleVo = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if (onSaleVo.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(onSaleVo.getErrno()));
            }
            // 判断回传的Product中的OnsaleId（某一时刻唯一）是否和传入的OnsaleId对应
            if (!onSaleVo.getData().getId().equals(productVo.getData().getOnSaleId())) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            //判断couponActId;
            if (simpleOrderItemVo.getCouponActId() != null) {
                //因为可能不同item用一个活动，下同理
                if (!couponActivityIds.contains(simpleOrderItemVo.getCouponActId())) {
                    couponActivityIds.add(simpleOrderItemVo.getCouponActId());
                    InternalReturnObject couponActivityById = couponService.getCouponActivityById(onSaleVo.getData().getShop().getId(), simpleOrderItemVo.getCouponId());
                    if (couponActivityById.getErrno() != 0) {
                        return new ReturnObject(ReturnNo.getByCode(couponActivityById.getErrno()));
                    }
                }
            }
            //
            if (simpleOrderItemVo.getCouponId() != null) {
                if (!couponIds.contains(simpleOrderItemVo.getCouponId())) {
                    couponIds.add(simpleOrderItemVo.getCouponId());
                    InternalReturnObject couponById = customService.isCouponExists(simpleOrderItemVo.getCouponId());
                    if (couponById.getErrno() != 0) {
                        return new ReturnObject(ReturnNo.getByCode(couponById.getErrno()));
                    }
                }
            }
            orderItem.setShopId(productVo.getData().getShop().getId());
            orderItem.setPrice(onSaleVo.getData().getPrice() * simpleOrderItemVo.getQuantity());
            //如果是优惠 就重新set
            orderItem.setDiscountPrice(0L);
            orderItem.setName(productVo.getData().getName());
            orderItem.setCommented((byte) 0);
            setPoCreatedFields(orderItem, userId, userName);
            setPoModifiedFields(orderItem, userId, userName);
            orderItemsBo.add(orderItem);
            shopIds.add(orderItem.getShopId());
        }
        //验证除了orderItem的字段
        if (simpleOrderVo.getGrouponId() != null) {
            // 团购订单
            //团购的list只能为1
            if (orderItems.size() != 1) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            InternalReturnObject<GrouponActivityVo> grouponsById = activityService.getGrouponsById(simpleOrderVo.getGrouponId());
            if (grouponsById.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(grouponsById.getErrno()));
            }
            SimpleOrderItemVo simpleOrderItemVo = orderItems.get(0);
            InternalReturnObject<OnSaleVo> onsaleById = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if (onsaleById.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(onsaleById.getErrno()));
            }
            OnSaleVo onSaleVo = onsaleById.getData();
            //判断onsaleid里的activity是不是couponid
            if (!(onSaleVo.getType() == (byte) 2 && onSaleVo.getActivityId().equals(simpleOrderVo.getGrouponId()))) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        } else if (simpleOrderVo.getAdvancesaleId() != null) {
            // 预售订单
            InternalReturnObject<AdvanceVo> advanceSaleById = activityService.getAdvanceSaleById(simpleOrderVo.getAdvancesaleId());
            if (simpleOrderVo.getOrderItems().size() != 1) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            if (advanceSaleById.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(advanceSaleById.getErrno()));
            }
            SimpleOrderItemVo simpleOrderItemVo = orderItems.get(0);
            InternalReturnObject<OnSaleVo> onsaleById = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if (onsaleById.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(onsaleById.getErrno()));
            }
            OnSaleVo onSaleVo = onsaleById.getData();
            //判断onsaleid里的activity是不是couponid
            if (!(onSaleVo.getType() == (byte) 3 && onSaleVo.getActivityId().equals(simpleOrderVo.getAdvancesaleId()))) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        } else {
            //普通或者优惠订单
            List<ProductPostVo> disList = new ArrayList<>();
            List<Integer> indexs = new ArrayList<>();
            for (int i = 0; i < orderItemsBo.size(); i++) {
                OrderItem orderItem = orderItemsBo.get(i);
                if (orderItem.getCouponActivityId() != null) {
                    //参加优惠活动的
                    ProductPostVo productPostVo = cloneVo(orderItem, ProductPostVo.class);
                    productPostVo.setActivityId(orderItem.getCouponActivityId());
                    productPostVo.setOriginalPrice(orderItem.getPrice());
                    disList.add(productPostVo);
                    indexs.add(i);
                }
            }
            if (disList.size() != 0) {
                InternalReturnObject internalReturnObject = couponService.calculateDiscoutprices(disList);
                if (internalReturnObject.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()));
                }
                //得到优惠钱封装进去
                List<ProductRetVo> calList = (List<ProductRetVo>) internalReturnObject.getData();
                for (int i = 0; i < calList.size(); i++) {
                    orderItemsBo.get(indexs.get(i)).setDiscountPrice(calList.get(i).getDiscountPrice() * 10);//1/10分
                }
            }
        }

        Order order = cloneVo(simpleOrderVo, Order.class);
        if (shopIds.size() == 1) {
            Iterator it = shopIds.iterator();
            order.setShopId((Long) it.next());
        }
        order.setCustomerId(userId);
        order.setPid(0L);
        //TODO 订单号 生成算法
        order.setState(OrderState.NEW_ORDER.getCode());
        order.setBeDeleted((byte) 0);
        setPoCreatedFields(order, userId, userName);
        setPoModifiedFields(order, userId, userName);
        Long sumOrigin = 0L;
        Long sumDiscount = 0L;
        Long sumNow = 0L;
        //分积点总积点*10分到某个里面在除以数量
        Long totalPoint = order.getPoint() * 10;
        for (int i = 0; i < orderItemsBo.size(); i++) {
            OrderItem orderItem = orderItemsBo.get(i);
            sumOrigin += orderItem.getPrice() * orderItem.getQuantity();
            sumDiscount += orderItem.getDiscountPrice() * orderItem.getQuantity();
            sumNow += sumOrigin;
            sumNow -= sumDiscount;
            //运费
            FreightCalculatingPostVo freightCalculatingPostVo = cloneVo(orderItem, FreightCalculatingPostVo.class);
            freightCalculatingPostVo.setWeight(weights.get(i));
            freightCalculatingPostVos.add(freightCalculatingPostVo);
            //减少库存量 redis
            InternalReturnObject internalReturnObject = goodsService.decreaseOnSale(orderItem.getShopId(), orderItem.getOnsaleId(), new QuantityVo(orderItem.getQuantity()));
            if (internalReturnObject.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()));
            }
        }
        //转换为1/10的单位
        sumNow *= 10;
        for (int i = 0; i < orderItemsBo.size(); i++) {
            OrderItem orderItem = orderItemsBo.get(i);
            double sum = orderItem.getQuantity() * (orderItem.getPrice() * 10 - orderItem.getDiscountPrice());
            double proportion = sum / sumNow;
            orderItem.setPoint((long) (proportion * totalPoint / orderItem.getQuantity()));
        }
        order.setOriginPrice(sumOrigin);
        order.setDiscountPrice(sumDiscount);
        //计算运费
        InternalReturnObject<FreightCalculatingRetVo> freightCalculatingRetVoInternalReturnObject = freightService.calculateFreight(order.getRegionId(), freightCalculatingPostVos);
        if (freightCalculatingRetVoInternalReturnObject.getErrno() != 0) {
            return new ReturnObject(ReturnNo.getByCode(freightCalculatingRetVoInternalReturnObject.getErrno()));
        }
        order.setExpressFee(freightCalculatingRetVoInternalReturnObject.getData().getFreightPrice());
        orderAndOrderItemsVo.setOrder(order);

        //减少积点,减少优惠卷
        InternalReturnObject internalReturnObject1 = customService.changeCustomerPoint(userId, new CustomerModifyPointsVo(-orderAndOrderItemsVo.getOrder().getPoint()));
        if (internalReturnObject1.getErrno() != 0) {
            return new ReturnObject(ReturnNo.getByCode(internalReturnObject1.getErrno()));
        }
        for (Long id : couponIds) {
            InternalReturnObject internalReturnObject = customService.useCoupon(id);
            if (internalReturnObject.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()));
            }
        }
        //todo: redis暂存 以防没插进去就支付
        //发消息
        String json = JacksonUtil.toJson(orderAndOrderItemsVo);
        Message message = MessageBuilder.withPayload(json).build();
        SendResult sendResult = rocketMQTemplate.syncSend("insert-order", message);
        if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
            sendResult = rocketMQTemplate.syncSend("insert-order", message);
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                //回滚积点,优惠卷
                internalReturnObject1 = customService.changeCustomerPoint(userId, new CustomerModifyPointsVo(orderAndOrderItemsVo.getOrder().getPoint()));
                if (internalReturnObject1.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(internalReturnObject1.getErrno()));
                }
                for (Long id : couponIds) {
                    InternalReturnObject internalReturnObject = customService.refundCoupon(id);
                    if (internalReturnObject.getErrno() != 0) {
                        return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()));
                    }
                }
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, "发送消息失败");
            }
        }
        return new ReturnObject();
    }

    /**
     * 4.买家查询订单完整信息（普通，团购，预售）
     *
     * @author Fang Zheng
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listCustomerWholeOrder(Long userId, Long orderId) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getCustomerId().equals(userId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        InternalReturnObject customerRet = customService.getCustomerById(order.getCustomerId());
        if (!customerRet.getErrno().equals(ReturnNo.OK)) {
            return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
        }
        SimpleVo customerVo = (SimpleVo) customerRet.getData();
        InternalReturnObject shopRet = shopService.getShopById(order.getShopId());
        if (!customerRet.getErrno().equals(ReturnNo.OK)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleVo shopVo = (SimpleVo) shopRet.getData();
        DetailOrderVo orderVo = Common.cloneVo(order, DetailOrderVo.class);
        orderVo.setCustomerVo(customerVo);
        orderVo.setShopVo(shopVo);
        List<OrderItem> orderItemList = (List<OrderItem>) orderDao.listOrderItemsByOrderId(orderId).getData();
        List<SimpleOrderitemRetVo> simpleOrderItemVos = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            SimpleOrderitemRetVo simpleOrderItemVo = Common.cloneVo(orderItem, SimpleOrderitemRetVo.class);
            simpleOrderItemVos.add(simpleOrderItemVo);
        }
        orderVo.setOrderItems(simpleOrderItemVos);
        return new ReturnObject(orderVo);
    }

    /**
     * 5.买家修改本人名下订单
     *
     * @author FangZheng
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateCustomerOrder(Long userId,
                                            Long orderId,
                                            UpdateOrderVo updateOrderVo) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order oldOrder = (Order) ret.getData();
        if (!oldOrder.getCustomerId().equals(userId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (oldOrder.getState() >= OrderState.SEND_GOODS.getCode()) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        Order newOrder = Common.cloneVo(updateOrderVo, Order.class);
        newOrder.setId(orderId);
        return orderDao.updateOrder(newOrder);
    }

    /**
     * 6.买家逻辑删除订单
     * created by  xiuchen lang
     *
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteOrderByCustomer(Long id, Long userId, String userName) {
        ReturnObject returnObject = orderDao.getOrderById(id);
        if (returnObject.getCode() != ReturnNo.OK) {
            return returnObject;
        }
        Order data = (Order) returnObject.getData();
        if (!Objects.equals(data.getCustomerId(), userId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (!(data.getState() == OrderState.COMPLETE_ORDER.getCode() || data.getState() == OrderState.CANCEL_ORDER.getCode())) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        Order order = new Order();
        order.setId(id);
        order.setBeDeleted((byte) 1);
        Common.setPoModifiedFields(order, userId, userName);
        return orderDao.updateOrder(order);
    }

    /**
     * 7.买家取消本人名下订单
     * create by xiuchen Lang
     *
     * @param orderId
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrderByCustomer(Long orderId, Long loginUserId, String loginUserName) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getCustomerId().equals(loginUserId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (order.getState() == OrderState.CANCEL_ORDER.getCode() || order.getState() == OrderState.COMPLETE_ORDER.getCode()) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        String documentId;
        Order pOrder = null;
        if (order.getPid() == 0) {
            documentId = order.getOrderSn();
        } else {
            ReturnObject ret1 = orderDao.getOrderById(order.getPid());
            if (!ret1.getCode().equals(ReturnNo.OK)) {
                return ret1;
            }
            pOrder = (Order) ret1.getData();
            documentId = pOrder.getOrderSn();
        }
        InternalReturnObject returnObject = transactionService.listPayment(0L, documentId, PaymentState.ALREADY_PAY.getCode(), null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<PaymentRetVo> list = (List<PaymentRetVo>) data.get("list");
        for (PaymentRetVo paymentVo : list) {
            RefundRecVo refundRecVo = cloneVo(paymentVo, RefundRecVo.class);
            refundRecVo.setPaymentId(paymentVo.getId());
            refundRecVo.setDocumentType(RefundType.ORDER.getCode());
            InternalReturnObject<RefundRetVo> retRefund = transactionService.refund(refundRecVo);
            if (retRefund.getData() == null) {
                return new ReturnObject(retRefund);
            }
        }
        order.setState(OrderState.CANCEL_ORDER.getCode());
        Common.setPoModifiedFields(order, loginUserId, loginUserName);
        if (pOrder != null) {
            pOrder.setState(OrderState.CANCEL_ORDER.getCode());
            Common.setPoModifiedFields(pOrder, loginUserId, loginUserName);
            ReturnObject ret3 = orderDao.updateOrder(pOrder);
            if (!ret3.getCode().equals(ReturnNo.OK)) {
                return ret3;
            }
            return orderDao.cancelRelatedOrder(order);
        }
        return orderDao.updateOrder(order);
    }


    /**
     * 8.买家标记确认收货
     * create by hty
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmOrder(Long orderId, Long loginUserId, String loginUserName) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getState().equals(OrderState.SEND_GOODS.getCode())) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        order.setState(OrderState.COMPLETE_ORDER.getCode());
        Common.setPoModifiedFields(order, loginUserId, loginUserName);
        return orderDao.updateOrder(order);
    }

    /**
     * 9.店家查询商户所有订单（概要）
     * create by hty
     *
     * @param shopId
     * @param customerId
     * @param orderSn
     * @param beginTime
     * @param endTime
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listBriefOrdersByShopId(Long shopId, Long customerId, String orderSn, LocalDateTime beginTime, LocalDateTime endTime, Integer pageNumber, Integer pageSize) {
        return orderDao.listBriefOrdersByShopId(shopId, customerId, orderSn, beginTime, endTime, pageNumber, pageSize);
    }

    /**
     * create by hty
     * 10.店家修改订单（留言）
     *
     * @param shopId
     * @param orderId
     * @param orderVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateOrderComment(Long shopId, Long orderId, OrderVo orderVo, Long loginUserId, String loginUserName) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getShopId().equals(shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        order.setMessage(orderVo.getMessage());
        Common.setPoModifiedFields(order, loginUserId, loginUserName);
        return orderDao.updateOrder(order);
    }

    /**
     * create by hty
     * 11.店家查询店内订单完整信息（普通，团购，预售）
     *
     * @param shopId
     * @param orderId
     * @return
     */

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getOrderDetail(Long shopId, Long orderId) {
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getShopId().equals(shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        SimpleVo customerVo = customService.getCustomerById(order.getCustomerId()).getData();
        SimpleVo shopVo = shopService.getShopById(order.getShopId()).getData();
        DetailOrderVo orderVo = Common.cloneVo(order, DetailOrderVo.class);
        orderVo.setCustomerVo(customerVo);
        orderVo.setShopVo(shopVo);
        List<OrderItem> orderItemList = (List<OrderItem>) orderDao.listOrderItemsByOrderId(orderId).getData();//根据orderId查orderItem
        List<SimpleOrderitemRetVo> simpleOrderItemVos = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            SimpleOrderitemRetVo simpleOrderItemVo = Common.cloneVo(orderItem, SimpleOrderitemRetVo.class);
            simpleOrderItemVos.add(simpleOrderItemVo);
        }
        orderVo.setOrderItems(simpleOrderItemVos);
        return new ReturnObject(orderVo);
    }

    /**
     * gyt
     * 12.管理员取消本店铺订单。
     *
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrderByShop(Long shopId, Long id, Long userId, String userName) {
        ReturnObject returnObject = orderDao.getOrderById(id);
        if (returnObject.getCode() != ReturnNo.OK) {
            return returnObject;
        }
        Order data = (Order) returnObject.getData();
        if (!Objects.equals(data.getShopId(), shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (data.getState() == OrderState.COMPLETE_ORDER.getCode() || data.getState() == OrderState.CANCEL_ORDER.getCode()) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        Order order = new Order();
        order.setId(id);
        order.setState(OrderState.CANCEL_ORDER.getCode());
        Common.setPoModifiedFields(order, userId, userName);
        return orderDao.updateOrder(order);
    }

    /**
     * gyt
     * 13.店家对订单标记发货
     *
     * @param shopId
     * @param id
     * @param markShipmentVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deliverByShop(Long shopId, Long id, MarkShipmentVo markShipmentVo, Long loginUserId, String loginUserName) {

        ReturnObject returnObject = orderDao.getOrderById(id);
        if (returnObject.getCode() != ReturnNo.OK) {
            return returnObject;
        }
        Order order = (Order) returnObject.getData();
        if (!order.getShopId().equals(shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (!order.getState().equals(OrderState.FINISH_PAY.getCode())) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        LocalDateTime nowTime = LocalDateTime.now();
        Order order1 = cloneVo(markShipmentVo, Order.class);
        order1.setId(id);
        order1.setConfirmTime(nowTime);
        order1.setState(OrderState.SEND_GOODS.getCode());
        setPoModifiedFields(order1, loginUserId, loginUserName);
        return orderDao.updateOrder(order1);
    }

    /**
     * gyt
     * 14.查询自己订单的支付信息
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentByOrderId(Long id, Long loginUserId, String loginUserName) {
        ReturnObject returnObject1 = orderDao.getOrderById(id);
        if (returnObject1.getCode() != ReturnNo.OK) {
            return returnObject1;
        }
        Order order = (Order) returnObject1.getData();
        String ducumentId = order.getOrderSn();
        InternalReturnObject returnObject = transactionService.listPayment(0L, ducumentId, PaymentState.ALREADY_PAY.getCode(), null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<PaymentRetVo> list = (List<PaymentRetVo>) data.get("list");
        return new ReturnObject(list);
    }

    /**
     * 15.确认团购订单
     * gyt
     *
     * @param shopId
     * @param id
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmGrouponOrder(Long shopId, Long id, Long loginUserId, String loginUserName) {
        ReturnObject<Order> retOrder = orderDao.getOrderById(id);
        // 判断订单存在与否
        if (!retOrder.getCode().equals(ReturnNo.OK)) {
            return retOrder;
        }
        // 判断订单是否为团购订单
        Order newOrder = retOrder.getData();
        if (newOrder.getGrouponId() == null) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (!newOrder.getShopId().equals(shopId)) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        // 设置订单状态为付款成功
        newOrder.setState(OrderState.FINISH_PAY.getCode());
        Common.setPoModifiedFields(newOrder, loginUserId, loginUserName);
        ReturnObject returnObject = orderDao.updateOrder(newOrder);
        //TODO:解析团购规则json信息，计算退款价格
//        TODO: 退款
//        if (!returnObject.getCode().equals(ReturnNo.OK)) {
//            return returnObject;
//        }

        return returnObject;
    }

    /*===============================================内部API=======================================*/

    /**
     * 1.内部API-取消订单
     * hty
     *
     * @param shopId
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject internalcancelOrderByShop(Long shopId, Long id, Long userId, String userName) {
        ReturnObject ret = orderDao.getOrderById(id);
        if (ret.getCode() != ReturnNo.OK) {
            return ret;
        }
        Order order = (Order) ret.getData();
        //判断操作的订单是否为子订单
        if (order.getPid() != 0) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        //操作的订单是父订单,接下来判断是否分单，分单shopId==null
        if (order.getShopId() == null) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        if (!order.getShopId().equals(shopId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if (order.getState() == OrderState.COMPLETE_ORDER.getCode() || order.getState() == OrderState.CANCEL_ORDER.getCode()) {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        String documentId = order.getOrderSn();
        InternalReturnObject returnObject = transactionService.listPayment(0L, documentId, PaymentState.ALREADY_PAY.getCode(), null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<PaymentRetVo> list = (List<PaymentRetVo>) data.get("list");
        for (PaymentRetVo paymentVo : list) {
            RefundRecVo refundRecVo = cloneVo(paymentVo, RefundRecVo.class);
            refundRecVo.setPaymentId(paymentVo.getId());
            refundRecVo.setDocumentType(RefundType.ORDER.getCode());
            InternalReturnObject<RefundRetVo> retRefund = transactionService.refund(refundRecVo);
            if (retRefund.getData() == null) {
                return new ReturnObject(retRefund);
            }
        }
        order.setState(OrderState.CANCEL_ORDER.getCode());
        Common.setPoModifiedFields(order, userId, userName);
        return orderDao.updateOrder(order);
    }


    /**
     * 2.内部API-管理员建立售后订单
     *
     * @param shopId
     * @param orderVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertAftersaleOrder(Long shopId, AftersaleRecVo orderVo, Long loginUserId, String loginUserName) {
        AftersaleOrderitemRecVo simpleOrderItemVo = orderVo.getOrderItem();
        // 判断productId是否存在
        InternalReturnObject<ProductVo> productVo = goodsService.getProductById(simpleOrderItemVo.getProductId());
        if (productVo.getErrno() != 0) {
            return new ReturnObject(ReturnNo.getByCode(productVo.getErrno()));
        }
        // 判断onsaleId是否存在
        InternalReturnObject<OnSaleVo> onSaleVo = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
        if (onSaleVo.getErrno() != 0) {
            return new ReturnObject(ReturnNo.getByCode(onSaleVo.getErrno()));
        }
        // 判断回传的Product中的OnsaleId（某一时刻唯一）是否和传入的OnsaleId对应
        if (!onSaleVo.getData().getId().equals(productVo.getData().getOnSaleId())) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        Order order = cloneVo(orderVo, Order.class);
        order.setShopId(shopId);
        order.setDiscountPrice(0L);
        order.setOriginPrice(0L);
        order.setPoint(0L);
        order.setPid(0L);
        order.setState(OrderState.FINISH_PAY.getCode());
        Common.setPoCreatedFields(order, loginUserId, loginUserName);
        ReturnObject ret = orderDao.createOrder(order);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order1 = (Order) ret.getData();
        OrderItem orderItem = cloneVo(simpleOrderItemVo, OrderItem.class);
        orderItem.setShopId(shopId);
        orderItem.setPrice(0L);
        orderItem.setName(productVo.getData().getName());
        orderItem.setOrderId(order1.getId());
        Common.setPoCreatedFields(orderItem, loginUserId, loginUserName);
        ReturnObject ret2 = orderDao.createOrderItem(orderItem);
        if (!ret2.getCode().equals(ReturnNo.OK)) {
            return ret2;
        }
        AftersaleRetVo aftersaleRetVo = cloneVo(order1, AftersaleRetVo.class);
        InternalReturnObject customer = customService.getCustomerById(order1.getCustomerId());
        aftersaleRetVo.setCustomer((SimpleVo) customer.getData());
        InternalReturnObject shop = shopService.getShopById(shopId);
        aftersaleRetVo.setShop((SimpleVo) shop.getData());
        SimpleOrderitemRetVo simpleOrderitemRetVo = cloneVo(orderItem, SimpleOrderitemRetVo.class);
        aftersaleRetVo.setAftersaleOrderitemVo(simpleOrderitemRetVo);
        return new ReturnObject(aftersaleRetVo);
    }

    /**
     * 3.查询自己订单的退款信息
     * hty
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listOrderRefunds(Long id) {
        ReturnObject ret = orderDao.getOrderById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order = (Order) ret.getData();
        String documentId = order.getOrderSn();
        InternalReturnObject returnObject = transactionService.listRefund(0L, documentId, RefundState.FINISH_REFUND.getCode(), null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<RefundRetVo> list = (List<RefundRetVo>) data.get("list");
        return new ReturnObject(list);
    }

    /**
     * 4.根据Itemid找item(加customerid)
     *
     * @param id
     * @param customerId
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getOrderItemById(Long id, Long customerId) {
        ReturnObject ret = orderDao.getOrderItemById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        OrderItem orderItem = (OrderItem) ret.getData();
        OrderItemRetVo retVo = cloneVo(orderItem, OrderItemRetVo.class);
        ReturnObject ret1 = orderDao.getOrderById(orderItem.getOrderId());
        if (!ret1.getCode().equals(ReturnNo.OK)) {
            return ret1;
        }
        Order order = (Order) ret1.getData();
        if (customerId != null) {
            if (!customerId.equals(order.getCustomerId())) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
        }
        retVo.setCustomerId(order.getCustomerId());
        return new ReturnObject(retVo);
    }

    /**
     * 5.根据itemid找Payment(如果为预售只返回尾款的Payment)
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentByOrderitem(Long id) {
        ReturnObject ret = orderDao.getOrderItemById(id);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        OrderItem orderItem = (OrderItem) ret.getData();
        ReturnObject ret1 = orderDao.getOrderById(orderItem.getOrderId());
        if (!ret1.getCode().equals(ReturnNo.OK)) {
            return ret1;
        }
        Order order = (Order) ret1.getData();
        String documentId = order.getOrderSn();
        InternalReturnObject returnObject = transactionService.listPayment(0L, documentId, PaymentState.ALREADY_PAY.getCode(), null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<PaymentRetVo> list = (List<PaymentRetVo>) data.get("list");
        PaymentRetVo retPayment = null;
        for (PaymentRetVo paymentRetVo : list) {
            if (paymentRetVo.getDocumentType().equals(PaymentType.ORDER_ADVANCE.getCode())) {
                continue;
            }
            retPayment = paymentRetVo;
        }
        return new ReturnObject(retPayment);
    }

    /**
     * 6.orderId查item
     * gyt
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public InternalReturnObject listOrderItemsByOrderId(Long id) {
        return orderDao.listOrderItemsByPOrderId(id);
    }

    /**
     * 7. orderSn查orderId
     * hty
     * @param orderSn
     * @return
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public InternalReturnObject getOrderId(String orderSn)
    {
        ReturnObject ret=orderDao.getOrderByOrderSn(orderSn);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return new InternalReturnObject(ret);
        }
        OrderIdRetVo orderIdRetVo=new OrderIdRetVo();
        Order order=(Order) ret.getData();
        orderIdRetVo.setId(order.getId());
        return new InternalReturnObject(orderIdRetVo);
    }


}
