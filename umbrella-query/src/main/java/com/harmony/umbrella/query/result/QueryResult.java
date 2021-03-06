package com.harmony.umbrella.query.result;

import com.harmony.umbrella.query.SpecificationSupplier;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Setter
@Accessors(chain = true)
public class QueryResult<T> {

    private EntityManager entityManager;
    @Setter(AccessLevel.NONE)
    private CriteriaBuilder criteriaBuilder;

    private SpecificationSupplier<T> specification;
    private Class<T> domainClass;

    public QueryResult(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Optional<T> getSingleResult() {
        CriteriaQuery<T> query = criteriaBuilder.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        Predicate predicate = specification.get().toPredicate(root, query, criteriaBuilder);
        query.select(root).where(predicate);
        return Optional.ofNullable(entityManager.createQuery(query).getSingleResult());
    }

    public Optional<T> getFirstResult() {
        return null;
    }

    public List<T> getListResult() {
        return null;
    }

}
