package cn.edu.xmu.oomall.order.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/07 17:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductVo {
    private Long id;
    private SimpleVo shop;
    private Long goodsId;
//    private Long onSaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Long price;
    private Integer quantity;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private SimpleVo category;
    private Boolean shareable;
}
