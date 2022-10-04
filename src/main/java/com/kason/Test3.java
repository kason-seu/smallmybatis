package com.kason;

import com.kason.entity.ProductCategory;
import com.kason.mapper.ProductCategoryMapper;
import com.kason.mybatis.SqlSession;

public class Test3 {

    public static void main(String[] args) {


        ProductCategoryMapper mapper = SqlSession.getMapper(ProductCategoryMapper.class);

        ProductCategory p = new ProductCategory();
        p.setCategoryId(10001);
        p.setCategoryName("tets2");
        p.setCategoryType(2);
        int test = mapper.insert(p);
        System.out.println(test);




    }
}
