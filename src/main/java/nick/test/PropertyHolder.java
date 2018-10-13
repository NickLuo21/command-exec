package nick.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * Created by Nick on 2018/10/13.
 */
@Slf4j
public class PropertyHolder {

    public static final String PROP_FILE_NAME = "init.properties";
    public static final String ENV_PROP_FILE_KEY = "init.prop";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static Properties properties = null;

    public synchronized static Properties properties() {
        if (properties == null) {
            String propStr = loadResource(ENV_PROP_FILE_KEY, PROP_FILE_NAME, "/" + PROP_FILE_NAME);
            if (!Strings.isNullOrEmpty(propStr)) {
                properties = new Properties();
                try {
                    properties.load(new StringReader(propStr));
                } catch (IOException e) {
                    throw new RuntimeException("initialize configuration failed", e);
                }
            } else {
                throw new RuntimeException("make sure config properties file provided");
            }
        }
        return properties;
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            // ignore
        }
        return "";
    }

    /**
     * 加载资源文件， 可以从环境变量、工作目录和类路径下加载
     *
     * @param envProp  环境变量KEY
     * @param fileName 文件名
     * @param cpRes    类路径
     * @return
     */
    public synchronized static String loadResource(String envProp, String fileName, String cpRes) {
        String resource = null;
        // 检查环境变量是否提供了资源文件位置
        String location = System.getProperty(envProp);
        if (!Strings.isNullOrEmpty(location)) {
            File file = new File(location);
            if (file.exists()) {
                try {
                    resource = CharStreams.toString(new FileReader(file));
                    log.info("load resource from {}", file.getAbsolutePath());
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        // 检查工作目录下是否提供了资源文件
        if (Strings.isNullOrEmpty(resource)) {
            String dir = System.getProperty("user.dir");
            if (dir != null && !dir.isEmpty()) {
                File file = new File(dir, fileName);
                if (file.exists()) {
                    try {
                        resource = CharStreams.toString(new FileReader(file));
                        log.info("load resource from {}", file.getAbsolutePath());
                    } catch (IOException e) {
                        //ignore
                    }
                }
            }
        }
        // 使用类路径下的默认映射文件
        if (Strings.isNullOrEmpty(resource)) {
            try {
                resource = CharStreams.toString(new InputStreamReader(PropertyHolder.class.getResourceAsStream(cpRes)));
                log.info("load resource form classpath:{}", cpRes);
            } catch (IOException e) {
                //ignore
            }
        }
        return resource;
    }
}
