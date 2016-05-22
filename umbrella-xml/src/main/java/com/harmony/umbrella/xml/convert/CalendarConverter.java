package com.harmony.umbrella.xml.convert;

import java.text.ParseException;
import java.util.Calendar;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
public class CalendarConverter extends AbstractValueConverter<Calendar> {

    private String pattern;

    @Override
    protected void init() {
        this.pattern = getAttribute("format");
    }

    @Override
    protected Calendar convertValue(String t) {
        if (StringUtils.isNotBlank(pattern)) {
            try {
                return TimeUtils.toCalendar(t, pattern);
            } catch (ParseException e) {
                ReflectionUtils.rethrowRuntimeException(e);
            }
        }
        try {
            return TimeUtils.toCalendar(t);
        } catch (ParseException e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
        throw new IllegalArgumentException();
    }

}
