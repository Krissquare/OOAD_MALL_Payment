package cn.edu.xmu.oomall.ooad201.order.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.ooad201.order.model.vo.SimpleOrderVo;
import cn.edu.xmu.oomall.ooad201.order.service.OrderService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Audit(departName = "order")
    @PostMapping("/orders")
    public Object addOrder(@RequestBody @Valid SimpleOrderVo simpleOrderVo,
                           BindingResult bindingResult,
                           @LoginUser Long userId,
                           @LoginName String userName) {
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (o != null) {
            return o;
        }
        if (simpleOrderVo.getAdvancesaleId()!=null&&simpleOrderVo.getGrouponId()!=null){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        return orderService.addOrder(simpleOrderVo,userId,userName);
    }

    /**
     * 用户逻辑删除自己订单，需判断是不是自己的
     *  create by xiuchen Lang
     * @param id       订单id
     * @param userId   顾客id
     * @param username 顾客名称
     * @return
     */
    @Audit(departName = "order")
    @DeleteMapping("/orders/{id}")
    public Object deleteOrderByCustomer(@PathVariable("id") Long id, @LoginUser Long userId, @LoginName String username) {
        return Common.decorateReturnObject(orderService.deleteOrderByCustomer(id, userId, username));
    }

    /**
     * 用户逻辑取消自己订单，需判断是不是自己的
     * create by xiuchen Lang
     * @param id
     * @param userId
     * @param username
     * @return
     */
    @Audit(departName = "order")
    @PutMapping("/orders/{id}/cancel")
    public Object cancleOrderByCunstomer(@PathVariable("id") Long id, @LoginUser Long userId, @LoginName String username) {
        return Common.decorateReturnObject(orderService.cancelOrderByCustomer(id, userId, username));
    }

}
