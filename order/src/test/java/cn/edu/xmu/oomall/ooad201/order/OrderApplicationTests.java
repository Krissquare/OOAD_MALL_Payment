package cn.edu.xmu.oomall.ooad201.order;

import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderApplication.class)
@AutoConfigureMockMvc
@Transactional
class OrderApplicationTests {
    private static final JwtHelper jwtHelper = new JwtHelper();
    String token;
    String token4;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        token = jwtHelper.createToken(2L, "lxc", 0L, 1, 3600);
        token4 = jwtHelper.createToken(4L, "lxc", 0L, 1, 3600);
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

}
