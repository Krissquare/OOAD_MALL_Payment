package cn.edu.xmu.oomall.order.util;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.microservice.vo.PaymentRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.RefundRetVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateObject {
    public static ReturnObject listRefunds(Long id)
    {
        List<RefundRetVo> refundRetVoList=new ArrayList<>();
        RefundRetVo refundRetVo=new RefundRetVo();
        refundRetVo.setId(1l);
        RefundRetVo refundRetVo1=new RefundRetVo();
        refundRetVo1.setId(2l);
        refundRetVoList.add(refundRetVo);
        refundRetVoList.add(refundRetVo1);
        Map<String,Object> map=new HashMap<>();
        map.put("list",refundRetVoList);
        map.put("total",10);
        return new ReturnObject(map);
    }
    public static ReturnObject listPayments(Long id)
    {
        List<PaymentRetVo> paymentList=new ArrayList<>();
        PaymentRetVo retVo=new PaymentRetVo();
        retVo.setId(1L);
        retVo.setAmount(500L);
        paymentList.add(retVo);
        PaymentRetVo retVo1=new PaymentRetVo();
        retVo1.setId(2L);
        retVo1.setAmount(100L);
        paymentList.add(retVo1);
        Map<String,Object> map=new HashMap<>();
        map.put("list",paymentList);
        map.put("total",10);
        return new ReturnObject(map);
    }
}
