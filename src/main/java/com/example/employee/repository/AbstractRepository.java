package com.example.employee.repository;

import java.util.List;

public interface AbstractRepository {

    default StringBuilder createSortAndOrderQuery(String sortType, List<String> sortBy){
        StringBuilder sqlQuery = new StringBuilder();

        if(sortType != null && sortType.equalsIgnoreCase("desc")) {
            sortType = " DESC";
        }else{
            sortType = " ASC";
        }
        if(sortBy != null) {
            sqlQuery.append(" ORDER BY ");
            for (int i = 0; i < sortBy.size(); i++) {
                if(i != 0) {
                    sqlQuery.append(", ");
                }
                sqlQuery.append(sortBy.get(i)).append(" ").append(sortType);
            }
        }
        return sqlQuery;
    }

}
