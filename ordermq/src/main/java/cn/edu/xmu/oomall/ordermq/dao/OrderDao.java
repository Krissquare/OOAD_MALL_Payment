package cn.edu.xmu.oomall.ordermq.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ordermq.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.ordermq.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.ordermq.model.bo.Order;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderItem;
import cn.edu.xmu.oomall.ordermq.model.bo.OrderState;
import cn.edu.xmu.oomall.ordermq.model.po.OrderItemPo;
import cn.edu.xmu.oomall.ordermq.model.po.OrderItemPoExample;
import cn.edu.xmu.oomall.ordermq.model.po.OrderPo;
import cn.edu.xmu.oomall.ordermq.model.po.OrderPoExample;
import cn.edu.xmu.oomall.ordermq.model.vo.OrderItemRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;


@Repository
public class OrderDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    OrderPoMapper orderPoMapper;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    OrderItemPoMapper orderItemPoMapper;

    @Autowired
    RedisUtil redisUtil;

    final static private String ORDER_KEY = "order_%d";

    public ReturnObject updateOrder(Order order) {
        try {
            OrderPo orderPo = cloneVo(order, OrderPo.class);
            int flag = orderPoMapper.updateByPrimaryKeySelective(orderPo);
            if (flag == 0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                redisUtil.del(String.format(ORDER_KEY,order.getId()));
                return new ReturnObject<>(ReturnNo.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject listOrderItemsByOrderId(Long orderId) {
        try {
            OrderItemPoExample orderItemPoExample = new OrderItemPoExample();
            OrderItemPoExample.Criteria cr = orderItemPoExample.createCriteria();
            cr.andOrderIdEqualTo(orderId);
            List<OrderItemPo> orderItemPos = orderItemPoMapper.selectByExample(orderItemPoExample);
            if (orderItemPos.size() == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            List<OrderItem> orderItemList = new ArrayList<>(orderItemPos.size());
            for (OrderItemPo orderItemPo : orderItemPos) {
                orderItemList.add(cloneVo(orderItemPo, OrderItem.class));
            }
            return new ReturnObject(orderItemList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject listOrderItemsByPOrderId(Long id){
        try{
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(id);
            List<OrderPo> list = orderPoMapper.selectByExample(example);
            List<OrderItemPo>orderItemPos=new ArrayList<>();
            //没有子订单
            if (list.size()==0) {
                OrderItemPoExample orderItemPoExample=new OrderItemPoExample();
                OrderItemPoExample.Criteria orderItemPoExampleCriteria=orderItemPoExample.createCriteria();
                orderItemPoExampleCriteria.andOrderIdEqualTo(id);
                orderItemPos=orderItemPoMapper.selectByExample(orderItemPoExample);
            }
            //有子订单
            else{
                for(OrderPo orderPo:list){
                    OrderItemPoExample orderItemPoExample=new OrderItemPoExample();
                    OrderItemPoExample.Criteria orderItemPoExampleCriteria=orderItemPoExample.createCriteria();
                    orderItemPoExampleCriteria.andOrderIdEqualTo(orderPo.getId());
                    orderItemPos.addAll(orderItemPoMapper.selectByExample(orderItemPoExample));
                }
            }
            List<OrderItemRetVo>list1=new ArrayList();
            for(OrderItemPo orderItemPo:orderItemPos){
                list1.add(cloneVo(orderItemPo,OrderItemRetVo.class));
            }
            return new ReturnObject(list1);}
        catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    public ReturnObject insertOrder(Order order){
        try {
            OrderPo orderPo = cloneVo(order, OrderPo.class);
            orderPoMapper.insert(orderPo);
            return new ReturnObject(orderPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject insertOrderItem(OrderItem orderItem){
        try {
            OrderItemPo orderItemPo = cloneVo(orderItem, OrderItemPo.class);
            orderItemPoMapper.insert(orderItemPo);
            return new ReturnObject(orderItemPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getOrderByOrderSn(String orderSn){
        try {
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria criteria = orderPoExample.createCriteria();
            criteria.andOrderSnEqualTo(orderSn);
            List<OrderPo> orderPos = orderPoMapper.selectByExample(orderPoExample);
            if (orderPos.size()==0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(orderPos.get(0));
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    //传入子订单
    public ReturnObject updateRelatedSonOrder(Order order) {
        try {
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria cr = orderPoExample.createCriteria();
            cr.andPidEqualTo(order.getPid());
            List<OrderPo> sonOrderList = orderPoMapper.selectByExample(orderPoExample);
            for (OrderPo orderPo : sonOrderList) {
                orderPo.setState(OrderState.CANCEL_ORDER.getCode());
                setPoModifiedFields(orderPo, orderPo.getCreatorId(), orderPo.getCreatorName());
                updateOrder(cloneVo(orderPo, Order.class));
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
