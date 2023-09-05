package com.mixzing.derby;

import java.util.List;

public interface MixzingAppSql
{

    List<String> dropTableStatements();

    List<String> createTableStatements();

    List<String> clearTableStatements();

}