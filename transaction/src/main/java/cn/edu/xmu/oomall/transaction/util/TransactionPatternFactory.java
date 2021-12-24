package cn.edu.xmu.oomall.transaction.util;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.util.alipay.AlipayTransaction;
import cn.edu.xmu.oomall.transaction.util.wechatpay.WechatTransaction;
import cn.edu.xmu.oomall.transaction.model.bo.PaymentPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class TransactionPatternFactory {

    @Autowired
    private AlipayTransaction alipayTransaction;

    @Autowired
    private WechatTransaction wechatTransaction;

    @Autowired
    private TransactionDao transactionDao;

    final private static String REQUEST_NO_PATTERN = "^[0-9]+-[\\S]+-[0-9]$";

    public static String encodeRequestNo(Long id, String documentId, Byte docymentType) {
        // 请求号 = 流水Id + documentId + documentType
        String requestNo = String.format("%s-%s-%s", id.toString(), documentId, docymentType.toString());
        boolean isMatch = Pattern.matches(REQUEST_NO_PATTERN, requestNo);
        if (isMatch) {
            return requestNo;
        } else {
            return null;
        }
    }

    public static Map<String, Object> decodeRequestNo(String requestNo) {
        boolean isMatch = Pattern.matches(REQUEST_NO_PATTERN, requestNo);
        if (isMatch) {
            String[] strings = requestNo.split("-");
            Map<String, Object> map = new HashMap<>();
            map.put("id", strings[0]);
            map.put("documentId", strings[1]);
            map.put("documentType", strings[2]);
            return map;
        } else {
            return null;
        }
    }

    public TransactionPattern getPatternInstance(Long patternId) {
   //     ReturnObject<PaymentPattern> retPaymentPattern = transactionDao.getPaymentPatternById(patternId);
       // if (!retPaymentPattern.getCode().equals(ReturnNo.OK)) {
//            throw new ClassNotFoundException();
     //       return null;
    //    }

    //    PaymentPattern paymentPattern = retPaymentPattern.getData();

        // TODO: 利用Spring技术获取Bean对象
        if (patternId == 0) {
            return alipayTransaction;
        } else {
            return wechatTransaction;
        }
    }

    public List<TransactionPattern> listAllPatterns() {
        return null;
    }
}
