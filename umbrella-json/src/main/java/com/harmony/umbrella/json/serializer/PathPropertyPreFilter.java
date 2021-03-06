package com.harmony.umbrella.json.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerialContext;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 通过构建字段的path来实现类中的字段的过滤
 *
 * <pre>
 * class A {
 *     B b;
 *     C[] cs;
 * }
 *
 * class B {
 *     C c;
 *     A a;
 * }
 *
 * class C {
 *     String name;
 * }
 * </pre>
 * <p>
 * 按以上类做路径说明:
 * <p>
 * A a = new A(); 有path=b.c.name 表示 A中的B的C中的name
 * <p>
 * B b = new B(); 有path=a.cs[x].name表示B中A的数组C中指定index=x的name
 * <p>
 * <b>当启用{@linkplain SerializerFeature#DisableCircularReferenceDetect}特性时,
 * 对于数组的index取值将无效取代之的是通配符*</b>
 *
 * @author wuxii@foxmail.com
 */
@Slf4j
public abstract class PathPropertyPreFilter implements PropertyPreFilter {

    public static final int DEFAULT_MAX_HIERARCHICAL_LEVEL = 10;

    protected final int maxHierarchicalLevel;

    public PathPropertyPreFilter() {
        this(DEFAULT_MAX_HIERARCHICAL_LEVEL);
    }

    public PathPropertyPreFilter(int maxHierarchicalLevel) {
        Assert.isTrue(maxHierarchicalLevel > 5, "max hierarchical must be great than 5");
        this.maxHierarchicalLevel = maxHierarchicalLevel;
    }

    /**
     * 在数据source中格局propertyName决定是否对对应的属性进行序列化
     *
     * @param source 拥有属性propertyName的source
     * @param name   属性名称
     * @return true进行序列化， false不序列化
     */
    public abstract boolean accept(JSONSerializer serializer, Object source, String name);

    /**
     * 是否接受字段序列化
     */
    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }
        final SerialContext context = serializer.getContext();
        String path;
        if (serializer.isEnabled(SerializerFeature.DisableCircularReferenceDetect)) {
            // XXX 在target下的复杂对象做序列化时候root context为当前的复杂对象, 从而导致计算path错误. 另, 在target下的数组对象无法计算其index使用*替代
            path = getContextPath(context);
        } else {
            path = "$".equals(context.toString()) ? "" : context.toString();
            if (path.indexOf("$.") == 0) {
                path = path.substring(2);
            } else if (path.indexOf("$[") == 0) {
                path = path.substring(1);
            }
        }
        String fullPath = "".equals(path) ? name : (path + "." + name);
        boolean accept = accept(serializer, source, fullPath);
        if (log.isDebugEnabled()) {
            log.debug("{} {} property -> {}", (accept ? "accept" : "not accept"), ("".equals(path) ? "$" : path), name);
        }
        return accept;
    }

    protected final String getContextPath(SerialContext context) {
        if (context == null) {
            return "";
        }
        SerialContext sc = context;
        List<String> names = new ArrayList<>();
        while (true) {
            Object currentObject = sc.object;
            if (currentObject instanceof Collection || currentObject.getClass().isArray()) {
                names.add(sc.fieldName + "[*]");
            } else {
                names.add(sc.fieldName.toString());
            }
            if ((sc = sc.parent) == null) {
                break;
            }
            if (names.size() > maxHierarchicalLevel) {
                throw new IllegalStateException("hierarchical level overflow " + maxHierarchicalLevel);
            }
        }
        StringBuilder o = new StringBuilder();
        for (int i = names.size() - 1; i >= 0; i--) {
            o.append(names.get(i));
            if (i > 0) {
                o.append(".");
            }
        }
        return o.toString();
    }

}
