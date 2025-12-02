package com.aajumaharjan.demofeatures.auth.model;

import jakarta.persistence.Entity;

@Entity
public class Role extends BaseEntity {
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
