package cn.edu.xmu.oomall.goods.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 简单店铺VO
 * @author 24320182203254 秦楚彦
 * @date 2020/11/30 13:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleShopDTO implements Serializable {
    private Long id;

    private String name;
}
