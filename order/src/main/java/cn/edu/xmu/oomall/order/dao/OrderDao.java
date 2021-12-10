package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.model.po.OrderPoExample;
import cn.edu.xmu.oomall.order.model.vo.BriefOrderVo;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Value("${oomall.order.expiretime}")
    private long orderExpireTime;

    @Autowired
    RedisUtil redisUtil;

    final static private String ORDER_KEY="order_%d";

    public ReturnObject getOrderById(Long id) {
        try {
            OrderPo po = orderPoMapper.selectByPrimaryKey(id);
            if (po == null||po.getBeDeleted()==1) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            Order order = cloneVo(po, Order.class);
            redisUtil.set(String.format(ORDER_KEY, id),order,orderExpireTime);
            return new ReturnObject<>(order);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


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

    public ReturnObject listBriefOrdersByShopId(Long shopId, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize);
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria cr = orderPoExample.createCriteria();
            cr.andShopIdEqualTo(shopId);
            List<OrderPo> orderPoList = orderPoMapper.selectByExample(orderPoExample);
            if (orderPoList.size() == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo(orderPoList));
            return Common.getPageRetVo(ret, BriefOrderVo.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject updateOrderComment(Order order) {
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(order.getId());
            if (orderPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if (!orderPo.getShopId().equals(order.getShopId())) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            orderPo.setMessage(order.getMessage());
            setPoModifiedFields(orderPo, order.getModifierId(), order.getModifierName());
            orderPoMapper.updateByPrimaryKeySelective(orderPo);
            return new ReturnObject<>(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getOrderDetail(Long shopId, Long orderId) {
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
            if (orderPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if (!orderPo.getShopId().equals(shopId)) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            Order order = (Order) cloneVo(orderPo, Order.class);
            return new ReturnObject(order);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * a-1
     * @Auther Fang Zheng
     * */
    public ReturnObject listBriefOrderByUserId(Long userId,
                                               String orderSn,
                                               Integer state,
                                               LocalDateTime beginTime,
                                               LocalDateTime endTime,
                                               Integer pageNumber,
                                               Integer pageSize){
        try{
            if (pageNumber!=null && pageSize!=null) {
                PageHelper.startPage(pageNumber, pageSize, true, true, true);
            }
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria cr = orderPoExample.createCriteria();
            cr.andCustomerIdEqualTo(userId);
            cr.andBeDeletedNotEqualTo((byte) 1);
            if (orderSn != null) {
                cr.andOrderSnEqualTo(orderSn);
            }
            if (state != null){
                cr.andStateEqualTo(state);
            }
            if (beginTime != null) {
                cr.andConfirmTimeGreaterThanOrEqualTo(beginTime);
            }
            if (endTime != null){
                cr.andConfirmTimeLessThanOrEqualTo(endTime);
            }
            List<OrderPo> orderPoList = orderPoMapper.selectByExample(orderPoExample);
            if (orderPoList.size() == 0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo(orderPoList));
            return Common.getPageRetVo(ret, BriefOrderVo.class);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
