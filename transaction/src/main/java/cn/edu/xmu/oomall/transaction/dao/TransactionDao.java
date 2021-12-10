package cn.edu.xmu.oomall.transaction.dao;

import cn.edu.xmu.oomall.transaction.mapper.PaymentPoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDao {
    @Autowired
    PaymentPoMapper paymentPoMapper;

}
