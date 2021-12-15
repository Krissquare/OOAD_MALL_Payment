package cn.edu.xmu.oomall.order;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
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
import cn.edu.xmu.oomall.order.model.vo.AftersaleOrderitemRecVo;
import cn.edu.xmu.oomall.order.model.vo.AftersaleRecVo;
import cn.edu.xmu.oomall.order.model.vo.MarkShipmentVo;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
import cn.edu.xmu.oomall.order.util.CreateObject;
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

    @MockBean
    private ShopService shopService;

    @MockBean
    private CustomService customService;
    @MockBean
    private TransactionService transactionService;

    @MockBean
    private GoodsService goodsService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        token = jwtHelper.createToken(2L, "lxc", 0L, 1, 3600);
        token4 = jwtHelper.createToken(4L, "lxc", 0L, 1, 3600);
        InternalReturnObject<Map<String, Object>> refunds = CreateObject.listRefunds(1L);
        InternalReturnObject<Map<String,Object>> payments=CreateObject.listPayments(1L);
        OnSaleVo onSaleVo=new OnSaleVo();
        onSaleVo.setId(1L);
        ProductVo productVo=new ProductVo();
        productVo.setId(1L);
        productVo.setOnSaleId(1L);
        productVo.setName("123");
        Mockito.when(shopService.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
        Mockito.when(customService.getCustomerById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
        Mockito.when(goodsService.getOnsaleById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(onSaleVo));
        Mockito.when(goodsService.getProductById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(productVo));
        Mockito.when(transactionService.listRefund(0L,"20216453652635231006", RefundState.FINISH_REFUND.getCode(),null,null,1,10)).thenReturn(refunds);
        Mockito.when(transactionService.listPayment(0L,"20216489872635231004", PaymentState.ALREADY_PAY.getCode(),null,null,1,10)).thenReturn(payments);
        Mockito.when(transactionService.refund(new RefundRecVo(null,null,1L,null,500L, RefundType.ORDER.getCode()))).thenReturn(new ReturnObject(ReturnNo.OK));
        Mockito.when(transactionService.refund(new RefundRecVo(null,null,2L,null,100L,RefundType.ORDER.getCode()))).thenReturn(new ReturnObject(ReturnNo.OK));
    }

    @Test
    public void testAddOrder() throws Exception {

//TODO: Test goes here...
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
        String expected = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"customerId\":1,\"shopId\":1,\"pid\":1,\"state\":null,\"gmtCreate\":\"2021-12-02T16:41:29\",\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":null},{\"id\":9,\"customerId\":4,\"shopId\":1,\"pid\":0,\"state\":null,\"gmtCreate\":\"2021-12-02T17:18:19\",\"originPrice\":280,\"discountPrice\":15,\"expressFee\":6,\"point\":12,\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":\"36527364532\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
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
        String expected = "{\"errno\":0,\"data\":{\"id\":2,\"orderSn\":\"20216487652635231002\",\"customerVo\":{\"id\":1,\"name\":\"aaa\"},\"shopVo\":{\"id\":1,\"name\":\"aaa\"},\"pid\":1,\"state\":null,\"confirmTime\":null,\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"message\":\"好耶\",\"regionId\":1,\"address\":\"临沂\",\"mobile\":\"16253645342\",\"consignee\":\"gyt\",\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":null,\"orderItems\":[{\"productId\":1,\"onsaleId\":1,\"quantity\":1,\"couponActId\":null,\"couponId\":1,\"price\":50,\"name\":\"巧克力\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 管理员取消本店铺订单
     * @throws Exception
     */
    @Test
    public void cancelOrderByShop() throws Exception
    {
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
     * @throws Exception
     */
    @Test
    public void markShipment() throws Exception
    {
        MarkShipmentVo markShipmentVo=new MarkShipmentVo();
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
     * @throws Exception
     */
    @Test
    public void getPaymentByOrderId() throws Exception
    {
        String responseString = this.mvc.perform(get("/orders/4/payment")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":1,\"tradeSn\":null,\"patternId\":null,\"documentId\":null,\"documentType\":2,\"descr\":null,\"amount\":500,\"actualAmount\":null,\"state\":null,\"payTime\":null,\"beginTime\":null,\"endTime\":null},{\"id\":2,\"tradeSn\":null,\"patternId\":null,\"documentId\":null,\"documentType\":3,\"descr\":null,\"amount\":100,\"actualAmount\":null,\"state\":null,\"payTime\":null,\"beginTime\":null,\"endTime\":null}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void confirmGrouponOrder() throws Exception
    {
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
    public void listOrderRefundsTest() throws Exception
    {
        String responseString = this.mvc.perform(get("/orders/1/refund")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":1,\"tradeSn\":null,\"patternId\":null,\"amount\":null,\"state\":null,\"documentId\":null,\"documentType\":null},{\"id\":2,\"tradeSn\":null,\"patternId\":null,\"amount\":null,\"state\":null,\"documentId\":null,\"documentType\":null}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getOrderItemTest() throws Exception
    {
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
    public void CancelOrderByCustomerTest() throws Exception
    {
        String responseString = this.mvc.perform(put("/orders/4/cancel")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void getPaymentByOrderitemTest() throws Exception
    {
        String responseString = this.mvc.perform(get("/internal/orderitems/3/payment")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":2,\"tradeSn\":null,\"patternId\":null,\"documentId\":null,\"documentType\":3,\"descr\":null,\"amount\":100,\"actualAmount\":null,\"state\":null,\"payTime\":null,\"beginTime\":null,\"endTime\":null},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    @Test
    public void getTokens(){
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
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/orders/10")
                            .header("authorization", token4)
                            .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expected = "{\"errno\":0,\"data\":{\"id\":10,\"orderSn\":\"20218987972635231004\",\"customerVo\":{\"id\":1,\"name\":\"aaa\"},\"shopVo\":{\"id\":1,\"name\":\"aaa\"},\"pid\":0,\"state\":400,\"confirmTime\":\"2021-11-11T17:24:20\",\"originPrice\":231,\"discountPrice\":12,\"expressFee\":8,\"point\":22,\"message\":\"啦啦\",\"regionId\":4,\"address\":\"福州\",\"mobile\":\"17276541624\",\"consignee\":\"hqg\",\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":\"65442635211\",\"orderItems\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void updateOrderByCustomerTest() throws Exception{
        String voStr = "{\n" +
                "  \"consignee\": \"update-test-consignee\",\n" +
                "  \"regionId\": 6666,\n" +
                "  \"address\": \"update-test-address\",\n" +
                "  \"mobile\": \"13822223333\"\n" +
                "}";
        String response = this.mvc.perform(MockMvcRequestBuilders.put("/orders/10")
                        .header("authorization",token4)
                        .contentType("application/json;charset=UTF-8")
                        .content(voStr))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expected, response, true);
    }
    /**
     * orderId查item
     * @throws Exception
     */
    @Test
    public void listOrderItemsByOrderId() throws Exception {
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/internal/order/1")
                .header("authorization", token4)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expected="{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"id\":1,\"orderId\":2,\"shopId\":1,\"productId\":1,\"onsaleId\":1,\"name\":\"巧克力\",\"quantity\":1,\"price\":50,\"discountPrice\":5,\"point\":3,\"couponId\":1,\"couponActivityId\":1,\"customerId\":null},{\"id\":2,\"orderId\":3,\"shopId\":2,\"productId\":2,\"onsaleId\":2,\"name\":\"薯片\",\"quantity\":1,\"price\":50,\"discountPrice\":5,\"point\":3,\"couponId\":2,\"couponActivityId\":2,\"customerId\":null}]}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    public void createAftersaleTest() throws Exception
    {
        AftersaleOrderitemRecVo orderitemRecVo=new AftersaleOrderitemRecVo();
        orderitemRecVo.setProductId(1L);
        orderitemRecVo.setOnsaleId(1L);
        orderitemRecVo.setQuantity(5L);
        AftersaleRecVo aftersaleRecVo=new AftersaleRecVo();
        aftersaleRecVo.setOrderItem(orderitemRecVo);
        aftersaleRecVo.setCustomerId(1L);
        aftersaleRecVo.setConsignee("222");
        aftersaleRecVo.setRegionId(5L);
        aftersaleRecVo.setMobile("13056766288");
        String request= JacksonUtil.toJson(aftersaleRecVo);
        String response = this.mvc.perform(MockMvcRequestBuilders.post("/internal/shops/1/orders")
                .header("authorization",token4)
                .contentType("application/json;charset=UTF-8")
                .content(request))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"data\":{\"orderSn\":null,\"customer\":{\"id\":1,\"name\":\"aaa\"},\"shop\":{\"id\":1,\"name\":\"aaa\"},\"pid\":0,\"state\":201,\"confirmTime\":null,\"discountPrice\":0,\"originPrice\":0,\"point\":0,\"expressFee\":null,\"consignee\":\"222\",\"regionId\":5,\"address\":null,\"mobile\":\"13056766288\",\"message\":null,\"advancesaleId\":null,\"grouponId\":null,\"shipmentSn\":null,\"aftersaleOrderitemVo\":{\"productId\":1,\"name\":\"123\",\"quantity\":5,\"price\":0}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, response, false);
    }

}
