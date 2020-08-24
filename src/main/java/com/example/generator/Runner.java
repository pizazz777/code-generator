package com.example.generator;

import com.example.generator.generate.CodeGenerator;
import com.example.generator.loader.PropertiesLoader;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 代码生成器 配置好properties一键生成
 **/
public class Runner {

    public static void main(String[] args) throws Exception {
        System.out.println("开始生成模板......");
        CodeGenerator generator = new CodeGenerator(PropertiesLoader.getInstance().getGeneratorProperties());
        generator.write();
        System.out.println("结束......");
    }

}
