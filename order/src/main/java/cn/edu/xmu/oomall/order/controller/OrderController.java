package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.vo.OrderVo;
import cn.edu.xmu.oomall.order.model.vo.SimpleOrderVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;

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
        Object object = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        if (simpleOrderVo.getAdvancesaleId() != null && simpleOrderVo.getGrouponId() != null) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        return orderService.addOrder(simpleOrderVo, userId, userName);
    }

    /**
     * 用户逻辑删除自己订单，需判断是不是自己的
     * create by xiuchen Lang
     *
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
     *
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

    @ApiOperation(value = "买家标记确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录账号(id)", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录名", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")
    })
    @PutMapping("orders/{id}/confirm")
    @Audit(departName = "order")
    public Object confirmOrder(@PathVariable("id") Long id) {
        return Common.decorateReturnObject(orderService.confirmOrder(id));
    }

    @ApiOperation(value = "店家查询商户所有订单（概要）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录账号(id)", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录名", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")
    })
    @GetMapping("shops/{shopId}/orders")
    @Audit(departName = "order")
    public Object searchBriefOrder(@PathVariable("shopId") Long shopId, @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return Common.decorateReturnObject(orderService.searchBriefOrderByShopId(shopId, page, pageSize));
    }

    @ApiOperation(value = "店家修改订单（留言）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录账号(id)", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "loginUser", value = "用户登录名", required = true, dataType = "String", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "资源不存在"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")
    })
    @PutMapping("shops/{shopId}/orders/{id}")
    @Audit(departName = "order")
    public Object updateOrder(@PathVariable("shopId") Long shopId, @PathVariable("id") Long orderId, @Validated @RequestBody OrderVo orderVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName) {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID, "传入的RequestBody参数格式不合法"));
        }
        return Common.decorateReturnObject(orderService.updateOrderComment(shopId, orderId, orderVo, loginUserId, loginUserName));
    }

    @GetMapping("shops/{shopId}/orders/{id}")
    @Audit(departName = "order")
    public Object getOrderDetail(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        return Common.decorateReturnObject(orderService.getOrderDetail(shopId, id));
    }

    /**
     * task a-1
     * @Auther Fang Zheng
     * */
    @GetMapping("orders/states")
    public Object listAllOrderStateController(){
        return Common.decorateReturnObject(orderService.listAllOrderState());
    }

    @GetMapping("orders")
    @Audit(departName = "order")
    public Object listCustomerBriefOrders(@LoginUser Long userId,
                                          @RequestParam(value = "orderSn", required = false) String orderSn,
                                          @RequestParam(value = "state", required = false) Integer state,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "beginTime", required = false) LocalDateTime beginTime,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "endTime", required = false) LocalDateTime endTime,
                                          @RequestParam(value = "page", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize){
        return Common.decorateReturnObject(orderService.listCustomerBriefOrder(userId,orderSn,state,beginTime,endTime,pageNumber,pageSize)) ;
    }

}
