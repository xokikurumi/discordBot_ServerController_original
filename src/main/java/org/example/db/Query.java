package org.example.db;

public class Query {
    public static final String EARTH_QUAKE_INSERT = "INSERT into earth_quake(id,latitude, longitude, quake_time)VALUES('${0}', '${1}', '${2}', '${3}')";
    public static final String EARTH_QUAKE_SELECT = "SELECT id FROM earth_quake WHERE id = '${0}' AND latitude = '${1}' AND longitude = '${2}'";
    public static final String EARTH_QUAKE_SELECT_WHERE_TIME = "SELECT id, latitude, longitude, quake_time FROM earth_quake WHERE latitude = '${1}' AND longitude = '${2}' AND quake_time = '${3}'";
}
