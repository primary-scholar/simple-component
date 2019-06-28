package com.mimu.simple;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * author: mimu
 * date: 2019/1/11
 */
public abstract class BaseRepository<T> implements RowMapper<T>, InitializingBean {

    protected DataSource writeDataSource;
    protected DataSource readDataSource;
    protected JdbcTemplate writeJdbcTemplate;
    protected JdbcTemplate readJdbcTemplate;
    protected NamedParameterJdbcTemplate writeNamedParameterJdbcTemplate;
    protected NamedParameterJdbcTemplate readNamedParameterJdbcTemplate;


    @Override
    public void afterPropertiesSet() throws Exception {
        init(writeDataSource, readDataSource);
    }

    public abstract void init(DataSource write, DataSource read);

    public void setWriteDataSource(DataSource dataSource) {
        this.writeDataSource = dataSource;
        this.writeJdbcTemplate = new JdbcTemplate(dataSource);
        this.writeNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(writeJdbcTemplate);
    }

    public void setReadDataSource(DataSource dataSource) {
        this.readDataSource = dataSource;
        this.readJdbcTemplate = new JdbcTemplate(dataSource);
        this.readNamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(readJdbcTemplate);
    }

    public JdbcTemplate getWriteJdbcTemplate() {
        return writeJdbcTemplate;
    }

    public JdbcTemplate getReadJdbcTemplate() {
        return readJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getWriteNamedParameterJdbcTemplate() {
        return writeNamedParameterJdbcTemplate;
    }

    public NamedParameterJdbcTemplate getReadNamedParameterJdbcTemplate() {
        return readNamedParameterJdbcTemplate;
    }
}
