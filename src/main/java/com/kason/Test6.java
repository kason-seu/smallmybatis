package com.kason;

import com.kason.annotations.MapperScan;
import com.kason.app.AppConfig;
import com.kason.entity.ProductCategory;
import com.kason.mapper.ProductCategoryMapper;
import com.kason.mybatis.MapperFactory;
import com.kason.mybatis.SqlSession;

import java.util.List;

public class Test6 {

    public static void main(String[] args) {


        MapperFactory mapperFactory = new MapperFactory(AppConfig.class);


        ProductCategoryMapper productCategoryMapper = (ProductCategoryMapper)mapperFactory.getMapper("productCategoryMapper");


        ProductCategory p = new ProductCategory();
        p.setCategoryType(2);
        List<ProductCategory> productCategories = productCategoryMapper.selectLists(p);
        for (ProductCategory productCategory : productCategories) {
            System.out.println(productCategory);
        }
    }
}

