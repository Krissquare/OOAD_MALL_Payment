package cn.edu.xmu.oomall.order;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.microservice.CustomService;
import cn.edu.xmu.oomall.order.microservice.GoodsService;
import cn.edu.xmu.oomall.order.microservice.ShopService;
import cn.edu.xmu.oomall.order.microservice.TransactionService;
import cn.edu.xmu.oomall.order.microservice.bo.PaymentState;
import cn.edu.xmu.oomall.order.microservice.bo.RefundState;
import cn.edu.xmu.oomall.order.microservice.bo.RefundType;
import cn.edu.xmu.oomall.order.microservice.vo.OnSaleVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRecVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRetVo;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.util.CreateObject;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderApplication.class)
@AutoConfigureMockMvc
@Transactional
class OrderApplicationTests {
    private static String adminToken;
    private static final JwtHelper jwtHelper = new JwtHelper();
    String token;
    String token4;
    String token1;

//    @MockBean
//    private ShopService shopService;

    @Autowired
    private CustomService customService;

    @Autowired
    private TransactionService transactionService;
//    @MockBean
//    private TransactionService transactionService;

    //    @MockBean
//    private GoodsService goodsService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        token = jwtHelper.createToken(2L, "lxc", 0L, 1, 3600);
        token4 = jwtHelper.createToken(4L, "lxc", 0L, 1, 3600);
        token1 = jwtHelper.createToken(1L, "lxc", 0L, 1, 3600);
        InternalReturnObject<Map<String, Object>> refunds = CreateObject.listRefunds(1L);
        InternalReturnObject<Map<String, Object>> payments = CreateObject.listPayments(1L);
        OnSaleVo onSaleVo = new OnSaleVo();
        onSaleVo.setId(1L);
        ProductVo productVo = new ProductVo();
        productVo.setId(1L);
        productVo.setOnsaleId(1L);
        productVo.setName("123");
//        Mockito.when(shopService.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
//        Mockito.when(customService.getCustomerById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
//        Mockito.when(goodsService.getOnsaleById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onSaleVo));
//        Mockito.when(goodsService.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(productVo));
//        Mockito.when(transactionService.listRefund(0L,"20216453652635231006", RefundState.FINISH_REFUND.getCode(),null,null,1,10)).thenReturn(refunds);
//        Mockito.when(transactionService.listPayment(0L,"20216489872635231004", PaymentState.ALREADY_PAY.getCode(),null,null,1,10)).thenReturn(payments);
//        Mockito.when(transactionService.refund(new RefundRecVo(null,null,1L,null,500L, RefundType.ORDER.getCode()))).thenReturn(new InternalReturnObject<>(new RefundRetVo(1L,"123",1L,500L,(byte)0,"123",(byte)0)));
//        Mockito.when(transactionService.refund(new RefundRecVo(null,null,2L,null,100L,RefundType.ORDER.getCode()))).thenReturn(new InternalReturnObject<>(new RefundRetVo(1L,"123",1L,100L,(byte)0,"123",(byte)0)));
//        Mockito.when(shopService.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
//        Mockito.when(customService.getCustomerById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
//        Mockito.when(goodsService.getOnsaleById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onSaleVo));
//        Mockito.when(goodsService.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(productVo));
//        Mockito.when(transactionService.listRefund(0L,"20216453652635231006", RefundState.FINISH_REFUND.getCode(),null,null,1,10)).thenReturn(refunds);
//        Mockito.when(transactionService.listPayment(0L,"20216489872635231004", PaymentState.ALREADY_PAY.getCode(),null,null,1,10)).thenReturn(payments);
//        Mockito.when(transactionService.refund(new RefundRecVo(null,null,1L,null,500L, RefundType.ORDER.getCode()))).thenReturn(new ReturnObject(ReturnNo.OK));
//        Mockito.when(transactionService.refund(new RefundRecVo(null,null,2L,null,100L,RefundType.ORDER.getCode()))).thenReturn(new ReturnObject(ReturnNo.OK));
    }

    //什么活动也不参加
    @Test
    public void testAddOrder() throws Exception {
        SimpleOrderVo simpleOrderVo = new SimpleOrderVo(null, "lxc", 1604L, "厦门大学", "15165666666", "没有留言",
                null, null, 666L, 100L);
        List<SimpleOrderItemVo> list = new ArrayList<>();
        SimpleOrderItemVo simpleOrderItemVo = new SimpleOrderItemVo(1550L, 1L, 10L, null, null, null, null);
        SimpleOrderItemVo simpleOrderItemVo1 = new SimpleOrderItemVo(1554L, 5L, 30L, null, null, null, null);
        list.add(simpleOrderItemVo);
        list.add(simpleOrderItemVo1);
        simpleOrderVo.setOrderItems(list);
        String requestJSON = JacksonUtil.toJson(simpleOrderVo);
        String responseString = mvc.perform(post("/orders")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //预售
    @Test
    public void testAddOrder1() throws Exception {
        SimpleOrderVo simpleOrderVo = new SimpleOrderVo(null, "lxc", 1604L, "厦门大学", "15165666666", "没有留言",
                2L, null, 666L, 100L);
        List<SimpleOrderItemVo> list = new ArrayList<>();
        SimpleOrderItemVo simpleOrderItemVo = new SimpleOrderItemVo(1555L, 6L, 10L, null, null, null, null);
        list.add(simpleOrderItemVo);
        simpleOrderVo.setOrderItems(list);
        String requestJSON = JacksonUtil.toJson(simpleOrderVo);
        String responseString = mvc.perform(post("/orders")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //团购
    @Test
    public void testAddOrder2() throws Exception {
        SimpleOrderVo simpleOrderVo = new SimpleOrderVo(null, "lxc", 1604L, "厦门大学", "15165666666", "没有留言",
                null, 1L, 666L, 100L);
        List<SimpleOrderItemVo> list = new ArrayList<>();
        SimpleOrderItemVo simpleOrderItemVo = new SimpleOrderItemVo(1589L, 40L, 10L, null, null, null, null);
        list.add(simpleOrderItemVo);
        simpleOrderVo.setOrderItems(list);
        String requestJSON = JacksonUtil.toJson(simpleOrderVo);
        String responseString = mvc.perform(post("/orders")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8")
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    //带优惠券的
    @Test
    public void testAddOrder3() throws Exception {
        SimpleOrderVo simpleOrderVo = new SimpleOrderVo(null, "lxc", 1604L, "厦门大学", "15165666666", "没有留言",
                null, null, 1L, 100L);
        List<SimpleOrderItemVo> list = new ArrayList<>();
        SimpleOrderItemVo simpleOrderItemVo = new SimpleOrderItemVo(1558L, 9L, 10L, 1L, 1L, null, null);
        list.add(simpleOrderItemVo);
        simpleOrderVo.setOrderItems(list);
        String requestJSON = JacksonUtil.toJson(simpleOrderVo);
        String responseString = mvc.perform(post("/orders")
                        .header("authorization", token1)
                        .contentType("application/json;charset=UTF-8")
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    public void testDeleteOrderByCustomer() throws Exception {
        String responseString = mvc.perform(delete("/orders/1")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        String responseString1 = mvc.perform(delete("/orders/10")
                        .header("authorization", token4)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, true);

    }


    @Test
    public void internalCancelOrderByShopTest() throws Exception {
        String responseString = mvc.perform(put("/internal/shops/2/orders/4/cancel")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, true);
    }

    @Test
    public void confirmOrderTest() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/orders/1/confirm").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void searchBriefOrderTest() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, false);
    }

    @Test
    public void updateOrderTest() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String requestJson = "{\"message\":\"修改商品\"}";
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/1/orders/2").header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void getOrderDetailTest() throws Exception {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders/2")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"data\":{\"id\":2,\"orderSn\":\"20216487652635231002\",\"customerVo\":{\"id\":1,\"name\":\"李智樑\"},\"shopVo\":{\"id\":1,\"name\":\"OOMALL自营商铺\"},\"pid\":1,\"state\":null,\"confirmTime\":null,\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"message\":\"好耶\",\"regionId\":1,\"address\":\"临沂\",\"mobile\":\"16253645342\",\"consignee\":\"gyt\",\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":null,\"orderItems\":[{\"productId\":1,\"name\":\"巧克力\",\"quantity\":1,\"price\":50}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 管理员取消本店铺订单
     *
     * @throws Exception
     */
    @Test
    public void cancelOrderByShop() throws Exception {
        String responseString = this.mvc.perform(delete("/shops/2/orders/4")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 店家对订单标记发货。（a-4）
     *
     * @throws Exception
     */
    @Test
    public void markShipment() throws Exception {
        MarkShipmentVo markShipmentVo = new MarkShipmentVo();
        markShipmentVo.setShipmentSn("123456");
        String requestJSON = JacksonUtil.toJson(markShipmentVo);
        String responseString = this.mvc.perform(put("/shops/3/orders/5/deliver")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token)
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 查询自己订单的支付信息（a-4）
     *
     * @throws Exception
     */
    @Test
    public void getPaymentByOrderId() throws Exception {
        String responseString = this.mvc.perform(get("/orders/4/payment")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    public void confirmGrouponOrder() throws Exception {
        String responseString = this.mvc.perform(put("/internal/shops/3/grouponorders/6/confirm")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    public void listOrderRefundsTest() throws Exception {
        String responseString = this.mvc.perform(get("/orders/1/refund")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    public void getOrderItemTest() throws Exception {
        String responseString = this.mvc.perform(get("/internal/orderitems/1")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"orderId\":2,\"shopId\":1,\"productId\":1,\"onsaleId\":1,\"name\":\"巧克力\",\"quantity\":1,\"price\":50,\"discountPrice\":5,\"point\":3,\"couponId\":1,\"couponActivityId\":1,\"customerId\":1},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }

    @Test
    public void CancelOrderByCustomerTest() throws Exception {
        String responseString = this.mvc.perform(put("/orders/1/cancel")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getPaymentByOrderitemTest() throws Exception {
        String responseString = this.mvc.perform(get("/internal/orderitems/3/payment")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":2,\"tradeSn\":\"7363522132\",\"patternId\":0,\"documentId\":\"20216489872635231004\",\"documentType\":0,\"descr\":null,\"amount\":195,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-03T00:51:38.000+08:00\",\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getTokens() {
        System.out.println();
        System.out.println();
        System.out.println(token4);
        System.out.println();
        System.out.println();
        System.out.println(token);
        System.out.println();
        System.out.println();
        System.out.println(adminToken);
        System.out.println();
        System.out.println();
    }

    @Test
    public void getOrderDetailByCustomerTest() throws Exception {
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/3")
                        .header("authorization", token1)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expected = "{\"errno\":0,\"data\":{\"id\":3,\"orderSn\":\"20216487872635231007\",\"customerVo\":{\"id\":1,\"name\":\"李智樑\"},\"shopVo\":{\"id\":2,\"name\":\"甜蜜之旅\"},\"pid\":1,\"state\":null,\"confirmTime\":null,\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"message\":\"好耶\",\"regionId\":1,\"address\":\"临沂\",\"mobile\":\"16253645342\",\"consignee\":\"gyt\",\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":null,\"orderItems\":[{\"productId\":2,\"name\":\"薯片\",\"quantity\":1,\"price\":50}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void updateOrderByCustomerTest() throws Exception {
        String voStr = "{\n" +
                "  \"consignee\": \"update-test-consignee\",\n" +
                "  \"regionId\": 6666,\n" +
                "  \"address\": \"update-test-address\",\n" +
                "  \"mobile\": \"13822223333\"\n" +
                "}";
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/orders/10")
                        .header("authorization", token4)
                        .contentType("application/json;charset=UTF-8").content(voStr))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * orderId查item
     *
     * @throws Exception
     */
    @Test
    public void listOrderItemsByOrderId() throws Exception {
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/internal/orders/1/orderitems")
                        .header("authorization", token4)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expected = "{\"errno\":0,\"data\":[{\"id\":1,\"orderId\":2,\"shopId\":1,\"productId\":1,\"onsaleId\":1,\"name\":\"巧克力\",\"quantity\":1,\"price\":50,\"discountPrice\":5,\"point\":3,\"couponId\":1,\"couponActivityId\":1,\"customerId\":null},{\"id\":2,\"orderId\":3,\"shopId\":2,\"productId\":2,\"onsaleId\":2,\"name\":\"薯片\",\"quantity\":1,\"price\":50,\"discountPrice\":5,\"point\":3,\"couponId\":2,\"couponActivityId\":2,\"customerId\":null}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void createAftersaleTest() throws Exception {
        AftersaleOrderitemRecVo orderitemRecVo = new AftersaleOrderitemRecVo();
        orderitemRecVo.setProductId(1550L);
        orderitemRecVo.setOnsaleId(1L);
        orderitemRecVo.setQuantity(5L);
        AftersaleRecVo aftersaleRecVo = new AftersaleRecVo();
        aftersaleRecVo.setOrderItem(orderitemRecVo);
        aftersaleRecVo.setCustomerId(1L);
        aftersaleRecVo.setConsignee("222");
        aftersaleRecVo.setRegionId(5L);
        aftersaleRecVo.setMobile("13056766288");
        String request = JacksonUtil.toJson(aftersaleRecVo);
        String response = this.mvc.perform(MockMvcRequestBuilders.post("/internal/shops/10/orders")
                        .header("authorization", token4)
                        .contentType("application/json;charset=UTF-8")
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"data\":{\"orderSn\":null,\"customer\":{\"id\":1,\"name\":\"李智樑\"},\"shop\":{\"id\":10,\"name\":\"商铺10\"},\"pid\":0,\"state\":201,\"confirmTime\":null,\"discountPrice\":0,\"originPrice\":0,\"point\":0,\"expressFee\":null,\"consignee\":\"222\",\"regionId\":5,\"address\":null,\"mobile\":\"13056766288\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"shipmentSn\":null,\"aftersaleOrderitemVo\":{\"productId\":1550,\"name\":\"欢乐家久宝桃罐头\",\"quantity\":5,\"price\":0}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, response, false);
    }


    @Test
    public void testRequestRefund() {
        RefundRecVo refundRecVo = new RefundRecVo();
        refundRecVo.setDocumentType((byte) 0);
        refundRecVo.setPaymentId(1L);
        refundRecVo.setAmount(50L);
        refundRecVo.setDescr("123");
        refundRecVo.setReason("aaa");
        refundRecVo.setDocumentId("2021");
        refundRecVo.setPatternId(0L);
        InternalReturnObject<RefundRetVo> refundRetVo = transactionService.requestRefund(refundRecVo);
        System.out.println(refundRetVo.getData());
    }

    @Test
    public void test() throws Exception{
        adminToken = jwtHelper.createToken(1L,"admin",0L, 2,1000);
        System.out.println(adminToken);
        for(int i=0;i<10;i++){
            System.out.println(Common.genSeqNum(1).substring(0,18));
        }
//        String response = this.mvc.perform(get("/shops/1/orders/1").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
//                .andReturn().getResponse().getContentAsString();
//        System.out.println(response);
    }
}
