/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.core.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Niels
 */
public class ConfigLoader {

    /**
     * 读取properties配置文件
     *
     * @param fileName 配置文件名称
     * @return properties配置文件类
     */
    public static Properties loadProperties(String fileName) throws IOException {
        InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
        return loadProperties(is);
    }

    public static Properties loadProperties(InputStream is) throws IOException {
        Properties prop;
        try {
            prop = new Properties();
            prop.load(is);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException e) {}
        }
        return prop;
    }

//
//    /**
//     * 读取ini配置文件
//     *
//     * @param fileName 配置文件名称
//     * @return ini配置文件类
//     */
//    public static IniEntity loadIni(String fileName) throws IOException {
//        Config cfg = new Config();
//        URL url = ConfigLoader.class.getClassLoader().getResource(fileName);
//        cfg.setMultiSection(true);
//        Ini ini = new Ini();
//        ini.setConfig(cfg);
//        ini.load(url);
//        return new IniEntity(ini);
//    }
//
//    /**
//     * 读取ini配置文件
//     *
//     * @param inputStream 配置文件
//     * @return ini配置文件类
//     */
//    public static IniEntity loadIni(InputStream inputStream) throws IOException {
//        Config cfg = new Config();
//        cfg.setMultiSection(true);
//        Ini ini = new Ini();
//        ini.setConfig(cfg);
//        ini.load(inputStream);
//        return new IniEntity(ini);
//    }
//
//    /**
//     * 读取json配置文件
//     * @param fileName
//     * @throws Exception
//     */
//    @Deprecated
//    public static void loadJsonCfg(String fileName) throws Exception {
//        String configJson = IoUtils.read(fileName);
//        List<ConfigItem> configItems = JSONUtils.json2list(configJson, ConfigItem.class);
//        Map<String, ConfigItem> map = new HashMap<>(configItems.size());
//        configItems.forEach(e -> map.put(e.getName(), e));
//        map.clear();
//    }
}
