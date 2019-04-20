package com.tuaier.framework.beans.support;

import com.tuaier.framework.beans.config.TuaierBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author tuaier
 */
public class TuaierBeanDefinitionReader {

    private List<String> registerBeanClasses = new ArrayList<>();

    private Properties config = new Properties();
    /**
     * 固定配置文件中的key 和xml中的规范对应
     */
    private final String SCAN_PACKAGE = "scanPackage";

    public TuaierBeanDefinitionReader(String... locations) {
        // 通过URL定位找到对应的文件 转化为文件流获取配置属性
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registerBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig() {
        return this.config;
    }

    /**
     * 把配置文件中扫描的所有配置信息转换为BeanDefinition对象，便于之后的IOC操作
     * 为了简化逻辑直接返回List<TuaierBeanDefinition>
     *
     * @return
     */
    public List<TuaierBeanDefinition> loadBeanDefinitions() {
        List<TuaierBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                // 如果是一个借口不能实例化，用他的实现来实例化
                if (beanClass.isInterface()) {
                    continue;
                }
                // beanName有三种情况： 1、默认是类名首字母小写  2、自定义   3、借口注入
                result.add(doCreateBeanDefinition(lowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                result.add(doCreateBeanDefinition(beanClass.getName(),beanClass.getName()));

                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 把className解析为beanDefinition
     *
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private TuaierBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        TuaierBeanDefinition beanDefinition = new TuaierBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 首字符小写
     * A-Z的ASCII码为65-90
     */

    private String lowerFirstCase(String simpleName) {
        // 大写A和Z的ASCII码
        int upA = 65, upZ = 90;
        char[] chars = simpleName.toCharArray();
        // 只翻译大写字母
        if (upA <= chars[0] && chars[0] <= upZ) {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }
}
