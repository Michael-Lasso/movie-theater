package com.jpmc.theater.domain;

import lombok.*;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
@ToString
public class Customer {

    private String id;
    private String name;

}