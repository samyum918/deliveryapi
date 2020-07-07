package com.springboot.deliveryapi.model;

import com.springboot.deliveryapi.model.enums.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "distance")
    private Integer distance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
