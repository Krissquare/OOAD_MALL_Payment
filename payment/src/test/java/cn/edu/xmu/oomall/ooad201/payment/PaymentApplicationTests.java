package cn.edu.xmu.oomall.ooad201.payment;

import cn.edu.xmu.oomall.ooad201.payment.model.vo.RefundRecVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PaymentApplication.class)
@AutoConfigureMockMvc
@Transactional
class PaymentApplicationTests {
    private static String adminToken;
    private static final JwtHelper jwtHelper = new JwtHelper();
    @Autowired
    private MockMvc mvc;
    @Test
    void contextLoads() {
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
}
