package cn.edu.xmu.oomall.ooad201.order.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.order.dao.OrderDao;
import cn.edu.xmu.oomall.ooad201.order.microService.ActivityService;
import cn.edu.xmu.oomall.ooad201.order.microService.CouponService;
import cn.edu.xmu.oomall.ooad201.order.microService.FreightService;
import cn.edu.xmu.oomall.ooad201.order.microService.GoodsService;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.AdvanceVo;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.GrouponActivityVo;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.OnSaleVo;
import cn.edu.xmu.oomall.ooad201.order.microService.vo.ProductVo;
import cn.edu.xmu.oomall.ooad201.order.model.bo.Order;
import cn.edu.xmu.oomall.ooad201.order.model.bo.OrderState;
import cn.edu.xmu.oomall.ooad201.order.model.vo.SimpleOrderItemVo;
import cn.edu.xmu.oomall.ooad201.order.model.vo.SimpleOrderVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addOrder(SimpleOrderVo simpleOrderVo,Long userId,String userName){
        if(simpleOrderVo.getGrouponId()!=null){
            ReturnObject<GrouponActivityVo> grouponsById = activityService.getGrouponsById(simpleOrderVo.getGrouponId());
            if(grouponsById.getCode()!= ReturnNo.OK){
                return grouponsById;
            }
            SimpleOrderItemVo simpleOrderItemVo = simpleOrderVo.getOrderItems().get(0);
            ReturnObject<ProductVo> productById = goodsService.getProductById(simpleOrderItemVo.getProductId());
            if(productById.getCode()!=ReturnNo.OK){
                return productById;
            }
            InternalReturnObject<OnSaleVo> onsaleById = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if(onsaleById.getData()==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            //团购通过onsale去算 每一件就的价钱是onsale的价钱
            Long price = onsaleById.getData().getPrice();


        }
        else if(simpleOrderVo.getAdvancesaleId()!=null){
            ReturnObject<AdvanceVo> advanceSaleById = activityService.getAdvanceSaleById(simpleOrderVo.getAdvancesaleId());
            if(advanceSaleById.getCode()!= ReturnNo.OK){
                return advanceSaleById;
            }
            SimpleOrderItemVo simpleOrderItemVo = simpleOrderVo.getOrderItems().get(0);
            ReturnObject<ProductVo> productById = goodsService.getProductById(simpleOrderItemVo.getProductId());
            if(productById.getCode()!=ReturnNo.OK){
                return productById;
            }
            InternalReturnObject<OnSaleVo> onsaleById = goodsService.getOnsaleById(simpleOrderItemVo.getOnsaleId());
            if(onsaleById.getData()==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            //根据预售去算钱

        }
        else {
            //都没有 传到3-1计算钱
        }
        return new ReturnObject();
    }
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteOrderByCustomer(Long id,Long userId,String userName){
        //TODO:调用查询买家查询自己订单  有东西证明是自己的
//      ReturnObject r = orderDao.selectById(order.getModifierId());
//      if(r.getCode()!=ReturnNo.OK)
//          return r;
        Order order = new Order();
        order.setId(id);
        order.setBeDeleted((byte)1);
        Common.setPoModifiedFields(order,userId,userName);
        return orderDao.deleteByCustomer(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrderByCustomer(Long id,Long userId,String userName){
        //TODO:调用查询买家查询自己订单  有东西证明是自己的
//        ReturnObject r = orderDao.selectById(order.getModifierId());
//        if(r.getCode()!=ReturnNo.OK)
//            return r;
        Order order = new Order();
        order.setId(id);
        order.setState(OrderState.CANCEL_ORDER.getCode());
        Common.setPoModifiedFields(order,userId,userName);
        return orderDao.cancelOrderByCustomer(order);
    }
}
