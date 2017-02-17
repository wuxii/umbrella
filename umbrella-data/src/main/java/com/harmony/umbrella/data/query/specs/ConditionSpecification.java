package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConditionSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 5012760558487273137L;

    private String left;
    private Object right;
    private Operator operator;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression x = QueryUtils.toExpressionRecursively(root, left);
        return operator.explain(x, cb, right);
    }

}