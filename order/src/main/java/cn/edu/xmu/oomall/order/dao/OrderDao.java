package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.bo.OrderState;
import cn.edu.xmu.oomall.order.model.po.OrderItemPo;
import cn.edu.xmu.oomall.order.model.po.OrderItemPoExample;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.model.po.OrderPoExample;
import cn.edu.xmu.oomall.order.model.vo.BriefOrderVo;
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
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;


@Repository
public class OrderDao {
    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    @Autowired
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemPoMapper orderItemPoMapper;

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
            if(customerId!=null)
            {
                cr.andCustomerIdEqualTo(customerId);
            }
            if(orderSn!=null)
            {
                cr.andOrderSnEqualTo(orderSn);
            }
            if(beginTime!=null)
            {
                cr.andGmtCreateGreaterThan(beginTime);
            }
            if(endTime!=null)
            {
                cr.andGmtCreateLessThan(endTime);
            }
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


    public ReturnObject getOrderItemByOrderId(Long orderId)
    {
        OrderItemPoExample orderItemPoExample=new OrderItemPoExample();
        OrderItemPoExample.Criteria cr=orderItemPoExample.createCriteria();
        cr.andOrderIdEqualTo(orderId);
        List<OrderItemPo> orderItemPos=orderItemPoMapper.selectByExample(orderItemPoExample);
        List<OrderItem> orderItemList=new ArrayList<>(orderItemPos.size());
        for(OrderItemPo orderItemPo:orderItemPos)
        {
            orderItemList.add((OrderItem) cloneVo(orderItemPo,OrderItem.class));
        }
        return new ReturnObject(orderItemList);
    }



}
