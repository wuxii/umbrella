package com.harmony.umbrella.data;

import com.harmony.umbrella.data.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.QueryBuilder.JoinAttributes;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleImpl<M> implements QueryBundle<M>, Serializable {

    private static final long serialVersionUID = 1L;

    Class<M> domainClass;

    Specification condition;

    FetchAttributes fetchAttributes;

    JoinAttributes joinAttributes;

    Selections grouping;

    int queryFeature;

    int pageNumber;

    int pageSize;

    Sort sort;

    public QueryBundleImpl() {
    }

    public QueryBundleImpl(QueryBundle bundle) {
        this.domainClass = bundle.getDomainClass();
        this.condition = bundle.getSpecification();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.grouping = bundle.getGrouping();
        this.queryFeature = bundle.getQueryFeature();
        this.pageNumber = bundle.getPageNumber();
        this.pageSize = bundle.getPageSize();
        this.sort = bundle.getSort();
    }

    @Override
    public Class<M> getDomainClass() {
        return domainClass;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Specification getSpecification() {
        return condition;
    }

    @Override
    public int getQueryFeature() {
        return queryFeature;
    }

    @Override
    public FetchAttributes getFetchAttributes() {
        return fetchAttributes;
    }

    @Override
    public JoinAttributes getJoinAttributes() {
        return joinAttributes;
    }

    @Override
    public Selections getGrouping() {
        return grouping;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("select * from ").append(domainClass != null ? domainClass.getSimpleName() : "UnknownType");
        if (condition != null) {
            out.append(" where ").append(condition);
        }
        if (grouping != null) {
            out.append(" group by ").append(grouping);
        }
        return out.toString();
    }

}
