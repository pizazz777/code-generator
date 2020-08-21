package com.example.generator.handler;

import com.example.generator.properties.GeneratorProperties;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author administrator
 * @date 2020/08/20
 * @description: 类描述: 模板处理类
 **/
@Slf4j
public class TemplateHandler {

    /**
     * freemarker config
     */
    private Configuration configuration;

    /**
     * 将模板写入到文件
     */
    public void writeTemplateToFile(GeneratorProperties properties, String templateFileName, String targetFilePath) throws IOException, TemplateException {
        File outputFileDir = new File(getParent(targetFilePath));
        if (!outputFileDir.exists()) {
            boolean success = outputFileDir.mkdirs();
        }
        Template template = config().getTemplate(templateFileName);
        try (PrintWriter writer = new PrintWriter(targetFilePath)) {
            template.process(properties, writer);
            writer.flush();
            if (log.isDebugEnabled()) {
                log.debug("将模板[{}]写入到文件:[{}]", templateFileName, targetFilePath);
            }
        }
    }

    private String getParent(String path) {
        int index = path.lastIndexOf(File.separator);
        if (index > -1) {
            return path.substring(0, index);
        }
        return path;
    }

    /**
     * 设置/获取模板设置
     */
    private Configuration config() throws IOException {
        if (Objects.isNull(configuration)) {
            // classpath
            URL url = TemplateHandler.class.getClassLoader().getResource("");
            if (Objects.isNull(url)) {
                throw new IOException("没找到classpath路径");
            }
            File templateDir = new File(url.getPath() + File.separator + "template");
            if (!templateDir.exists()) {
                boolean success = templateDir.mkdirs();
            }
            FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(templateDir);
            configuration = new Configuration(Configuration.VERSION_2_3_25);
            configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            configuration.setLogTemplateExceptions(false);
            configuration.setTemplateLoader(fileTemplateLoader);
        }
        return configuration;
    }

}
