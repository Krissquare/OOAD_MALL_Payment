package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.microservice.*;
import cn.edu.xmu.oomall.order.microservice.vo.AdvanceVo;
import cn.edu.xmu.oomall.order.microservice.vo.GrouponActivityVo;
import cn.edu.xmu.oomall.order.microservice.vo.OnSaleVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;

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
        if (simpleOrderVo.getOrderItems().size() == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }

        if (simpleOrderVo.getGrouponId() == null && simpleOrderVo.getAdvancesaleId() == null) {
            // 普通订单
            // TODO：都没有 传到3-1计算钱
        } else {
            // 团购、预售订单的orderItem必须为1
            if (simpleOrderVo.getOrderItems().size() != 1) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }

            SimpleOrderItemVo simpleOrderItemVo = simpleOrderVo.getOrderItems().get(0);
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

            // 验证完毕，开始算钱
            if (simpleOrderVo.getGrouponId() != null) {
                // 团购订单
                InternalReturnObject<GrouponActivityVo> grouponsById = activityService.getGrouponsById(simpleOrderVo.getGrouponId());
                if (grouponsById.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(grouponsById.getErrno()));
                }
                // TODO：团购通过onsale去算 每一件就的价钱是onsale的价钱

            } else if (simpleOrderVo.getAdvancesaleId() != null) {
                // 预售订单
                InternalReturnObject<AdvanceVo> advanceSaleById = activityService.getAdvanceSaleById(simpleOrderVo.getAdvancesaleId());
                if (advanceSaleById.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(advanceSaleById.getErrno()));
                }
                // TODO: 根据预售去算钱
            }
        }
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
    public ReturnObject confirmGrouponOrder(Long shopId, Long grouponActivityId, Long loginUserId, String loginUserName) {
        ReturnObject<Order> retOrder = orderDao.getOrderById(grouponActivityId);
        // 判断订单存在与否
        if (retOrder.getCode().equals(ReturnNo.OK)) {
            return retOrder;
        }
        // 判断订单是否为团购订单
        Order newOrder = retOrder.getData();
        if (newOrder.getGrouponId() == null) {
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }

        // 设置订单状态为付款成功
        newOrder.setState(OrderState.FINISH_PAY.getCode());
        Common.setPoModifiedFields(newOrder, loginUserId, loginUserName);
        ReturnObject returnObject = orderDao.updateOrder(newOrder);

//        TODO: 需根据团购规则退款
//        if (!returnObject.getCode().equals(ReturnNo.OK)) {
//            return returnObject;
//        }

        return returnObject;
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
        SimpleVo customerVo = customService.getCustomerById(order.getCustomerId()).getData();
        SimpleVo shopVo = shopService.getShopById(order.getShopId()).getData();
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
