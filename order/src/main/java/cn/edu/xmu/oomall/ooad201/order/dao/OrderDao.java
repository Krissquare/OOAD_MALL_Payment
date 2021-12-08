package cn.edu.xmu.oomall.ooad201.order.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.ooad201.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.ooad201.order.model.bo.Order;
import cn.edu.xmu.oomall.ooad201.order.model.bo.OrderState;
import cn.edu.xmu.oomall.ooad201.order.model.po.OrderPo;
import cn.edu.xmu.oomall.ooad201.order.model.po.OrderPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    final static private List<Integer> CANCEL_COMPLETE_LIST= Arrays.asList(OrderState.CANCEL_ORDER.getCode(),OrderState.COMPLETE_ORDER.getCode());

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
}
