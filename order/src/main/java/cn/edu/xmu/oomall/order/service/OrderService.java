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
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject insertOrder(SimpleOrderVo simpleOrderVo, Long userId, String userName) {
        // 订单不允许0个orderItem
        if (simpleOrderVo.getOrderItems().size() == 0) {
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }

        if (simpleOrderVo.getGrouponId() == null && simpleOrderVo.getAdvancesaleId() == null) {
            // 普通订单
            // 传到3-1计算钱

        } else {
            // 团购、预售只能有一个orderItem
            if (simpleOrderVo.getOrderItems().size() != 1) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }

            // 获取OrderItem的productVo，判断是否存在
            SimpleOrderItemVo simpleOrderItemVo = simpleOrderVo.getOrderItems().get(0);
            InternalReturnObject<ProductVo> retProductVo = goodsService.getProductById(simpleOrderItemVo.getProductId());
            if (retProductVo.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(retProductVo.getErrno()));
            }

            // 获取OrderItem的OnsaleVo，判断是否存在
            InternalReturnObject<OnSaleVo> retOnSaleVo = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if (retOnSaleVo.getErrno() != 0) {
                return new ReturnObject(ReturnNo.getByCode(retOnSaleVo.getErrno()));
            }

            // 判断onsaleVo的productId和productVo的Id是否对应
            if (retOnSaleVo.getData().getProduct().getId() != retProductVo.getData().getId()) {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }


            if (simpleOrderVo.getGrouponId() != null) {
                // 团购订单
                InternalReturnObject<GrouponActivityVo> grouponsById = activityService.getGrouponsById(simpleOrderVo.getGrouponId());
                if (grouponsById.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(grouponsById.getErrno()));
                }

                //团购通过onsale去算 每一件就的价钱是onsale的价钱

            } else if (simpleOrderVo.getAdvancesaleId() != null) {
                // 预售订单
                InternalReturnObject<AdvanceVo> advanceSaleById = activityService.getAdvanceSaleById(simpleOrderVo.getAdvancesaleId());
                if (advanceSaleById.getErrno() != 0) {
                    return new ReturnObject(ReturnNo.getByCode(advanceSaleById.getErrno()));
                }

                //根据预售去算钱

            }
        }

        return new ReturnObject();
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteOrderByCustomer(Long id, Long userId, String userName) {
        //TODO:调用查询买家查询自己订单  有东西证明是自己的
//      ReturnObject r = orderDao.selectById(order.getModifierId());
//      if(r.getCode()!=ReturnNo.OK)
//          return r;
        Order order = new Order();
        order.setId(id);
        order.setBeDeleted((byte) 1);
        Common.setPoModifiedFields(order, userId, userName);
        return orderDao.deleteOrder(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrderByCustomer(Long id, Long userId, String userName) {
        //TODO:调用查询买家查询自己订单  有东西证明是自己的
//        ReturnObject r = orderDao.selectById(order.getModifierId());
//        if(r.getCode()!=ReturnNo.OK)
//            return r;
        Order order = new Order();
        order.setId(id);
        order.setState(OrderState.CANCEL_ORDER.getCode());
        Common.setPoModifiedFields(order, userId, userName);
        return orderDao.cancelOrder(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmOrder(Long orderId) {
        LocalDateTime nowTime = LocalDateTime.now();
        return orderDao.confirmOrder(orderId, nowTime);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject listBriefOrdersByShopId(Long shopId, Integer pageNumber, Integer pageSize) {
        return orderDao.listBriefOrdersByShopId(shopId, pageNumber, pageSize);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateOrderComment(Long shopId, Long orderId, OrderVo orderVo, Long loginUserId, String loginUserName) {
        Order order = Common.cloneVo(orderVo, Order.class);
        order.setShopId(shopId);
        order.setId(orderId);
        Common.setPoModifiedFields(order, loginUserId, loginUserName);
        return orderDao.updateOrderComment(order);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getOrderDetail(Long shopId, Long orderId) {
        ReturnObject ret = orderDao.getOrderDetail(shopId, orderId);
        if (ret.getData() != null) {
            Order order = (Order) ret.getData();
            SimpleVo customerVo = customService.getCustomerById(order.getCustomerId()).getData();
            SimpleVo shopVo = shopService.getShopById(order.getShopId()).getData();
            DetailOrderVo orderVo = (DetailOrderVo) Common.cloneVo(order, DetailOrderVo.class);
            orderVo.setCustomerVo(customerVo);
            orderVo.setShopVo(shopVo);
            return new ReturnObject(orderVo);
        } else {
            return ret;
        }
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

}
