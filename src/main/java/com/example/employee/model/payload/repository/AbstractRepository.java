package com.example.employee.model.payload.repository;

import java.util.List;

public interface AbstractRepository {

    default StringBuilder createSortAndOrderQuery(String sortType, List<String> sortList){
        StringBuilder sqlQuery = new StringBuilder();

        if(sortType != null && sortType.equalsIgnoreCase("desc")) {
            sortType = " DESC";
        }else{
            sortType = " ASC";
        }

        sqlQuery.append(" ORDER BY ");
        for (String sortBy : sortList) {
            sqlQuery.append(sortBy)
                    .append(" ")
                    .append(sortType)
                    .append(", ")
            ;
        }
        //remove the last ", "
        //sqlQuery.deleteCharAt(sqlQuery.length() - 2);
        sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length());

        return sqlQuery;
    }

}
