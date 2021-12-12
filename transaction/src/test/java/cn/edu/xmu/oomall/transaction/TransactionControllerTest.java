package cn.edu.xmu.oomall.transaction;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.oomall.transaction.model.vo.PaymentModifyVo;
import cn.edu.xmu.oomall.transaction.util.MyDateTime;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import cn.edu.xmu.oomall.transaction.model.vo.RefundRecVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = TransactionApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerTest {
    @Autowired
    private MockMvc mvc;

    private static String adminToken;

    private static final JwtHelper jwtHelper = new JwtHelper();

    private DateTimeFormatter df;

    private static final Locale LOCALE=Locale.CHINA;

    @BeforeEach
    void init() {
        df = DateTimeFormatter.ofPattern(MyDateTime.DATE_TIME_FORMAT, LOCALE);
        adminToken =jwtHelper.createToken(1L,"admin",0L, 1,40000);
    }

    @Test
    void getRefundTest() throws Exception
    {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/0/refund").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    void getRefundDetailTest() throws Exception
    {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/shops/0/refund/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    @Test
    void updateRefund() throws Exception
    {
        adminToken = jwtHelper.createToken(1L, "admin", 0L, 3600, 0);
        RefundRecVo refundRecVo=new RefundRecVo();
        refundRecVo.setState((byte)4);
        refundRecVo.setDescr("123");
        String requestJson= JacksonUtil.toJson(refundRecVo);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/shops/0/refund/1").header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expected = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expected, responseString, true);
    }

    /**
     * 平台管理员查询支付信息
     * @throws Exception
     */
    //1.查询所有
    @Test
    public void listPayment() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/payment")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":9,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"tradeSn\":\"7363522113\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":92,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T15:43:38\",\"beginTime\":null,\"endTime\":null},{\"id\":2,\"tradeSn\":\"7363522132\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":195,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T16:51:38\",\"beginTime\":null,\"endTime\":null},{\"id\":3,\"tradeSn\":\"7363872113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":232,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T00:36:19\",\"beginTime\":null,\"endTime\":null},{\"id\":4,\"tradeSn\":\"7363598113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":103,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:36:56\",\"beginTime\":null,\"endTime\":null},{\"id\":5,\"tradeSn\":\"8963522113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":123,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:40:57\",\"beginTime\":null,\"endTime\":null},{\"id\":6,\"tradeSn\":\"7363522113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":233,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:42:22\",\"beginTime\":null,\"endTime\":null},{\"id\":7,\"tradeSn\":\"7363522178\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":259,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T14:33:43\",\"beginTime\":null,\"endTime\":null},{\"id\":8,\"tradeSn\":\"7363522195\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":205,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T14:35:07\",\"beginTime\":null,\"endTime\":null},{\"id\":9,\"tradeSn\":\"5363522113\",\"patternId\":1," +
                "\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":466,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T14:37:18\",\"beginTime\":null,\"endTime\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    //2.按documentId和state查询
    @Test
    public void listPayment1() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/payment")
                .queryParam("documentId","20216453652635231006")
                .queryParam("state","1")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"tradeSn\":\"7363522113\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":92,\"actualAmount\":null,\"state\":1," +
                "\"payTime\":\"2021-12-01T15:43:38\",\"beginTime\":null,\"endTime\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    //3.按时间查询
    @Test
    public void listPayment2() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/payment")
                .queryParam("beginTime","2021-10-01T15:43:38.000")
                .queryParam("endTime","2022-10-01T15:43:38.000")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"total\":9,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":1,\"tradeSn\":\"7363522113\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":92,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T15:43:38\",\"beginTime\":null,\"endTime\":null},{\"id\":2,\"tradeSn\":\"7363522132\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":195,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T16:51:38\",\"beginTime\":null,\"endTime\":null},{\"id\":3,\"tradeSn\":\"7363872113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":232,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T00:36:19\",\"beginTime\":null,\"endTime\":null},{\"id\":4,\"tradeSn\":\"7363598113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":103,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:36:56\",\"beginTime\":null,\"endTime\":null},{\"id\":5,\"tradeSn\":\"8963522113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":123,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:40:57\",\"beginTime\":null,\"endTime\":null},{\"id\":6,\"tradeSn\":\"7363522113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":233,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T00:42:22\",\"beginTime\":null,\"endTime\":null},{\"id\":7,\"tradeSn\":\"7363522178\",\"patternId\":0,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":259,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-01T14:33:43\",\"beginTime\":null,\"endTime\":null},{\"id\":8,\"tradeSn\":\"7363522195\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":205,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T14:35:07\",\"beginTime\":null,\"endTime\":null},{\"id\":9,\"tradeSn\":\"5363522113\",\"patternId\":1,\"documentId\":null,\"documentType\":0,\"descr\":null,\"amount\":466,\"actualAmount\":null,\"state\":1,\"payTime\":\"2021-12-02T14:37:18\",\"beginTime\":null,\"endTime\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 平台管理员查询支付信息详情
     * @throws Exception
     */
    @Test
    public void getPaymentDetail() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/payment/1")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1,\"tradeSn\":\"7363522113\",\"patternId\":0,\"amount\":92,\"actualAmount\":null,\"documentId\":null,\"documentType\":0,\"payTime\":\"2021-12-01T15:43:38\",\"beginTime\":null,\"endTime\":null,\"state\":1,\"descr\":null,\"adjust\":{\"id\":null,\"name\":null},\"adjustTime\":null,\"creator\":{\"id\":1,\"name\":\"gyt\"}," +
                "\"gmtCreate\":\"2021-12-02T17:46:10\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 平台管理员修改支付信息
     * @throws Exception
     */
    @Test
    public void updatePayment() throws Exception
    {
        PaymentModifyVo paymentModifyVo=new PaymentModifyVo();
        paymentModifyVo.setState((byte)2);
        paymentModifyVo.setDescr("已对账噢");
        String requestJSON = JacksonUtil.toJson(paymentModifyVo);
        String responseString = this.mvc.perform(put("/shops/0/payment/1")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":1,\"tradeSn\":null,\"patternId\":null,\"amount\":null,\"actualAmount\":null,\"documentId\":null,\"documentType\":null,\"payTime\":null,\"beginTime\":null,\"endTime\":null,\"state\":2,\"descr\":\"已对账噢\",\"adjust\":{\"id\":null,\"name\":null},\"adjustTime\":null,\"creator\":{\"id\":null,\"name\":null},\"gmtCreate\":null,\"gmtModified\":\"2021-12-09T22:44:29.1910273\",\"modifier\":{\"id\":1,\"name\":\"admin\"}}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    public void listAllPaymentStateTest() throws Exception{
        String response = this.mvc.perform(get("/payments/states").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"data\":{\"0\":\"待支付\",\"1\":\"已支付\",\"2\":\"已对账\",\"3\":\"已清算\",\"5\":\"失败\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, response, true);
    }

    @Test
    public void listAllValidPayPatterns() throws Exception{
        String response = this.mvc.perform(get("/paypatterns").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"data\":[{\"id\":1,\"name\":\"支付宝\"},{\"id\":2,\"name\":\"微信\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, response, true);
    }

    @Test
    public void listAllPayPatterns() throws Exception{
        String response = this.mvc.perform(get("/shops/0/paypatterns").contentType("application/json;charset=UTF-8").header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        String expected = "{\"errno\":0,\"data\":[{\"id\":1,\"name\":\"支付宝\",\"state\":null,\"beginTime\":null,\"endTime\":null,\"className\":\"AlipayTransaction\",\"creator\":{\"id\":null,\"name\":null},\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":2,\"name\":\"微信\",\"state\":null,\"beginTime\":null,\"endTime\":null,\"className\":\"WechatpayTransaction\",\"creator\":{\"id\":null,\"name\":null},\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expected, response, true);
    }

}
