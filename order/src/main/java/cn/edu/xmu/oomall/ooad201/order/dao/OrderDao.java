package cn.edu.xmu.oomall.ooad201.order.dao;

import cn.edu.xmu.oomall.ooad201.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.ooad201.order.mapper.OrderPoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {
    @Autowired
    OrderPoMapper orderPoMapper;
    @Autowired
    OrderItemPoMapper orderItemPoMapper;
}
