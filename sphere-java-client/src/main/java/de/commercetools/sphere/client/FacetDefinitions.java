package de.commercetools.sphere.client;

import com.google.common.collect.Range;
import de.commercetools.internal.FacetOnAttributeDefinitionBase;
import net.jcip.annotations.Immutable;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static de.commercetools.internal.util.SearchUtil.*;
import static de.commercetools.internal.util.QueryStringParser.*;

public class FacetDefinitions {

    // -------------------------------------------------------------------------------------------------------
    // String
    // -------------------------------------------------------------------------------------------------------

    public static class StringAttribute {
        @Immutable
        public static final class Terms extends FacetOnAttributeDefinitionBase {
            public Terms(String attribute) { super(attribute); }
            public Terms(String attribute, String queryParam) { super(attribute, queryParam); }
            public Facets.StringAttribute.TermsMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.StringAttribute.TermsMultiSelect(attribute, parseStrings(queryString, queryParam));
            }
        }
        @Immutable
        public static final class Values extends FacetOnAttributeDefinitionBase {
            private List<String> values;
            public Values(String attribute, String value, String... values) { this(attribute, list(value, values)); }
            public Values(String attribute, Collection<String> values) { super(attribute); this.values = toList(values); }
            public Values(String attribute, String queryParam, String value, String... values) { this(attribute, queryParam, list(value, values)); }
            public Values(String attribute, String queryParam, Collection<String> values) { super(attribute, queryParam); this.values = toList(values); }
            public Facets.StringAttribute.ValuesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.StringAttribute.ValuesMultiSelect(attribute, parseStrings(queryString, queryParam), values);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // Number
    // -------------------------------------------------------------------------------------------------------

    public static class NumberAttribute {
        @Immutable
        public static final class Terms extends FacetOnAttributeDefinitionBase {
            public Terms(String attribute) { super(attribute); }
            public Terms(String attribute, String queryParam) { super(attribute, queryParam); }
            public Facets.NumberAttribute.TermsMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.NumberAttribute.TermsMultiSelect(attribute, parseDoubles(queryString, queryParam));
            }
        }
        @Immutable
        public static final class Values extends FacetOnAttributeDefinitionBase {
            private List<Double> values;
            public Values(String attribute, Double value, Double... values) { this(attribute, list(value, values)); }
            public Values(String attribute, Collection<Double> values) { super(attribute); this.values = toList(values); }
            public Values(String attribute, String queryParam, Double value, Double... values) { this(attribute, queryParam, list(value, values)); }
            public Values(String attribute, String queryParam, Collection<Double> values) { super(attribute, queryParam); this.values = toList(values); }
            public Facets.NumberAttribute.ValuesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.NumberAttribute.ValuesMultiSelect(attribute, parseDoubles(queryString, queryParam), values);
            }
        }
        @Immutable
        public static final class Ranges extends FacetOnAttributeDefinitionBase {
            private List<Range<Double>> ranges;
            public Ranges(String attribute, Range<Double> range, Range<Double>... ranges) { this(attribute, list(range, ranges)); }
            public Ranges(String attribute, Collection<Range<Double>> ranges) { super(attribute); this.ranges = toList(ranges); }
            public Ranges(String attribute, String queryParam, Range<Double> range, Range<Double>... ranges) { this(attribute, queryParam, list(range, ranges)); }
            public Ranges(String attribute, String queryParam, Collection<Range<Double>> ranges) { super(attribute, queryParam); this.ranges = toList(ranges); }
            public Facets.NumberAttribute.RangesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.NumberAttribute.RangesMultiSelect(attribute, parseDoubleRanges(queryString, queryParam), ranges);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // Money
    // -------------------------------------------------------------------------------------------------------

    public static class MoneyAttribute {
        @Immutable
        public static final class Terms extends FacetOnAttributeDefinitionBase {
            public Terms(String attribute) { super(attribute); }
            public Terms(String attribute, String queryParam) { super(attribute, queryParam); }
            public Facets.MoneyAttribute.TermsMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.MoneyAttribute.TermsMultiSelect(attribute, parseDoubles(queryString, queryParam));
            }
        }
        @Immutable
        public static final class Values extends FacetOnAttributeDefinitionBase {
            private List<Double> values;
            public Values(String attribute, Double value, Double... values) { this(attribute, list(value, values)); }
            public Values(String attribute, Collection<Double> values) { super(attribute); this.values = toList(values); }
            public Values(String attribute, String queryParam, Double value, Double... values) { this(attribute, queryParam, list(value, values)); }
            public Values(String attribute, String queryParam, Collection<Double> values) { super(attribute, queryParam); this.values = toList(values); }
            public Facets.MoneyAttribute.ValuesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.MoneyAttribute.ValuesMultiSelect(attribute, parseDoubles(queryString, queryParam), values);
            }
        }
        @Immutable
        public static final class Ranges extends FacetOnAttributeDefinitionBase {
            private List<Range<Double>> ranges;
            public Ranges(String attribute, Range<Double> range, Range<Double>... ranges) { this(attribute, list(range, ranges)); }
            public Ranges(String attribute, Collection<Range<Double>> ranges) { super(attribute); this.ranges = toList(ranges); }
            public Ranges(String attribute, String queryParam, Range<Double> range, Range<Double>... ranges) { this(attribute, queryParam, list(range, ranges)); }
            public Ranges(String attribute, String queryParam, Collection<Range<Double>> ranges) { super(attribute, queryParam); this.ranges = toList(ranges); }
            public Facets.MoneyAttribute.RangesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.MoneyAttribute.RangesMultiSelect(attribute, parseDoubleRanges(queryString, queryParam), ranges);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------
    // Date
    // -------------------------------------------------------------------------------------------------------

    public static class DateAttribute {
        public static final class Terms extends FacetOnAttributeDefinitionBase {
            public Terms(String attribute) { super(attribute); }
            public Terms(String attribute, String queryParam) { super(attribute, queryParam); }
            public Facets.DateAttribute.TermsMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.DateAttribute.TermsMultiSelect(attribute, parseDates(queryString, queryParam));
            }
        }
        @Immutable
        public static final class Values extends FacetOnAttributeDefinitionBase {
            private List<LocalDate> values;
            public Values(String attribute, LocalDate value, LocalDate... values) { this(attribute, list(value, values)); }
            public Values(String attribute, Collection<LocalDate> values) { super(attribute); this.values = toList(values); }
            public Values(String attribute, String queryParam, LocalDate value, LocalDate... values) { this(attribute, queryParam, list(value, values)); }
            public Values(String attribute, String queryParam, Collection<LocalDate> values) { super(attribute, queryParam); this.values = toList(values); }
            public Facets.DateAttribute.ValuesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.DateAttribute.ValuesMultiSelect(attribute, parseDates(queryString, queryParam), values);
            }
        }
        @Immutable
        public static final class Ranges extends FacetOnAttributeDefinitionBase {
            private List<Range<LocalDate>> ranges;
            public Ranges(String attribute, Range<LocalDate> range, Range<LocalDate>... ranges) { this(attribute, list(range, ranges)); }
            public Ranges(String attribute, Collection<Range<LocalDate>> ranges) { super(attribute); this.ranges = toList(ranges); }
            public Ranges(String attribute, String queryParam, Range<LocalDate> range, Range<LocalDate>... ranges) { this(attribute, queryParam, list(range, ranges)); }
            public Ranges(String attribute, String queryParam, Collection<Range<LocalDate>> ranges) { super(attribute, queryParam); this.ranges = toList(ranges); }
            public Facets.DateAttribute.RangesMultiSelect parse(Map<String,String[]> queryString) {
                return new Facets.DateAttribute.RangesMultiSelect(attribute, parseDateRanges(queryString, queryParam), ranges);
            }
        }
    }
}
