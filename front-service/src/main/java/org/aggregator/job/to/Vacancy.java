package org.aggregator.job.to;

import lombok.*;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class Vacancy {
    private String title;
    private String salary;
    private String location;
    private String companyName;
    @EqualsAndHashCode.Exclude
    private String siteName;
    @EqualsAndHashCode.Exclude
    private String link;
}
