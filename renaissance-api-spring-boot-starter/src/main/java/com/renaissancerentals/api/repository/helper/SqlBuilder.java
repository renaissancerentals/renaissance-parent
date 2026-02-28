package com.renaissancerentals.api.repository.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SqlBuilder {
    private final String baseSql;

    private final StringBuilder where = new StringBuilder();
    private final Map<String, Object> params = new LinkedHashMap<>();

    public SqlBuilder where(String condition,String paramName,Object value){
        if (value != null) {
            if (!where.isEmpty()) {
                where.append(" AND ");
            }
            where.append(condition);
            params.put(paramName,value);
        }
        return this;
    }

    public String sql(){
        if (where.isEmpty()) {
            return baseSql;
        }
        return baseSql + " WHERE " + where;
    }

    public Map<String, Object> params(){
        return Map.copyOf(params);
    }
}
