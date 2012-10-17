package de.commercetools.sphere.client;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import de.commercetools.internal.UserInputFilterDefinitionBase;
import static de.commercetools.internal.util.SearchUtil.*;
import static de.commercetools.internal.util.QueryStringParsing.*;
import static de.commercetools.internal.util.QueryStringConstruction.*;

import de.commercetools.internal.MultiSelectFilterDefinitionBase;
import de.commercetools.internal.UserInputFilterOnAttributeDefinitionBase;
import net.jcip.annotations.Immutable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FilterDefinitions {

    // -------------------------------------------------------------------------------------------------------
    // Fulltext
    // -------------------------------------------------------------------------------------------------------

    @Immutable
    public static final class Fulltext extends UserInputFilterDefinitionBase<String> {
        public Fulltext() { this("search"); }
        public Fulltext(String queryParam) { super(queryParam); }
        @Override public String parseValue(Map<String, String[]> queryString) {
            return parseString(queryString, queryParam);
        }
        @Override public Filters.Fulltext parse(Map<String,String[]> queryString) {
            return new Filters.Fulltext(parseValue(queryString));
        }
    }

    // TODO expose FilterTypes if needed (for now using default)
    // TODO convenience SingleSelect:
    //   newCarsFilter = new FilterDefinitions.NumberAttribute.MultiSelect(SearchAttributes.mileage, 0);
    //   vs (nicer)
    //   newCarsFilter = new FilterDefinitions.NumberAttribute.SingleSelect(SearchAttributes.mileage, 0);

    // -------------------------------------------------------------------------------------------------------
    // String
    // -------------------------------------------------------------------------------------------------------

    public static class StringAttribute {
        @Immutable
        public static class Value extends UserInputFilterOnAttributeDefinitionBase<String> {
            public Value(String attribute) { super(attribute); }
            public Value(String attribute, String queryParam) { super(attribute, queryParam); }
            @Override public String parseValue(Map<String, String[]> queryString) {
                return parseString(queryString, queryParam);
            }
            @Override public Filters.StringAttribute.Equals parse(Map<String,String[]> queryString) {
                return new Filters.StringAttribute.Equals(attribute, parseValue(queryString));
            }
        }
        @Immutable
        public static class MultiSelect extends MultiSelectFilterDefinitionBase<String> {
            public MultiSelect(String attribute, String value, String... values) { super(attribute, value, values); }
            public MultiSelect(String attribute, Collection<String> values) { super(attribute, values); }
            public MultiSelect(String attribute, String queryParam, String value, String... values) { super(attribute, queryParam, value, values); }
            public MultiSelect(String attribute, String queryParam, Collection<String> values) { super(attribute, queryParam, values); }
            @Override public List<QueryParam> getUrlParams(String value) {
                return list(new QueryParam(queryParam, value));
            }
            @Override public List<String> parseValues(Map<String, String[]> queryString) {
                return parseStrings(queryString, queryParam);
            }
            @Override public Filters.StringAttribute.EqualsAnyOf parse(Map<String,String[]> queryString) {
                return new Filters.StringAttribute.EqualsAnyOf(attribute, parseValues(queryString));
            }
        }
    }


    // -------------------------------------------------------------------------------------------------------
    // Categories
    // -------------------------------------------------------------------------------------------------------

    // user-input category filter makes no sense as user won't be typing in category ids

    @Immutable
    public static final class CategoriesMultiSelect extends MultiSelectFilterDefinitionBase<String> {
        private static final String defaultQueryParam = "price";
        public CategoriesMultiSelect(String attribute, String value, String... values) { super(attribute, defaultQueryParam, value, values); }
        public CategoriesMultiSelect(String attribute, Collection<String> values) { super(attribute, defaultQueryParam, values); }
        public CategoriesMultiSelect(String attribute, String queryParam, String value, String... values) { super(attribute, queryParam, value, values); }
        public CategoriesMultiSelect(String attribute, String queryParam, Collection<String> values) { super(attribute, queryParam, values); }
        @Override public List<QueryParam> getUrlParams(String value) {
            return list(new QueryParam(queryParam, value));
        }
        @Override public List<String> parseValues(Map<String, String[]> queryString) {
            return parseStrings(queryString, queryParam);
        }
        @Override public Filters.CategoryAnyOf parse(Map<String,String[]> queryString) {
            return new Filters.CategoryAnyOf(parseValues(queryString));
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // Number
    // -------------------------------------------------------------------------------------------------------

    public static class NumberAttribute {
        @Immutable
        public static final class Value extends UserInputFilterOnAttributeDefinitionBase<Double> {
            public Value(String attribute) { super(attribute); }
            public Value(String attribute, String queryParam) { super(attribute, queryParam); }
            @Override public Double parseValue(Map<String, String[]> queryString) {
                return parseDouble(queryString, queryParam);
            }
            @Override public Filters.NumberAttribute.Equals parse(Map<String,String[]> queryString) {
                return new Filters.NumberAttribute.Equals(attribute, parseValue(queryString));
            }
        }
        @Immutable
        public static final class MultiSelect extends MultiSelectFilterDefinitionBase<Double> {
            public MultiSelect(String attribute, Double value, Double... values) { super(attribute, value, values); }
            public MultiSelect(String attribute, Collection<Double> values) { super(attribute, values); }
            public MultiSelect(String attribute, String queryParam, Double value, Double... values) { super(attribute, queryParam, value, values); }
            public MultiSelect(String attribute, String queryParam, Collection<Double> values) { super(attribute, queryParam, values); }
            @Override public List<QueryParam> getUrlParams(Double value) {
                return list(new QueryParam(queryParam, doubleToString(value)));
            }
            @Override public List<Double> parseValues(Map<String, String[]> queryString) {
                return parseDoubles(queryString, queryParam);
            }
            @Override public Filters.NumberAttribute.EqualsAnyOf parse(Map<String,String[]> queryString) {
                return new Filters.NumberAttribute.EqualsAnyOf(attribute, parseValues(queryString));
            }
        }
        @Immutable
        public static final class Range extends UserInputFilterOnAttributeDefinitionBase<com.google.common.collect.Range<Double>> {
            public Range(String attribute) { super(attribute); }
            public Range(String attribute, String queryParam) { super(attribute, queryParam); }
            @Override public com.google.common.collect.Range<Double> parseValue(Map<String, String[]> queryString) {
                return parseDoubleRange(queryString, queryParam);
            }
            @Override public Filters.NumberAttribute.Range parse(Map<String,String[]> queryString) {
                return new Filters.NumberAttribute.Range(attribute, parseValue(queryString));
            }
        }
        @Immutable
        public static final class RangesMultiSelect extends MultiSelectFilterDefinitionBase<com.google.common.collect.Range<Double>> {
            public RangesMultiSelect(String attribute, com.google.common.collect.Range<Double> value, com.google.common.collect.Range<Double>... values) {
                super(attribute, value, values);
            }
            public RangesMultiSelect(String attribute, Collection<com.google.common.collect.Range<Double>> values) {
                super(attribute, values);
            }
            public RangesMultiSelect(String attribute, String queryParam, com.google.common.collect.Range<Double> value, com.google.common.collect.Range<Double>... values) {
                super(attribute, queryParam, value, values);
            }
            public RangesMultiSelect(String attribute, String queryParam, Collection<com.google.common.collect.Range<Double>> values) {
                super(attribute, queryParam, values);
            }
            @Override public List<QueryParam> getUrlParams(com.google.common.collect.Range<Double> range) {
                return list(new QueryParam(queryParam, doubleRangeToString(range)));
            }
            @Override public List<com.google.common.collect.Range<Double>> parseValues(Map<String,String[]> queryString) {
                return parseDoubleRanges(queryString, queryParam);
            }
            @Override public Filters.NumberAttribute.Ranges parse(Map<String,String[]> queryString) {
                return new Filters.NumberAttribute.Ranges(attribute, parseValues(queryString));
            }
        }
    }


    // -------------------------------------------------------------------------------------------------------
    // Money
    // -------------------------------------------------------------------------------------------------------


    // -------------------------------------------------------------------------------------------------------
    // Price
    // -------------------------------------------------------------------------------------------------------

    public static class Price {
        private static final String defaultQueryParam = "price";
        @Immutable
        public static final class Value extends UserInputFilterDefinitionBase<BigDecimal> {
            public Value() { this(defaultQueryParam); }
            public Value(String queryParam) { super(queryParam); }
            @Override public BigDecimal parseValue(Map<String, String[]> queryString) {
                return parseDecimal(queryString, queryParam);
            }
            @Override public Filters.Price parse(Map<String,String[]> queryString) {
                return new Filters.Price(parseValue(queryString));
            }
        }
        @Immutable
        public static final class MultiSelect extends MultiSelectFilterDefinitionBase<BigDecimal> {
            public MultiSelect(String attribute, BigDecimal value, BigDecimal... values) { super(attribute, defaultQueryParam, value, values); }
            public MultiSelect(String attribute, Collection<BigDecimal> values) { super(attribute, defaultQueryParam, values); }
            public MultiSelect(String attribute, String queryParam, BigDecimal value, BigDecimal... values) { super(attribute, queryParam, value, values); }
            public MultiSelect(String attribute, String queryParam, Collection<BigDecimal> values) { super(attribute, queryParam, values); }
            @Override public List<QueryParam> getUrlParams(BigDecimal value) {
                return list(new QueryParam(queryParam, decimalToString(value)));
            }
            @Override public List<BigDecimal> parseValues(Map<String, String[]> queryString) {
                return parseDecimals(queryString, queryParam);
            }
            @Override public Filters.PriceAnyOf parse(Map<String,String[]> queryString) {
                return new Filters.PriceAnyOf(parseValues(queryString));
            }
        }
        @Immutable
        public static final class Range extends UserInputFilterDefinitionBase<com.google.common.collect.Range<BigDecimal>> {
            private final BigDecimal defaultMin;
            private final BigDecimal defaultMax;
            /** Default upper endpoint of the range if not specified. Effects {@link #parseValue} and {@link #parse}. Does nothing if set to null. */
            public BigDecimal getDefaultMin() { return defaultMin; }
            /** Default lower endpoint of the range if not specified. Effects {@link #parseValue} and {@link #parse}. Does nothing if set to null. */
            public BigDecimal getDefaultMax() { return defaultMax; }
            public Range() { this(defaultQueryParam, null, null); }
            public Range(String queryParam) { this(queryParam, null, null); }
            public Range(BigDecimal defaultMin, BigDecimal defaultMax) { this(defaultQueryParam, defaultMin, defaultMax); }
            public Range(String queryParam, BigDecimal defaultMin, BigDecimal defaultMax) {
                super(queryParam);
                this.defaultMin = defaultMin;
                this.defaultMax = defaultMax;
            }
            @Override public com.google.common.collect.Range<BigDecimal> parseValue(Map<String,String[]> queryString) {
                com.google.common.collect.Range<BigDecimal> range = parseDecimalRange(queryString, queryParam);
                if (defaultMin != null && !range.hasLowerBound())
                    range = range.intersection(Ranges.atLeast(defaultMin));
                if (defaultMin != null && !range.hasUpperBound())
                    range = range.intersection(Ranges.atMost(defaultMax));
                return range;
            }
            @Override public Filters.PriceRange parse(Map<String,String[]> queryString) {
                return new Filters.PriceRange(parseValue(queryString));
            }
        }
        @Immutable
        public static final class RangesMultiSelect extends MultiSelectFilterDefinitionBase<com.google.common.collect.Range<BigDecimal>> {
            public RangesMultiSelect(String attribute, com.google.common.collect.Range<BigDecimal> value, com.google.common.collect.Range<BigDecimal>... values) {
                super(attribute, defaultQueryParam, value, values);
            }
            public RangesMultiSelect(String attribute, Collection<com.google.common.collect.Range<BigDecimal>> values) {
                super(attribute, defaultQueryParam, values);
            }
            public RangesMultiSelect(String attribute, String queryParam, com.google.common.collect.Range<BigDecimal> value, com.google.common.collect.Range<BigDecimal>... values) {
                super(attribute, queryParam, value, values);
            }
            public RangesMultiSelect(String attribute, String queryParam, Collection<com.google.common.collect.Range<BigDecimal>> values) {
                super(attribute, queryParam, values);
            }
            @Override public List<QueryParam> getUrlParams(com.google.common.collect.Range<BigDecimal> range) {
                return list(new QueryParam(queryParam, decimalRangeToString(range)));
            }
            @Override public List<com.google.common.collect.Range<BigDecimal>> parseValues(Map<String, String[]> queryString) {
                return parseDecimalRanges(queryString, queryParam);
            }
            @Override public Filters.PriceRanges parse(Map<String,String[]> queryString) {
                return new Filters.PriceRanges(parseValues(queryString));
            }
        }
    }


    // -------------------------------------------------------------------------------------------------------
    // Date
    // -------------------------------------------------------------------------------------------------------



    // -------------------------------------------------------------------------------------------------------
    // Time
    // -------------------------------------------------------------------------------------------------------



    // -------------------------------------------------------------------------------------------------------
    // DateTime
    // -------------------------------------------------------------------------------------------------------
}