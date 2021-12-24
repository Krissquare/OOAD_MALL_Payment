package cn.edu.xmu.oomall.order;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.order.microservice.CustomService;
import cn.edu.xmu.oomall.order.microservice.GoodsService;
import cn.edu.xmu.oomall.order.microservice.ShopService;
import cn.edu.xmu.oomall.order.microservice.TransactionService;
import cn.edu.xmu.oomall.order.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CoverageTest {

    static String adminToken;
    static final JwtHelper jwtHelper = new JwtHelper();
    String token_1;
    String token_2;
    String token_3;

    @Autowired private MockMvc mvc;

    @MockBean private ShopService shopService;
    @MockBean private TransactionService transactionService;
    @MockBean private GoodsService goodsService;
    @MockBean private CustomService customService;

    @BeforeEach
    void init() {
        /////////////////////////////
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        token_1 = jwtHelper.createToken(1L, "lxc", 0L, 1, 3600);
        token_2 = jwtHelper.createToken(2L, "lxc", 0L, 1, 3600);
        token_3 = jwtHelper.createToken(3L, "lxc", 0L, 1, 3600);
        ///////////////////////////////
        ProductVo productVo = new ProductVo();
        productVo.setId(1L);
        productVo.setOnsaleId(1L);
        productVo.setName("123");
        //////////////////////////////
//        Mockito.when().thenReturn();
//        Mockito.when(goodsService.getProductDetails(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(productVo));
//        Mockito.when(goodsService.selectFullOnsale(Mockito.anyLong())).thenReturn();
    }

    /**
     * 获得订单的所有状态
     * */
    @Test
    public void listAllOrderStateController() throws Exception{
        String responseString = mvc.perform(get("/orders/states")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 买家查询名下订单概要
     * */
    @Test
    public void listCustomerBriefOrdersController() throws Exception{
        String responseString = mvc.perform(get("/orders")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 新建订单
     * */
    @Test
    public void insertOrderByCustom() throws Exception{
        //======预售========
        SimpleOrderVo vo1 = new SimpleOrderVo(null, "lxc", 1604L, "厦门大学", "15165666666", "没有留言",
                2L, null, 666L, 100L);
        List<SimpleOrderItemVo> list1 = new ArrayList<>();
        SimpleOrderItemVo item1 = new SimpleOrderItemVo(1555L, 6L, 10L, null, null, null, null);
        list1.add(item1);
        vo1.setOrderItems(list1);
        String requestJSON = JacksonUtil.toJson(vo1);
        String responseString = mvc.perform(post("/orders")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8")
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //=====团购========

        //=====优惠券======

    }

    /**
     * 买家查询订单完整信息
     * */
    @Test
    public void getCustomerWholeOrderController() throws Exception{
        String responseString = mvc.perform(get("/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 买家修改本人名下订单
     * */
    @Test
    public void updateCustomerOrderController() throws Exception{
        UpdateOrderVo updateOrderVo = new UpdateOrderVo(null,2417L,null,"13900000000");

        String responseString = mvc.perform(put("/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8")
                        .content(JacksonUtil.toJson(updateOrderVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 买家逻辑删除本人名下订单
     * */
    @Test
    public void deleteOrderByCustom() throws Exception{
        String responseString = mvc.perform(delete("/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 买家取消本人名下订单
     * */
    @Test
    public void cancelOrderByCustomer() throws Exception{
        String responseString = mvc.perform(put("/orders/1/cancel")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 买家标记确认收货
     * */
    @Test
    public void confirmOrder() throws Exception{
        String responseString = mvc.perform(put("/orders/1/confirm")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店家查询商户所有订单概要
     * */
    @Test
    public void listBriefOrdersByShopId() throws Exception{
        String responseString = mvc.perform(get("/shops/1/orders")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店家修改订单
     * */
    @Test
    public void updateOrderComment() throws Exception{
        OrderVo orderVo = new OrderVo("lalalalademaxiya");

        String responseString = mvc.perform(put("/shops/1/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8")
                        .content(JacksonUtil.toJson(orderVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店家查询店内订单完整信息
     * */
    @Test
    public void getOrderDetail() throws Exception{
        String responseString = mvc.perform(get("/shops/1/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 管理员取消本店铺订单
     * */
    @Test
    public void cancelOrderByShop() throws Exception{
        String responseString = mvc.perform(delete("/shops/1/orders/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 店家对订单标记发货
     * */
    @Test
    public void deliverByShop() throws Exception{
        MarkShipmentVo markShipmentVo = new MarkShipmentVo("lalalalalualua");

        String responseString = mvc.perform(put("/shops/1/orders/1/deliver")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8")
                        .content(JacksonUtil.toJson(markShipmentVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 查询自己订单的支付信息
     * */
    @Test
    public void getPaymentByOrderId() throws Exception{
        String responseString = mvc.perform(get("/orders/1/payment")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 确认团购订单
     * */
    @Test
    public void confirmGrouponOrder() throws Exception{
        //qm数据库里面一个团购单都没有......
        String responseString = mvc.perform(put("/internal/shops/1/grouponorders/1/confirm")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 内部API取消订单
     * */
    @Test
    public void internalCancleOrderByShop() throws Exception{
        String responseString = mvc.perform(put("/internal/shops/1/orders/1/cancel")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 内部API管理员建立售后订单
     * */
    @Test
    public void createAftersaleOrder() throws Exception{
        AftersaleRecVo aftersaleRecVo = new AftersaleRecVo(new AftersaleOrderitemRecVo(5223L,3674L,1L),
                "fz",2417L,null,"13900000000",null,null);

        String responseString = mvc.perform(post("/internal/shops/1/orders")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8")
                        .content(JacksonUtil.toJson(aftersaleRecVo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 查询自己订单的退款信息
     * */
    @Test
    public void listOrderRefunds() throws Exception{
        String responseString = mvc.perform(get("/orders/1/refund")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 根据Itemid找item
     * */
    @Test
    public void getOrderItemById() throws Exception{
        String responseString = mvc.perform(get("/internal/orderitems/1")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * 根据itemid找Payment
     * */
    @Test
    public void getPaymentByOrderItemId() throws Exception{
        String responseString = mvc.perform(get("/internal/orderitems/11864/payment")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * orderId查item
     * */
    @Test
    public void listOrderItemsByOrderId() throws Exception{
        String responseString = mvc.perform(get("/internal/orders/1/orderitems")
                        .header("authorization", token_1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * orderSn查orderId
     * */
    @Test
    public void getOrderIdByOrderSn() throws Exception{
        String responseString = mvc.perform(get("/internal/orderid")
                        .header("authorization", token_1)
                        .queryParam("orderSn","2016102361242")
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }


}
