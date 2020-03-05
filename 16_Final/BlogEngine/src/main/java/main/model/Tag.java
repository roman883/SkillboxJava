package main.model;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;
}