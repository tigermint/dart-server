package com.ssh.dartserver;

import com.google.common.base.CaseFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;
    private Map<String, String> pkNames;

    @Override
    public void afterPropertiesSet() {
        final Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();

        tableNames = entities.stream()
            .filter(e -> isEntity(e))
            .map(this::getTableName)
            .collect(Collectors.toList());

        // 각 Entity의 Id에 @Column의 name value를 가져옴
        pkNames = entities.stream()
            .filter(e -> {
                final String tableName = getTableName(e);
                return tableNames.contains(tableName);
            })
            .flatMap(e -> Arrays.stream(e.getJavaType().getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(field -> {
                    Column column = field.getAnnotation(Column.class);
                    String idName = column.name().isBlank() ? toSnakeCase(field.getName()) : column.name();

                    return Map.entry(getTableName(e), idName);
                }))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String toSnakeCase(final String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (final String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN " + pkNames.get(tableName) +  " RESTART WITH 1").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private boolean isEntity(final EntityType<?> e) {
        return null != e.getJavaType().getAnnotation(Entity.class);
    }

    private boolean hasTableAnnotation(final EntityType<?> e) {
        return null != e.getJavaType().getAnnotation(Table.class);
    }

    private String getTableName(EntityType<?> e) {
        String tableName = "";
        if (hasTableAnnotation(e)) {
            tableName = e.getJavaType().getAnnotation(Table.class).name();
        }
        return tableName.isBlank()
            ? toSnakeCase(e.getName())
            : tableName;
    }
}
