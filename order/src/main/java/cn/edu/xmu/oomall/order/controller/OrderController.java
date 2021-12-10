package cn.edu.xmu.oomall.order.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.vo.MarkShipmentVo;
import cn.edu.xmu.oomall.order.model.vo.OrderVo;
import cn.edu.xmu.oomall.order.model.vo.SimpleOrderVo;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.util.MyDateTime;
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

import static cn.edu.xmu.oomall.core.util.Common.processFieldErrors;

@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 新建订单
     * @author created by xiuchen lang
     * @param simpleOrderVo
     * @param bindingResult
     * @param userId
     * @param userName
     * @return
     */
    @Audit(departName = "order")
    @PostMapping("/orders")
    public Object insertOrderByCustom(@RequestBody @Valid SimpleOrderVo simpleOrderVo,
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

        return orderService.insertOrder(simpleOrderVo, userId, userName);
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
    public Object deleteOrderByCustom(@PathVariable("id") Long id, @LoginUser Long userId, @LoginName String username) {
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
    public Object cancleOrder(@PathVariable("id") Long id, @LoginUser Long userId, @LoginName String username) {
        return Common.decorateReturnObject(orderService.cancelOrderByCustomer(id, userId, username));
    }

    /**
     * 买家标记确认收货
     * create by hty
     */

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
    public Object confirmOrder(@PathVariable("id") Long id,@LoginUser Long loginUserId,@LoginName String loginName) {
        return Common.decorateReturnObject(orderService.confirmOrder(id,loginUserId,loginName));
    }

    /**
     * create by hty
     * 店家查询商户所有订单（概要）
     */

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
    public Object listBriefOrdersByShopId(@PathVariable("shopId") Long shopId,
                                          @RequestParam(value="customerId",required = false)Long customerId,
                                          @RequestParam(value="orderSn",required = false) String orderSn,
                                          @RequestParam(value = "beginTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT) LocalDateTime beginTime,
                                          @RequestParam(value = "endTime",required = false)@DateTimeFormat(pattern = MyDateTime.DATE_TIME_FORMAT)LocalDateTime endTime,
                                          @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime))
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        return Common.decorateReturnObject(orderService.listBriefOrdersByShopId(shopId, customerId,orderSn,beginTime,endTime,page, pageSize));
    }

    /**
     * create by hty
     * 店家修改订单（留言）
     * @param shopId
     * @param orderId
     * @param orderVo
     * @param bindingResult
     * @param loginUserId
     * @param loginUserName
     * @return
     */

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
    public Object updateOrderComment(@PathVariable("shopId") Long shopId, @PathVariable("id") Long orderId, @Validated @RequestBody OrderVo orderVo, BindingResult bindingResult, @LoginUser Long loginUserId, @LoginName String loginUserName) {
        if (bindingResult.hasErrors()) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID));
        }
        return Common.decorateReturnObject(orderService.updateOrderComment(shopId, orderId, orderVo, loginUserId, loginUserName));
    }

    /**
     * create by hty
     * 店家查询订单详情
     * @param shopId
     * @param id
     * @return
     */
    @GetMapping("shops/{shopId}/orders/{id}")
    @Audit(departName = "order")
    public Object getOrderDetail(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        return Common.decorateReturnObject(orderService.getOrderDetail(shopId, id));
    }
    /**
     * gyt
     * 管理员取消本店铺订单。（a-4）
     * @param shopId
     * @param id
     * @return
     */
    @Audit(departName = "shops")
    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public Object cancelOrderByShop(@LoginUser Long userId,
                              @LoginName String userName,
                              @PathVariable(value = "shopId") Long shopId,
                              @PathVariable(value = "id") Long id) {
        return Common.decorateReturnObject(orderService.cancelOrderByShop(shopId, id, userId, userName));
    }

    /**
     * gyt
     * 店家对订单标记发货。（a-4）
     * @param loginUserId
     * @param loginUserName
     * @param shopId
     * @param id
     * @param markShipmentVo
     * @param bindingResult
     * @return
     */
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/orders/{id}/deliver")
    public Object deliverByShop(@LoginUser Long loginUserId,
                               @LoginName String loginUserName,
                               @PathVariable(value = "shopId") Long shopId,
                               @PathVariable(value = "id") Long id,
                               @Validated @RequestBody MarkShipmentVo markShipmentVo,
                               BindingResult bindingResult) {
        Object object = processFieldErrors(bindingResult, httpServletResponse);
        if (object != null) {
            return object;
        }
        return Common.decorateReturnObject(orderService.deliverByShop(shopId, id, markShipmentVo, loginUserId, loginUserName));
    }
    /**
     * gyt
     * 查询自己订单的支付信息（a-4）
     * @param id
     * @return
     */
    @GetMapping("/orders/{id}/payment")
    public Object getPaymentByOrderId(@LoginUser Long loginUserId,
                                      @LoginName String loginUserName,
                                      @PathVariable(value = "id") Long id) {
        return Common.decorateReturnObject(orderService.getPaymentByOrderId(id,loginUserId,loginUserName));
    }



}
