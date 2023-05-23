package com.dqt.token.dtos.request;

import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeDTO {
    private Long id;

    private String fullName;

    private Date year;

    private String email;

    private String address;


}
