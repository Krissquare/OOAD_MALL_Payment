package cn.edu.xmu.oomall.ooad201.order.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.ooad201.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.ooad201.order.model.bo.Order;
import cn.edu.xmu.oomall.ooad201.order.model.bo.OrderState;
import cn.edu.xmu.oomall.ooad201.order.model.po.OrderPo;
import cn.edu.xmu.oomall.ooad201.order.model.po.OrderPoExample;
import cn.edu.xmu.oomall.ooad201.order.model.vo.BriefOrderVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class OrderDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Autowired
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    final static private List<Integer> CANCEL_COMPLETE_LIST = Arrays.asList(OrderState.CANCEL_ORDER.getCode(), OrderState.COMPLETE_ORDER.getCode());

    public ReturnObject deleteByCustomer(Order order) {
        try {
            OrderPo orderPo = cloneVo(order, OrderPo.class);
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria criteria = orderPoExample.createCriteria();
            criteria.andIdEqualTo(orderPo.getId())
                    .andCustomerIdEqualTo(orderPo.getModifierId())
                    .andBeDeletedIsNull()
                    .andStateIn(CANCEL_COMPLETE_LIST);
            int ret = orderPoMapper.updateByExampleSelective(orderPo, orderPoExample);
            if (ret == 1) {
                return new ReturnObject();
            }
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject cancelOrderByCustomer(Order order) {
        try {
            OrderPo orderPo = cloneVo(order, OrderPo.class);
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria criteria = orderPoExample.createCriteria();
            criteria.andIdEqualTo(orderPo.getId())
                    .andCustomerIdEqualTo(orderPo.getModifierId())
                    .andStateNotIn(CANCEL_COMPLETE_LIST);
            int ret = orderPoMapper.updateByExampleSelective(orderPo, orderPoExample);
            if (ret == 1) {
                return new ReturnObject();
            }
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject confirmOrder(Long orderId, LocalDateTime nowTime) {
        try {
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(orderId);
            if (orderPo.getState() == OrderState.SEND_GOODS.getCode()) {
                orderPo.setState(OrderState.COMPLETE_ORDER.getCode());
                orderPo.setConfirmTime(nowTime);
                return new ReturnObject<>(ReturnNo.OK);
            } else {
                return new ReturnObject(ReturnNo.STATENOTALLOW, "当前货品状态不支持进行该操作");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject searchBriefOrderByShopId(Long shopId, Integer pageNumber, Integer pageSize) {
        try {
            PageHelper.startPage(pageNumber, pageSize, true, true, true);
            OrderPoExample orderPoExample = new OrderPoExample();
            OrderPoExample.Criteria cr = orderPoExample.createCriteria();
            cr.andShopIdEqualTo(shopId);
            List<OrderPo> orderPoList = orderPoMapper.selectByExample(orderPoExample);
            if (orderPoList.size() == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ReturnObject<PageInfo<Object>> ret = new ReturnObject(new PageInfo<OrderPo>(orderPoList));
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
            Common.setPoModifiedFields(orderPo, order.getModifierId(), order.getModifierName());
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
            Order order = (Order) Common.cloneVo(orderPo, Order.class);
            return new ReturnObject(order);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
