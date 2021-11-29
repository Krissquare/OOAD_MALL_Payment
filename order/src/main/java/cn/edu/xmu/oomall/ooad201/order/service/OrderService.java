package cn.edu.xmu.oomall.ooad201.order.service;

import cn.edu.xmu.oomall.ooad201.order.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
}
