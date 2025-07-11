package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.db.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class BaseDao<T> {
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public abstract void save(T entity) throws SQLException;
    public abstract T findById(int id) throws SQLException;
    public abstract List<T> findAll() throws SQLException;
    public abstract void update(T entity) throws SQLException;
    public abstract void delete(int id) throws SQLException;
}
