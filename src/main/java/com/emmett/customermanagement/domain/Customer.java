package com.emmett.customermanagement.domain;

import com.emmett.customermanagement.domain.enumeration.Gender;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "customer")
@ApiModel(description = "Represents a Customer")
public class Customer implements Serializable {

    @ApiModelProperty(notes = "Omit for POST")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @ApiModelProperty(notes = "Enter in form YYYY-MM-DD")
    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ApiModelProperty(notes = "This is an optional field allowing an external customer id to be specified. This can avoid accidentally adding the same user twice. Omit if not needed")
    @Size(max = 128)
    @Column(name = "external_customer_id", length = 128, unique = true)
    private String externalCustomerId;

    public Customer(){}

    public Customer(String name, Gender gender, String externalCustomerId, LocalDate birthDate){
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.externalCustomerId = externalCustomerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getExternalCustomerId() {
        return externalCustomerId;
    }

    public void setExternalCustomerId(String externalCustomerId) {
        this.externalCustomerId = externalCustomerId;
    }

    /**
     * only comparing db id here!!
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }
}
