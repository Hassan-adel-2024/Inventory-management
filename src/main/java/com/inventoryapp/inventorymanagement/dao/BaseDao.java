package com.inventoryapp.inventorymanagement.dao;

import java.sql.SQLException;
import java.util.List;

public interface BaseDao <T>{
    public abstract void save(T entity) throws SQLException;
    public abstract T findById(int id) throws SQLException;
    public abstract List<T> findAll() throws SQLException;
    public abstract void update(T entity) throws SQLException;
    public abstract void delete(int id) throws SQLException;
}
