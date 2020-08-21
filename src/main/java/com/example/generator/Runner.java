package com.example.generator;

import com.example.generator.generate.CodeGenerator;
import com.example.generator.loader.PropertiesLoader;

/**
 * @author Administrator
 * @date 2020-08-20 15:02
 */
public class Runner {

    public static void main(String[] args) throws Exception {
        System.out.println("开始生成模板......");
        CodeGenerator generator = new CodeGenerator(PropertiesLoader.getInstance().getGeneratorProperties());
        generator.write();
        System.out.println("结束......");
    }

}
