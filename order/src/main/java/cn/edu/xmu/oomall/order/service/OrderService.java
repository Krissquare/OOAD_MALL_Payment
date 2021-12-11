package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.*;
import cn.edu.xmu.oomall.order.microservice.vo.*;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

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

    /**
     * 新建订单
     * @param simpleOrderVo
     * @param userId
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertOrder(SimpleOrderVo simpleOrderVo, Long userId, String userName) {
        // 订单的orderItem不能为空
        List<SimpleOrderItemVo> orderItems = simpleOrderVo.getOrderItems();
        if (orderItems.size() == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }
        //验证orderItem的所有可能会不存在的id
        for(SimpleOrderItemVo simpleOrderItemVo:orderItems){
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
            if (onSaleVo.getData().getId().equals(productVo.getData().getOnSaleId())) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            //判断couponActId;
            if(simpleOrderItemVo.getCouponActId()!=null){
                InternalReturnObject couponActivityById = couponService.getCouponActivityById(onSaleVo.getData().getShop().getId(), simpleOrderItemVo.getCouponId());
                if(couponActivityById.getErrno()!=0){
                    return new ReturnObject(ReturnNo.getByCode(couponActivityById.getErrno()));
                }
            }
            //
            if(simpleOrderItemVo.getCouponId()!=null){
                InternalReturnObject couponById = customService.getCouponById(simpleOrderItemVo.getCouponId());
                if(couponById.getErrno()!=0){
                    return new ReturnObject(ReturnNo.getByCode(couponById.getErrno()));
                }
            }
        }
        //验证除了orderItem的字段
        if (simpleOrderVo.getGrouponId() != null) {
            // 团购订单
            //团购的list只能为1
            if(orderItems.size()!=1){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            InternalReturnObject<GrouponActivityVo> grouponsById = activityService.getGrouponsById(simpleOrderVo.getGrouponId());
            if (grouponsById.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(grouponsById.getErrno()));
            }
            SimpleOrderItemVo simpleOrderItemVo = orderItems.get(0);
            OnSaleVo onSaleVo = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId()).getData();
            //上面也判断过所以肯定存在
            //判断onsaleid里的activity是不是couponid
            if(!(onSaleVo.getType()==(byte)2&& onSaleVo.getActivityId().equals(simpleOrderVo.getGrouponId()))){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            //得到价格
            Long price = onSaleVo.getPrice();//单价*数量=总价

        } else if(simpleOrderVo.getAdvancesaleId()!=null){
            // 预售订单
            InternalReturnObject<AdvanceVo> advanceSaleById = activityService.getAdvanceSaleById(simpleOrderVo.getAdvancesaleId());
            if (simpleOrderVo.getOrderItems().size() != 1) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            if (advanceSaleById.getErrno()!=0){
                return new ReturnObject(ReturnNo.getByCode(advanceSaleById.getErrno()));
            }
            //钱
            AdvanceVo data = advanceSaleById.getData();//单价*数量
            Long price = data.getPrice();
            Long advancePayPrice = data.getAdvancePayPrice();

        }else {
            //普通或者优惠订单
            List<ProductPostVo> commonList = new ArrayList<>();
            List<ProductPostVo> disList = new ArrayList<>();
            for (SimpleOrderItemVo simpleOrderItemVo:simpleOrderVo.getOrderItems()){
                OnSaleVo data = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId()).getData();
                //价格*数量
                Long price = data.getPrice();
                ProductPostVo productPostVo = new ProductPostVo(simpleOrderItemVo.getProductId(), simpleOrderItemVo.getOnsaleId(), simpleOrderItemVo.getQuantity()
                        , simpleOrderItemVo.getCouponActId(), price);
                if (productPostVo.getActivityId()!=null){
                    disList.add(productPostVo);
                }else {
                    commonList.add(productPostVo);
                }
            }
            if (disList.size()!=0){
                InternalReturnObject internalReturnObject = couponService.calculateDiscoutprices(disList);
                if (internalReturnObject.getErrno()!=0){
                    return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()));
                }
                //得到优惠钱
                ProductRetVo productRetVo = (ProductRetVo) internalReturnObject.getData();
                //TODO:计算普通的钱
            }

        }
        //TODO：组装vo
        return new ReturnObject();
    }

    /**
     * 买家逻辑删除订单
     * created by  xiuchen lang
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
     * 买家取消订单
     * create by xiuchen Lang
     * @param id
     * @param userId
     * @param userName
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrderByCustomer(Long id, Long userId, String userName) {
        ReturnObject returnObject = orderDao.getOrderById(id);
        if (returnObject.getCode() != ReturnNo.OK) {
            return returnObject;
        }
        Order data = (Order) returnObject.getData();
        if (!Objects.equals(data.getCustomerId(), userId)) {
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
     * 买家取消订单
     * create by hty
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmOrder(Long orderId,Long loginUserId,String loginUserName) {
        ReturnObject ret=orderDao.getOrderById(orderId);
        if(!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        Order order=(Order) ret.getData();
        if(!order.getState().equals(OrderState.SEND_GOODS.getCode()))
        {
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        order.setState(OrderState.COMPLETE_ORDER.getCode());
        Common.setPoModifiedFields(order,loginUserId,loginUserName);
        return orderDao.updateOrder(order);
    }

    /**
     * create by hty
     * 店家获取订单概要
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
    public ReturnObject listBriefOrdersByShopId(Long shopId,Long customerId,String orderSn,LocalDateTime beginTime,LocalDateTime endTime, Integer pageNumber, Integer pageSize) {
        return orderDao.listBriefOrdersByShopId(shopId,customerId,orderSn,beginTime,endTime, pageNumber, pageSize);
    }

    /**
     * create by hty
     * 店家修改订单留言
     * @param shopId
     * @param orderId
     * @param orderVo
     * @param loginUserId
     * @param loginUserName
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateOrderComment(Long shopId, Long orderId, OrderVo orderVo, Long loginUserId, String loginUserName) {
        ReturnObject ret=orderDao.getOrderById(orderId);
        if(!ret.getCode().equals(ReturnNo.OK))
        {
            return ret;
        }
        Order order = (Order) ret.getData();
        if(!order.getShopId().equals(shopId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        order.setMessage(orderVo.getMessage());
        Common.setPoModifiedFields(order, loginUserId, loginUserName);
        return orderDao.updateOrder(order);
    }

    /**
     * create by hty
     * 店家获取订单详情
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
        List<SimpleOrderItemVo> simpleOrderItemVos = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            SimpleOrderItemVo simpleOrderItemVo = Common.cloneVo(orderItem, SimpleOrderItemVo.class);
            simpleOrderItemVos.add(simpleOrderItemVo);
        }
        orderVo.setOrderItems(simpleOrderItemVos);
        return new ReturnObject(orderVo);
    }

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
        if(!newOrder.getShopId().equals(shopId)){
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
    /**
     * gyt
     * 管理员取消本店铺订单。（a-4）
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
     * 店家对订单标记发货。
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
        LocalDateTime nowTime=LocalDateTime.now();
        Order order1 = cloneVo(markShipmentVo, Order.class);
        order1.setId(id);
        order1.setConfirmTime(nowTime);
        order1.setState(OrderState.SEND_GOODS.getCode());
        setPoModifiedFields(order1, loginUserId, loginUserName);
        return orderDao.updateOrder(order1);
    }
    /**
     * gyt
     * 查询自己订单的支付信息（a-4）
     * @param id
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentByOrderId(Long id,Long loginUserId,String loginUserName) {
        ReturnObject returnObject1 = orderDao.getOrderById(id);
        if (returnObject1.getCode() != ReturnNo.OK) {
            return returnObject1;
        }
        Order order = (Order) returnObject1.getData();
        String ducumentId = order.getOrderSn();
        ReturnObject returnObject = transactionService.listPayment(0L, ducumentId, null, null, null, 1, 10);
        Map<String, Object> data = (Map<String, Object>) returnObject.getData();
        List<PaymentRetVo> list = (List<PaymentRetVo>) data.get("list");
        return new ReturnObject(list);
    }

    /**
     * a-1
     * @author Fang Zheng
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listAllOrderState(){
        HashMap<Integer, String> ret = new HashMap<>();
        for (OrderState item: OrderState.values()){
            ret.put(item.getCode(), item.getMessage());
        }
        return new ReturnObject(ret);
    }

    /**
     * a-1
     * @author Fang Zheng
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listCustomerBriefOrder(Long userId,
                                               String orderSn,
                                               Integer state,
                                               LocalDateTime beginTime,
                                               LocalDateTime endTime,
                                               Integer pageNumber,
                                               Integer pageSize){
        return orderDao.listBriefOrderByUserId(userId,orderSn,state,beginTime,endTime,pageNumber,pageSize);
    }

    /**
     * a-1
     * @author Fang Zheng
     * */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listCustomerWholeOrder(Long userId, Long orderId){
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        Order order = (Order) ret.getData();
        if (!order.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        InternalReturnObject customerRet = customService.getCustomerById(order.getCustomerId());
        if (!customerRet.getErrno().equals(ReturnNo.OK)){
            return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
        }
        SimpleVo customerVo = (SimpleVo) customerRet.getData();
        InternalReturnObject shopRet = shopService.getShopById(order.getShopId());
        if (!customerRet.getErrno().equals(ReturnNo.OK)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        SimpleVo shopVo = (SimpleVo) shopRet.getData();
        DetailOrderVo orderVo = Common.cloneVo(order, DetailOrderVo.class);
        orderVo.setCustomerVo(customerVo);
        orderVo.setShopVo(shopVo);
        List<OrderItem> orderItemList = (List<OrderItem>) orderDao.listOrderItemsByOrderId(orderId).getData();
        List<SimpleOrderItemVo> simpleOrderItemVos = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            SimpleOrderItemVo simpleOrderItemVo = Common.cloneVo(orderItem, SimpleOrderItemVo.class);
            simpleOrderItemVos.add(simpleOrderItemVo);
        }
        orderVo.setOrderItems(simpleOrderItemVos);
        return new ReturnObject(orderVo);
    }

    /**
     * a-1
     * @author FangZheng
     * */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateCustomerOrder(Long userId,
                                            Long orderId,
                                            UpdateOrderVo updateOrderVo){
        ReturnObject ret = orderDao.getOrderById(orderId);
        if (!ret.getCode().equals(ReturnNo.OK)){
            return ret;
        }
        Order oldOrder = (Order) ret.getData();
        if (!oldOrder.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        Order newOrder = Common.cloneVo(updateOrderVo, Order.class);
        newOrder.setId(orderId);
        return orderDao.updateOrder(newOrder);
    }

}
