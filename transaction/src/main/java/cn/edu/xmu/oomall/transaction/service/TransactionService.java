package cn.edu.xmu.oomall.transaction.service;

import cn.edu.xmu.oomall.transaction.dao.TransactionDao;
import cn.edu.xmu.oomall.transaction.model.vo.WechatPaymentNotifyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    TransactionDao transactionDao;


    public Object paymentNotifyByWechat(WechatPaymentNotifyVo wechatPaymentNotifyVo){
        String out_trade_no = wechatPaymentNotifyVo.getResource().getCiphertext().getOut_trade_no();//订单号
//        transactionDao.listPayment()

        return null;
    }


}
