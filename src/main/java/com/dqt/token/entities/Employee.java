package com.dqt.token.entities;


import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "employees")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private Date year;

    @Column(unique = true)
    private String email;

    private String address;

    private Boolean enabled;

}
