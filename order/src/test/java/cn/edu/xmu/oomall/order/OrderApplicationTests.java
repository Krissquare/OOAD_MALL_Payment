package cn.edu.xmu.oomall.order;

import cn.edu.xmu.oomall.order.microservice.CustomService;
import cn.edu.xmu.oomall.order.microservice.ShopService;
import cn.edu.xmu.oomall.order.model.vo.SimpleVo;
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
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        token = jwtHelper.createToken(2L, "lxc", 0L, 1, 3600);
        token4 = jwtHelper.createToken(4L, "lxc", 0L, 1, 3600);
        Mockito.when(shopService.getShopById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
        Mockito.when(customService.getCustomerById(Mockito.anyLong())).thenReturn(new InternalReturnObject<>(new SimpleVo(1L, "aaa")));
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
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
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
    public void testCancleOrderByCunstomer() throws Exception {
        String responseString = mvc.perform(put("/orders/1/cancel")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(expectString, responseString, true);

        String responseString1 = mvc.perform(put("/orders/9/cancel")
                .header("authorization", token4)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, true);
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
        String expected = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"customerId\":1,\"shopId\":1,\"pid\":1,\"state\":null,\"gmtCreate\":\"2021-12-02T16:41:29\",\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null},{\"id\":9,\"customerId\":4,\"shopId\":1,\"pid\":0,\"state\":null,\"gmtCreate\":\"2021-12-02T17:18:19\",\"originPrice\":280,\"discountPrice\":15,\"expressFee\":6,\"point\":12,\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":\"36527364532\"}]},\"errmsg\":\"成功\"}";
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
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/1/orders/2").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":0,\"data\":{\"id\":2,\"orderSn\":\"20216487652635231002\",\"customerVo\":{\"id\":1,\"name\":\"aaa\"},\"shopVo\":{\"id\":1,\"name\":\"aaa\"},\"pid\":1,\"state\":null,\"confirmTime\":null,\"originPrice\":50,\"discountPrice\":5,\"expressFee\":null,\"point\":3,\"message\":\"好耶\",\"regionId\":1,\"address\":\"临沂\",\"mobile\":\"16253645342\",\"consignee\":\"gyt\",\"grouponId\":null,\"advancesaleId\":null,\"shipmentSn\":null,\"orderItems\":[{\"productId\":1,\"onsaleId\":1,\"quantity\":1,\"couponActId\":null,\"couponId\":1,\"price\":50,\"name\":\"巧克力\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

}
