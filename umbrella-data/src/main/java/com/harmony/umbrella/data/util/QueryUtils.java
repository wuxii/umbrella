package com.harmony.umbrella.data.util;

import static com.harmony.umbrella.data.Bond.Link.*;
import static java.util.regex.Pattern.*;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.bond.AbstractBond;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Sort.Order;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * 查询工具类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class QueryUtils {

    public static final String COUNT_QUERY_STRING = "select count(%s) from %s x";
    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";
    public static final String DEFAULT_ALIAS = "x";

    private static final String COUNT_REPLACEMENT_TEMPLATE = "select count(%s) $5$6$7";
    private static final String SIMPLE_COUNT_VALUE = "$2";
    private static final String COMPLEX_COUNT_VALUE = "$3$6";
    private static final String ORDER_BY_PART = "(?iu)\\s+order\\s+by\\s+.*$";

    private static final Pattern ALIAS_MATCH;
    private static final Pattern COUNT_MATCH;

    private static final String IDENTIFIER = "[\\p{Alnum}._$]+";
    private static final String IDENTIFIER_GROUP = String.format("(%s)", IDENTIFIER);

    private static final String LEFT_JOIN = "left (outer )?join " + IDENTIFIER + " (as )?" + IDENTIFIER_GROUP;
    private static final Pattern LEFT_JOIN_PATTERN = Pattern.compile(LEFT_JOIN, Pattern.CASE_INSENSITIVE);

    private static final String EQUALS_CONDITION_STRING = "%s.%s = :%s";
    private static final Pattern ORDER_BY = Pattern.compile(".*order\\s+by\\s+.*", CASE_INSENSITIVE);

    private static final int QUERY_JOIN_ALIAS_GROUP_INDEX = 3;
    private static final int VARIABLE_NAME_GROUP_INDEX = 4;

    private static final Map<PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

    static {

        StringBuilder builder = new StringBuilder();
        builder.append("(?<=from)"); // from as starting delimiter
        builder.append("(?: )+"); // at least one space separating
        builder.append(IDENTIFIER_GROUP); // Entity name, can be qualified (any
        builder.append("(?: as)*"); // exclude possible "as" keyword
        builder.append("(?: )+"); // at least one space separating
        builder.append("(\\w*)"); // the actual alias

        ALIAS_MATCH = compile(builder.toString(), CASE_INSENSITIVE);

        builder = new StringBuilder();
        builder.append("(select\\s+((distinct )?(.+?)?)\\s+)?(from\\s+");
        builder.append(IDENTIFIER);
        builder.append("(?:\\s+as)?\\s+)");
        builder.append(IDENTIFIER_GROUP);
        builder.append("(.*)");

        COUNT_MATCH = compile(builder.toString(), CASE_INSENSITIVE);

        Map<PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<PersistentAttributeType, Class<? extends Annotation>>();
        persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
        persistentAttributeTypes.put(ONE_TO_MANY, null);
        persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
        persistentAttributeTypes.put(MANY_TO_MANY, null);
        persistentAttributeTypes.put(ELEMENT_COLLECTION, null);

        ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private QueryUtils() {

    }

    /**
     * Returns the query string to execute an exists query for the given id
     * attributes.
     * 
     * @param entityName
     *            the name of the entity to create the query for, must not be
     *            {@literal null}.
     * @param countQueryPlaceHolder
     *            the placeholder for the count clause, must not be
     *            {@literal null}.
     * @param idAttributes
     *            the id attributes for the entity, must not be {@literal null}.
     * @return
     */
    public static String getExistsQueryString(String entityName, String countQueryPlaceHolder, Iterable<String> idAttributes) {

        StringBuilder sb = new StringBuilder(String.format(COUNT_QUERY_STRING, countQueryPlaceHolder, entityName));
        sb.append(" WHERE ");

        for (String idAttribute : idAttributes) {
            sb.append(String.format(EQUALS_CONDITION_STRING, "x", idAttribute, idAttribute));
            sb.append(" AND ");
        }

        sb.append("1 = 1");
        return sb.toString();
    }

    /**
     * Returns the query string for the given class name.
     * 
     * @param template
     * @param entityName
     * @return
     */
    public static String getQueryString(String template, String entityName) {

        Assert.hasText(entityName, "Entity name must not be null or empty!");

        return String.format(template, entityName);
    }

    /**
     * Adds {@literal order by} clause to the JPQL query. Uses the
     * {@link #DEFAULT_ALIAS} to bind the sorting property to.
     * 
     * @param query
     * @param sort
     * @return
     */
    public static String applySorting(String query, Sort sort) {

        return applySorting(query, sort, DEFAULT_ALIAS);
    }

    /**
     * Adds {@literal order by} clause to the JPQL query.
     * 
     * @param query
     * @param sort
     * @param alias
     * @return
     */
    public static String applySorting(String query, Sort sort, String alias) {

        Assert.hasText(query);

        if (null == sort || !sort.iterator().hasNext()) {
            return query;
        }

        StringBuilder builder = new StringBuilder(query);

        if (!ORDER_BY.matcher(query).matches()) {
            builder.append(" order by ");
        } else {
            builder.append(", ");
        }

        Set<String> aliases = getOuterJoinAliases(query);

        for (Order order : sort) {
            builder.append(getOrderClause(aliases, alias, order)).append(", ");
        }

        builder.delete(builder.length() - 2, builder.length());

        return builder.toString();
    }

    /**
     * Returns the order clause for the given {@link Order}. Will prefix the
     * clause with the given alias if the referenced property refers to a join
     * alias.
     * 
     * @param joinAliases
     *            the join aliases of the original query.
     * @param alias
     *            the alias for the root entity.
     * @param order
     *            the order object to build the clause for.
     * @return
     */
    private static String getOrderClause(Set<String> joinAliases, String alias, Order order) {

        String property = order.getProperty();
        boolean qualifyReference = !property.contains("("); // ( indicates a
                                                            // function

        for (String joinAlias : joinAliases) {
            if (property.startsWith(joinAlias)) {
                qualifyReference = false;
                break;
            }
        }

        String reference = qualifyReference ? String.format("%s.%s", alias, property) : property;
        String wrapped = order.isIgnoreCase() ? String.format("lower(%s)", reference) : reference;

        return String.format("%s %s", wrapped, toJpaDirection(order));
    }

    /**
     * Returns the aliases used for {@code left (outer) join}s.
     * 
     * @param query
     * @return
     */
    static Set<String> getOuterJoinAliases(String query) {

        Set<String> result = new HashSet<String>();
        Matcher matcher = LEFT_JOIN_PATTERN.matcher(query);

        while (matcher.find()) {

            String alias = matcher.group(QUERY_JOIN_ALIAS_GROUP_INDEX);
            if (StringUtils.hasText(alias)) {
                result.add(alias);
            }
        }

        return result;
    }

    private static String toJpaDirection(Order order) {
        return order.getDirection().name().toLowerCase(Locale.US);
    }

    /**
     * Resolves the alias for the entity to be retrieved from the given JPA
     * query.
     * 
     * @param query
     * @return
     */
    public static String detectAlias(String query) {

        Matcher matcher = ALIAS_MATCH.matcher(query);

        return matcher.find() ? matcher.group(2) : null;
    }

    /**
     * Creates a where-clause referencing the given entities and appends it to
     * the given query string. Binds the given entities to the query.
     * 
     * @param <T>
     * @param queryString
     * @param entities
     * @param entityManager
     * @return
     */
    public static <T> Query applyAndBind(String queryString, Iterable<T> entities, EntityManager entityManager) {

        Assert.notNull(queryString);
        Assert.notNull(entities);
        Assert.notNull(entityManager);

        Iterator<T> iterator = entities.iterator();

        if (!iterator.hasNext()) {
            return entityManager.createQuery(queryString);
        }

        String alias = detectAlias(queryString);
        StringBuilder builder = new StringBuilder(queryString);
        builder.append(" where");

        int i = 0;

        while (iterator.hasNext()) {

            iterator.next();

            builder.append(String.format(" %s = ?%d", alias, ++i));

            if (iterator.hasNext()) {
                builder.append(" or");
            }
        }

        Query query = entityManager.createQuery(builder.toString());

        iterator = entities.iterator();
        i = 0;

        while (iterator.hasNext()) {
            query.setParameter(++i, iterator.next());
        }

        return query;
    }

    /**
     * Creates a count projected query from the given original query.
     * 
     * @param originalQuery
     *            must not be {@literal null} or empty.
     * @return
     */
    public static String createCountQueryFor(String originalQuery) {
        return createCountQueryFor(originalQuery, null);
    }

    /**
     * Creates a count projected query from the given original query.
     * 
     * @param originalQuery
     *            must not be {@literal null}.
     * @param countProjection
     *            may be {@literal null}.
     * @return
     * @since 1.6
     */
    public static String createCountQueryFor(String originalQuery, String countProjection) {

        Assert.hasText(originalQuery, "OriginalQuery must not be null or empty!");

        Matcher matcher = COUNT_MATCH.matcher(originalQuery);
        String countQuery = null;

        if (countProjection == null) {

            String variable = matcher.matches() ? matcher.group(VARIABLE_NAME_GROUP_INDEX) : null;
            boolean useVariable = variable != null && StringUtils.hasText(variable) && !variable.startsWith("new") && !variable.startsWith("count(")
                    && !variable.contains(",");

            String replacement = useVariable ? SIMPLE_COUNT_VALUE : COMPLEX_COUNT_VALUE;
            countQuery = matcher.replaceFirst(String.format(COUNT_REPLACEMENT_TEMPLATE, replacement));
        } else {
            countQuery = matcher.replaceFirst(String.format(COUNT_REPLACEMENT_TEMPLATE, countProjection));
        }

        return countQuery.replaceFirst(ORDER_BY_PART, "");
    }

    /**
     * Returns whether the given {@link Query} contains named parameters.
     * 
     * @param query
     * @return
     */
    public static boolean hasNamedParameter(Query query) {

        for (Parameter<?> parameter : query.getParameters()) {
            if (parameter.getName() != null) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, String name) {

        StringTokenizer token = new StringTokenizer(name, ".");
        Bindable<?> propertyPathModel = null;
        Expression<T> result = (Expression<T>) from;

        while (token.hasMoreTokens()) {
            Bindable<?> model = ((From<?, ?>) result).getModel();
            String currentName = token.nextToken();
            if (model instanceof ManagedType) {
                propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(currentName);
            } else {
                propertyPathModel = (((From<?, ?>) result).get(currentName)).getModel();
            }
            if (requiresJoin(propertyPathModel, model instanceof PluralAttribute)) {
                result = (Expression<T>) getOrCreateJoin(from, currentName);
            } else {
                result = ((From<?, ?>) result).get(currentName);
            }
        }
        return result;
    }

    /**
     * Returns whether the given {@code propertyPathModel} requires the creation
     * of a join. This is the case if we find a non-optional association.
     * 
     * @param propertyPathModel
     *            must not be {@literal null}.
     * @param forPluralAttribute
     * @return
     */
    private static boolean requiresJoin(Bindable<?> propertyPathModel, boolean forPluralAttribute) {

        if (propertyPathModel == null && forPluralAttribute) {
            return true;
        }

        if (!(propertyPathModel instanceof Attribute)) {
            return false;
        }

        Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;

        if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
            return false;
        }

        Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());

        if (associationAnnotation == null) {
            return true;
        }

        Member member = attribute.getJavaMember();

        if (!(member instanceof AnnotatedElement)) {
            return true;
        }

        Annotation annotation = null;
        AnnotatedElement annotatedElement = ((AnnotatedElement) member);
        try {
            annotation = annotatedElement.getAnnotation(associationAnnotation);
            if (annotation == null) {
                for (Annotation metaAnn : annotatedElement.getAnnotations()) {
                    annotation = metaAnn.annotationType().getAnnotation(associationAnnotation);
                    if (annotation != null) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return annotation == null ? true : (Boolean) getAnnotationValue(annotation, "optional");
    }
    
    private static Object getAnnotationValue(Annotation ann, String name) {
        try {
            return ann.getClass().getMethod(name).invoke(ann);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns an existing join for the given attribute if one already exists or
     * creates a new one if not.
     * 
     * @param from
     *            the {@link From} to get the current joins from.
     * @param attribute
     *            the {@link Attribute} to look for in the current joins.
     * @return will never be {@literal null}.
     */
    private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {

        for (Join<?, ?> join : from.getJoins()) {

            boolean sameName = join.getAttribute().getName().equals(attribute);

            if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
                return join;
            }
        }

        return from.join(attribute, JoinType.LEFT);
    }

    public static List<javax.persistence.criteria.Order> toJpaOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> result = new ArrayList<javax.persistence.criteria.Order>();
        for (Sort.Order order : sort) {
            Expression<?> expression = toExpressionRecursively(root, order.getProperty());
            result.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
        }
        return result;
    }

    public static String trueCondition() {
        return "1 = 1";
    }

    public static String falseCondition() {
        return "1 <> 1";
    }

    public static Bond trueBond() {
        return new BooleanBond(true);
    }

    public static Bond falseBond() {
        return new BooleanBond(false);
    }

    private static final class BooleanBond extends AbstractBond {

        private static final long serialVersionUID = 8351121402796624988L;

        public BooleanBond(boolean negated) {
            super("1", "1", negated ? EQUAL : NOT_EQUAL);
        }

        private BooleanBond(Link link) {
            super("1", "1", link);
        }

        @Override
        public Bond not() {
            return new BooleanBond(link.negated());
        }

        @Override
        public boolean isInline() {
            return true;
        }

    }

}