package cn.edu.xmu.oomall.annotation;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * 过滤返回字段
 *
 * @author wwc
 * @date 2020/11/22 09:33
 * @version 1.0
 */
@Slf4j
public class JsonFilterSerializer {
    /**
     * DYNC_INCLUDE 包含的标识
     * DYNC_EXCLUDE 过滤的标识
     */
    private static final String DYNC_INCLUDE = "DYNC_INCLUDE";
    private static final String DYNC_EXCLUDE = "DYNC_EXCLUDE";
    private final ObjectMapper mapper = new ObjectMapper();

    public void filter(Class<?> clazz, String include, String exclude) {
        if (clazz == null) {
            return;
        }
        //包含的操作,多个字段用,分割开
        if (include != null && include.length() > 0) {
            log.debug("include: " + include);
            mapper.setFilterProvider(new SimpleFilterProvider()
                    .addFilter(DYNC_INCLUDE, SimpleBeanPropertyFilter.filterOutAllExcept(include.split(","))));
            mapper.addMixIn(clazz, DynamicInclude.class);
        } else if (exclude != null && exclude.length() > 0) {
            log.debug("exclude: " + exclude);
            mapper.setFilterProvider(new SimpleFilterProvider()
                    .addFilter(DYNC_EXCLUDE, SimpleBeanPropertyFilter.serializeAllExcept(exclude.split(","))));
            mapper.addMixIn(clazz, DynamicExclude.class);
        }
    }

    public String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    @JsonFilter(DYNC_EXCLUDE)
    interface DynamicExclude {

    }

    @JsonFilter(DYNC_INCLUDE)
    interface DynamicInclude {

    }
}
