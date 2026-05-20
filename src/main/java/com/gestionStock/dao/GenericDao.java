package com.gestionStock.dao;

import com.gestionStock.util.DatabaseConnection;
import com.gestionStock.util.SqlUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GenericDao<T> {

    private final Class<T> clazz;
    private final String tableName;

    public GenericDao(Class<T> clazz) {
        this.clazz = clazz;
        this.tableName = SqlUtils.tableName(clazz);
    }
    
    public T save(T entity) throws Exception {
        List<Field> fields = getFieldsWithoutId();

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            columns.append(getColumnName(fields.get(i)));
            values.append("?");

            if (i < fields.size() - 1) {
                columns.append(", ");
                values.append(", ");
            }
        }

        String sql = "INSERT INTO " + tableName +
                " (" + columns + ") VALUES (" + values + ") RETURNING id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            setPreparedStatementValues(ps, fields, entity);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    setId(entity, rs.getLong("id"));
                }
            }
        }

        return entity;
    }

    public T findById(Long id) throws Exception {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    public List<T> findAll() throws Exception {
        List<T> result = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName + " ORDER BY id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        }

        return result;
    }

    public void update(T entity) throws Exception {
        List<Field> fields = getFieldsWithoutId();

        StringBuilder setPart = new StringBuilder();

        for (int i = 0; i < fields.size(); i++) {
            setPart.append(getColumnName(fields.get(i))).append(" = ?");

            if (i < fields.size() - 1) {
                setPart.append(", ");
            }
        }

        String sql = "UPDATE " + tableName + " SET " + setPart + " WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            setPreparedStatementValues(ps, fields, entity);
            ps.setLong(fields.size() + 1, getId(entity));

            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws Exception {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private List<Field> getFieldsWithoutId() {
        List<Field> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.getName().equals("id")) {
                fields.add(field);
            }
        }

        return fields;
    }

    private String getColumnName(Field field) {
        return SqlUtils.toSnakeCase(field.getName());
    }

    private void setPreparedStatementValues(
            PreparedStatement ps,
            List<Field> fields,
            T entity
    ) throws Exception {

        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            field.setAccessible(true);

            Object value = field.get(entity);

            if (value instanceof Enum<?>) {
                ps.setObject(i + 1, value.toString(), Types.OTHER);
            } else {
                ps.setObject(i + 1, value);
            }
        }
    }

    private T mapResultSet(ResultSet rs) throws Exception {
        T object = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String columnName = getColumnName(field);
            Object value = getValueByType(rs, columnName, field.getType());

            field.set(object, value);
        }

        return object;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object getValueByType(
            ResultSet rs,
            String columnName,
            Class<?> type
    ) throws SQLException {

        if (type == Long.class) {
            long value = rs.getLong(columnName);
            return rs.wasNull() ? null : value;
        }

        if (type == String.class) {
            return rs.getString(columnName);
        }

        if (type == BigDecimal.class) {
            return rs.getBigDecimal(columnName);
        }

        if (type == LocalDate.class) {
            Date date = rs.getDate(columnName);
            return date == null ? null : date.toLocalDate();
        }

        if (type.isEnum()) {
            String value = rs.getString(columnName);
            return value == null ? null : Enum.valueOf((Class<Enum>) type, value);
        }

        return rs.getObject(columnName);
    }

  
    

    public T getPlusAncienArticle(Long articleId, List<Long> excludedIds) throws Exception {
        String sql = "SELECT * FROM " + tableName +
                " WHERE article_id = ? AND type_mouvement = 'ENTREE'" +
                " AND id <> ALL (?::bigint[])" +
                " ORDER BY date_mouvement ASC, id ASC LIMIT 1";

        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, articleId);
            ps.setArray(2, toBigIntArray(con, excludedIds));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    public T getPlusRecentArticle(Long articleId, List<Long> excludedIds) throws Exception {
        String sql = "SELECT * FROM " + tableName +
                " WHERE article_id = ? AND type_mouvement = 'ENTREE'" +
                " AND id <> ALL (?::bigint[])" +
                " ORDER BY date_mouvement DESC, id DESC LIMIT 1";

        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, articleId);
            ps.setArray(2, toBigIntArray(con, excludedIds));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }

        return null;
    }

    private Array toBigIntArray(Connection con, List<Long> values) throws SQLException {
        List<Long> safeValues = values == null ? List.of() : values;
        return con.createArrayOf("bigint", safeValues.toArray());
    }


    private Long getId(T entity) throws Exception {
        Field field = clazz.getDeclaredField("id");
        field.setAccessible(true);
        return (Long) field.get(entity);
    }

    private void setId(T entity, Long id) throws Exception {
        Field field = clazz.getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
