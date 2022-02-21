package org.aggregator.job.to;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@EqualsAndHashCode
@Getter
public class Vacancy {
    private final String title;
    private final String salary;
    private final String location;
    private final String companyName;
    @EqualsAndHashCode.Exclude
    private final String siteName;
    @EqualsAndHashCode.Exclude
    private final String link;
}
