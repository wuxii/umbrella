/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.util;

import java.math.BigDecimal;

/**
 * @author wuxii@foxmail.com
 */
public class FormatMathConverter implements MathConverter {

    private NumberConfig nc;

    private boolean before;

    private boolean last;

    private boolean result;

    public FormatMathConverter(NumberConfig nc, boolean before, boolean result, boolean last) {
        this.nc = nc;
        this.before = before;
        this.last = last;
        this.result = result;
    }

    @Override
    public BigDecimal convert(Number t) {
        return t instanceof BigDecimal ? (BigDecimal) t : BigDecimal.valueOf(t.doubleValue());
    }

    @Override
    public BigDecimal coverterBefore(Number number) {
        BigDecimal num = convert(number);
        return before ? scale(num) : num;
    }

    @Override
    public BigDecimal convertResult(Number number) {
        BigDecimal num = convert(number);
        return result ? scale(num) : num;
    }

    @Override
    public BigDecimal convertLast(Number number) {
        BigDecimal num = convert(number);
        return last ? scale(num) : num;
    }

    private BigDecimal scale(BigDecimal num) {
        return num.setScale(nc.scale, nc.roundingMode);
    }
}
